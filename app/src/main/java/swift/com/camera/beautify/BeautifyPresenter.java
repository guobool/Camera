package swift.com.camera.beautify;

import android.graphics.Bitmap;

import java.util.List;

import javax.inject.Inject;
import swift.com.camera.data.PictureRepository;

/**
 * Created by bool on 17-4-19.
 */

public class BeautifyPresenter implements BeautifyContract.Presenter{
    private PictureRepository mPictureReposotory;
    private BeautifyContract.View mBeautifyView;

    @Inject
    public BeautifyPresenter(PictureRepository repository, BeautifyContract.View beautifyView){
        mPictureReposotory = repository;
        mBeautifyView = beautifyView;
    }
}
