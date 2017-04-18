package swift.com.camera.data;

import android.graphics.Bitmap;

import java.util.ArrayList;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * Created by bool on 17-4-12.
 */
@Singleton
public class PictureRepository implements PictureDataSource {
    private final PictureDataSource mLocalPictureSource;
    @Inject
    PictureRepository(@Local PictureDataSource localDataSource) {
        mLocalPictureSource = localDataSource;
    }

    @Override
    public ArrayList<PictureBean> getPicture() {
        return mLocalPictureSource.getPicture();
    }

    @Override
    public Bitmap getAdapterImage(String pathName, int width, int height) {
        return mLocalPictureSource.getAdapterImage(pathName, width, height);
    }
}
