package swift.com.camera.select;

import java.util.List;

import javax.inject.Inject;

import swift.com.camera.data.PictureInfo;
import swift.com.camera.data.PictureRepository;

/**
 * Created by bool on 17-6-12.
 */

public class AlbumPresenter {
    private PictureRepository mPictureRepository;
    private AlbumContract.View mAlbumView;
    private List<PictureInfo> mImagesBean;
    @Inject
    public AlbumPresenter(AlbumContract.View albumView){
        //mPictureRepository = repository;
        mAlbumView = albumView;
    }

    public void scanImage(){}
}
