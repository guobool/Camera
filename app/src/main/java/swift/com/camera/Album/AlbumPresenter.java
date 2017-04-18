package swift.com.camera.Album;

import android.graphics.Bitmap;
import java.util.List;

import javax.inject.Inject;

import swift.com.camera.data.PictureBean;
import swift.com.camera.data.PictureRepository;

/**
 * Created by bool on 17-4-14.
 */

class AlbumPresenter implements AlbumContract.Presenter {
    private PictureRepository mPictureReposotory;
    private AlbumContract.View mAlbumView;
    private List<PictureBean> images;
    @Inject
    public AlbumPresenter(PictureRepository repository, AlbumContract.View albumView){
        mPictureReposotory = repository;
        mAlbumView = albumView;
    }
    /**
     * Method injection is used here to safely reference {@code this} after the object is created.
     * For more information, see Java Concurrency in Practice.
     */
    @Inject
    void setupListeners() {
        mAlbumView.setPresenter(this);
    }

    public Bitmap getBitMap(String imageName, int width, int height) {
        return mPictureReposotory.getAdapterImage(imageName, width, height);
    }

    public List<PictureBean> getImages() {
        return images;
    }

    @Override
    public void toProcessingActivity() {
        mAlbumView.toProcessingActivity();
    }

    @Override
    public void toBeautifyActivity() {
        mAlbumView.toBeautifyActivity();
    }

    @Override
    public List<PictureBean> getImagesList() {
        return mPictureReposotory.getPicture();
    }
}
