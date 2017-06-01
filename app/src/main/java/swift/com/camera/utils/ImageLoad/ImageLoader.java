package swift.com.camera.utils.ImageLoad;

import android.graphics.Bitmap;
import android.widget.ImageView;

/**
 * Created by bool on 17-5-31.
 */

public interface ImageLoader {

    void getAdapteImage(String fullName, ImageView imageView);

    void getOriginalImage(String fullName, ImageView imageView);

    Bitmap getSpecifiedSizeImage(String fullPath, int i, int i1);

    void getSpecifiedSizeImage(String fullName, int width, int height,
                                 ImageView imageView);
}
