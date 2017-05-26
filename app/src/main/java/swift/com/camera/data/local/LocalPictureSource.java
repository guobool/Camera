package swift.com.camera.data.local;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.util.LruCache;
import java.io.File;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import javax.inject.Inject;
import javax.inject.Singleton;

import swift.com.camera.data.PictureBean;
import swift.com.camera.data.PictureDataSource;


/**
 * Created by bool on 17-4-17.
 */
@Singleton
public class LocalPictureSource implements PictureDataSource {
    private Context mContext;
    private LruCache<String, Bitmap> mMemoryCache;
    private ExecutorService mImageThreadPool = Executors.newFixedThreadPool(1);
    private ArrayList<PictureBean> mPictureList;
    @Inject
    public LocalPictureSource(Context context){
        mContext = context;
        // 获应用程序的最大内存
        final int maxMemory = (int)(Runtime.getRuntime().maxMemory() / 1024);
        //使用应用内存的1/4来存储图片
        final int catchSize = maxMemory / 4;
        mMemoryCache = new LruCache<String, Bitmap>(catchSize){
            // 获取每张图片大小
            @Override
            protected int sizeOf(String key, Bitmap bitmap){
                return bitmap.getRowBytes() * bitmap.getHeight() / 1024;
            }
        };
        mPictureList = new ArrayList<PictureBean>();
    }


    @Override
    public void loadPicture(@NonNull final LoadPictureCallBack loadCallBack){

        Handler handler = new Handler();
        handler.post(new Runnable() {
            @Override
            public void run() {
                ContentResolver mContentReslver = mContext.getContentResolver();
                Cursor mCursor = mContentReslver.query(LOCAL_IMAGE_URI, null, LOCAL_QUERY_CRITERIA,
                        LOCAL_QUERY_VALUE, LOCAL_QUERY_ATTR);
                if(mCursor != null){
                    PictureBean mPicture;
                    while(mCursor.moveToNext()){
                        // 获取图片路径
                        String path = mCursor.getString(mCursor.getColumnIndex(MediaStore.Images.Media.DATA));
                        mPicture = new PictureBean(path);
                        mPictureList.add(mPicture);
                    }
                    mCursor.close();
                }
                if(mPictureList.size() != 0) {
                    loadCallBack.onPictureLoaded(mPictureList);
                }
            }
        });

    }

    /**
     * @param pathName 图片的完整路径名
     * @param width 图片的宽度
     * @param height 图片的高度
     * @return Bitmap对象
     * 当width或这height不给时，则保持原图大小
     *
     *
     * *注意：LoaclPictureSource负责本地资源的加载，RemotePictureSource负责远程的数据加载。
     * PictureRepository则是对两种存取方式的再次封装，使上层不用关心数据的存取方式。
     *
     * 之所以在PictureRepository和LoaclPictureSource中的函数都要传入LoadPictureCallBack对象，
     * 这是由于两层共同使用同一接口PictureDataSource造成的。如果不使用同一接口，可以只在PictureRepository
     * 中完成回调即可。异步处理也需要放到PictureRepository中，底层只需要完成这存取返回即可。这样使处理的使传递
     * 的参数明显减少，回到的层级变少。
     *
     * 之所以三个类使用同一接口，可能是为了减少接口的数量和定义，同时在上层需要明确从某处加载时，可以直接使用该类，而不需
     * 要改动调用接口和处理过程。不过这可以通过传递标志值来弥补。因为既然结构已经定义好之后，各层之间的调用是不能随便越级
     * 的，论乱的调用只会使程序无法维护。
     *
     * 又或者，这只是个人习惯的一种选择，毕竟，程序只是思想的表达而已。也只有todo-mvp的构建者才知道自己当初为什么这样抽取
     * 接口了。个人所思，略作参考：)
     */
    @Override
    public void getAdapterImage(@NonNull final GetPictureCallBack getCallBack,
                                final String pathName, final int width, final int height){
        Handler handler = new Handler();
        handler.post(new Runnable() {
            @Override
            public void run() {
                Bitmap bitmap = mMemoryCache.get(pathName);
                if(bitmap == null){
                    //根据传入的宽高获取图片，对图片进行缩放
                    bitmap = getBitmapFromFile(pathName, width , height);
                    //将图片加入到内存缓存
                    addBitmapToMemoryCache(pathName, bitmap);
                    if(bitmap != null){
                        getCallBack.onPictureGeted(bitmap);
                    }else{
                        getCallBack.onGetFailed();
                    }

                }
            }
        });
    }


    /**
     * @param pathName 图片完整路径
     * @param bitmap 要缓存的图片
     */
    private void addBitmapToMemoryCache(String pathName, Bitmap bitmap) {
        if (mMemoryCache.get(pathName) == null && bitmap != null) {
            mMemoryCache.put(pathName, bitmap);
        }
    }


    /**
     * 根据View(主要是ImageView)的宽和高来计算Bitmap缩放比例。当viewWidth和viewHeight之一是0，则不缩放
     * @param options 图片的
     * @param viewWidth 图片宽度
     * @param viewHeight 图片高度
     */
    public static int computeScale(BitmapFactory.Options options, int viewWidth, int viewHeight){
        int inSampleSize = 1;
        if(viewWidth == 0 || viewWidth == 0){
            return inSampleSize;
        }
        int bitmapWidth = options.outWidth;
        int bitmapHeight = options.outHeight;

        //假如Bitmap的宽度或高度大于我们设定图片的View的宽高，则计算缩放比例
        if(bitmapWidth > viewWidth || bitmapHeight > viewWidth){
            int widthScale = Math.round((float) bitmapWidth / (float) viewWidth);
            int heightScale = Math.round((float) bitmapHeight / (float) viewWidth);

            //为了保证图片不缩放变形，我们取宽高比例最小的那个
            inSampleSize = widthScale < heightScale ? widthScale : heightScale;
        }
        return inSampleSize;
    }

    public static Bitmap getBitmapFromFile(String pathName, int width, int height) {
        File dst = new File(pathName);
        if (null != dst && dst.exists()) {
            BitmapFactory.Options opts = null;
            if (width > 0 && height > 0) {
                opts = new BitmapFactory.Options();
                //设置inJustDecodeBounds为true后，decodeFile并不分配空间，此时计算原始图片的长度和宽度
                opts.inJustDecodeBounds = true;
                BitmapFactory.decodeFile(dst.getPath(), opts);
                // 计算图片缩放比例
                final int minSideLength = Math.min(width, height);
                //opts.inSampleSize = computeScale(opts, width, height);
                opts.inSampleSize = computeSampleSize(opts, -1, width * height);
                opts.inJustDecodeBounds = false;
                opts.inInputShareable = true;
                opts.inPurgeable = true;
            }
            try {
                return BitmapFactory.decodeFile(dst.getPath(), opts);
            } catch (OutOfMemoryError e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public static int computeSampleSize(BitmapFactory.Options options, int minSideLength, int maxNumOfPixels) {
        int initialSize = computeInitialSampleSize(options, minSideLength, maxNumOfPixels);
        int roundedSize;
        if (initialSize <= 8) {
            roundedSize = 1;
            while (roundedSize < initialSize) {
                roundedSize <<= 1;
            }
        } else {
            roundedSize = (initialSize + 7) / 8 * 8;
        }
        return roundedSize;
    }

    private static int computeInitialSampleSize(BitmapFactory.Options options, int minSideLength, int maxNumOfPixels) {
        double w = options.outWidth;
        double h = options.outHeight;
        int lowerBound = (maxNumOfPixels == -1) ? 1 : (int) Math.ceil(Math.sqrt(w * h / maxNumOfPixels));
        int upperBound = (minSideLength == -1) ? 128 : (int) Math.min(Math.floor(w / minSideLength), Math.floor(h / minSideLength));
        if (upperBound < lowerBound) {
            // return the larger one when there is no overlapping zone.
            return lowerBound;
        }
        if ((maxNumOfPixels == -1) && (minSideLength == -1)) {
            return 1;
        } else if (minSideLength == -1) {
            return lowerBound;
        } else {
            return upperBound;
        }
    }
}
