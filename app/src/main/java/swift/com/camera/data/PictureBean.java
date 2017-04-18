package swift.com.camera.data;

import android.media.Image;

/**
 * Created by bool on 17-4-11.
 */

public final class PictureBean {
    private String mImagePath;

    public PictureBean(String path){
        mImagePath = path;
    }
    public String getmImagePath(){
        return mImagePath;
    }
    public Image getImage(){
        return null;
    }
}
