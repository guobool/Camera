package swift.com.camera.ui.activity;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.provider.MediaStore;

import swift.com.camera.data.local.LocalPictureSource;

/**
 * Created by junnikokuki on 2017/4/12.
 */

public class CameraSupport implements CameraContract.Support {

    private final Context mContext;

    public CameraSupport(Context context) {
        mContext = context;
    }

    @Override
    public Bitmap getLastPhoto() {
        final String[] imageColumns = { MediaStore.Images.Media._ID, MediaStore.Images.Media.DATA };
        final String imageOrderBy = MediaStore.Images.Media._ID + " DESC";
        ContentResolver contentReslver = mContext.getContentResolver();
        Cursor imageCursor = contentReslver.query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, imageColumns, null, null, imageOrderBy);
        if (imageCursor.moveToFirst()) {
            String fullPath = imageCursor.getString(imageCursor.getColumnIndex(MediaStore.Images.Media.DATA));
            imageCursor.close();
            Bitmap bitmap = LocalPictureSource.getBitmapFromFile(fullPath, 120 , 120);
            return bitmap;
        }
        return null;
    }
}
