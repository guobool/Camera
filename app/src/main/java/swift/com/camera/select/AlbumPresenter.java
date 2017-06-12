package swift.com.camera.select;

import android.support.v4.app.LoaderManager;

import javax.inject.Inject;

/**
 * Created by bool on 17-6-12.
 */

public class AlbumPresenter {
    private AlbumActivity mAlbumActivity;

    @Inject
    public AlbumPresenter(AlbumActivity activity){
        mAlbumActivity = activity;
    }
    public void scanImage(AlbumActivity activity, LoaderManager supportLoaderManager){}
}
