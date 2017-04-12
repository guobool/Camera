package swift.com.camera;

import android.content.Context;
import android.support.annotation.NonNull;
import swift.com.camera.data.PictureRepository;
import static com.squareup.haha.guava.base.Joiner.checkNotNull;

/**
 * Created by bool on 17-4-12.
 */

public class Injection {
    public static PictureRepository providePictureRepository(@NonNull Context context) {
        checkNotNull(context);
        return new PictureRepository();
    }
}
