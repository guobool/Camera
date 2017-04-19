package swift.com.camera.data;

import java.io.Serializable;

/**
 * Created by bool on 17-4-11.
 */

public final class PictureBean implements Serializable {
    private String mImagePath;

    public PictureBean(String path){
        mImagePath = path;
    }
    public String getmImagePath(){
        return mImagePath;
    }
}
