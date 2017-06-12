package swift.com.camera.Album;

import android.content.Context;
import android.content.Intent;
import java.util.List;
import javax.inject.Inject;
import swift.com.camera.beautify.BeautifyActivity;
import swift.com.camera.data.PictureBean;
import swift.com.camera.data.PictureRepository;
import static swift.com.camera.data.PictureDataSource.*;

/**
 * Created by bool on 17-4-14.
 */

class AlbumPresenter implements AlbumContract.Presenter {
    private PictureRepository mPictureRepository;
    private AlbumContract.View mAlbumView;
    private List<PictureBean> mImagesBean;
    @Inject
    public AlbumPresenter(PictureRepository repository, AlbumContract.View albumView){
        mPictureRepository = repository;
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

    @Override
    public void toProcessingActivity() {
        mAlbumView.toProcessingActivity();
    }


    @Override
    public void getImagesList() {
        mPictureRepository.loadPicture(new LoadPictureCallBack() {
            @Override
            public void onPictureLoaded(List<PictureBean> pictureBeanList) {
                if(pictureBeanList != null){
                    mAlbumView.pictureBeanLoaded(pictureBeanList);
                }
            }

            @Override
            public void onLoadFailed() {
            }
        });
    }

    @Override
    public void toBeaytifyActivity(PictureBean pictureBean) {
        Intent intent = new Intent((Context) mAlbumView, BeautifyActivity.class);
        intent.putExtra("PictureBean", pictureBean);
        ((Context) mAlbumView).startActivity(intent);
    }
}
