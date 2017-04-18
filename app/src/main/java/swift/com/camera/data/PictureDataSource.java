package swift.com.camera.data;

import android.graphics.Bitmap;

import java.util.ArrayList;

/**
 * Created by bool on 17-4-18.
 */

public interface PictureDataSource {
    ArrayList<PictureBean> getPicture();

    Bitmap getAdapterImage(String pathName, int width, int height);
}
