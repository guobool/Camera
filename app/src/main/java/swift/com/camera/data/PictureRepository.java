package swift.com.camera.data;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import java.util.ArrayList;
import javax.inject.Inject;
import javax.inject.Singleton;
import static dagger.internal.Preconditions.checkNotNull;

/**
 * Created by bool on 17-4-12.
 */
@Singleton
public class PictureRepository implements PictureDataSource {
    private Context mContext;
    private ArrayList<PictureBean> mPictureList;
    @Inject
    public PictureRepository(@NonNull Context context) {
        mContext = context;
        mPictureList = new ArrayList<PictureBean>();
    }

    @Override
    public void loadPicture(@NonNull final LoadPictureCallBack loadCallBack) {
        checkNotNull(loadCallBack);
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
}
