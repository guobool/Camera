package swift.com.camera.data.local;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.LruCache;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
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
    public ArrayList<PictureBean> getPicture(){
        Uri mImageUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        ContentResolver mContentReslver = mContext.getContentResolver();
        Cursor mCursor = mContentReslver.query(mImageUri, null,
                MediaStore.Images.Media.MIME_TYPE + "=? or " + MediaStore.Images.Media.MIME_TYPE + "=?",
                new String[] { "image/jpeg", "image/png" }, MediaStore.Images.Media.DATE_MODIFIED);
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
        return mPictureList;
    }

    /**
     * @param pathName 图片的完整路径名
     * @param width 图片的宽度
     * @param height 图片的高度
     * @return Bitmap对象
     * 当width或这height不给时，则保持原图大小
     */
    @Override
    public Bitmap getAdapterImage(final String pathName, final int width, final int height){
        Bitmap bitmap = mMemoryCache.get(pathName);

        if(bitmap == null){
            //根据传入的宽高获取图片，对图片进行了缩放
            bitmap = getBitmapFromFile(pathName, width , height);
            //将图片加入到内存缓存
            addBitmapToMemoryCache(pathName, bitmap);
        }
        return bitmap;
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
                opts.inSampleSize = computeScale(opts, width, height);
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

}
