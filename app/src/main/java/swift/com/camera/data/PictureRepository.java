package swift.com.camera.data;

import android.support.annotation.NonNull;

import javax.inject.Inject;
import javax.inject.Singleton;

import static dagger.internal.Preconditions.checkNotNull;

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
    public void getAdapterImage(@NonNull GetPictureCallBack getCallBace, String pathName,
                                int width, int height) {
        mLocalPictureSource.getAdapterImage(getCallBace, pathName, width, height);
    }

    @Override
    public void loadPicture(@NonNull LoadPictureCallBack loadCallBack) {
        checkNotNull(loadCallBack);
        mLocalPictureSource.loadPicture(loadCallBack);
    }



}
