package swift.com.camera.Album;

import android.app.Activity;

import dagger.Module;
import dagger.Provides;
import swift.com.camera.utils.ImageLoad.GlideImageLoader;
import swift.com.camera.utils.ImageLoad.ImageLoader;

/**
 * Created by bool on 17-4-18.
 */
@Module
public class AlbumPresenterModule {
    private final AlbumContract.View mView;

    public AlbumPresenterModule(AlbumContract.View mView) {
        this.mView = mView;
    }

    @Provides
    AlbumContract.View provideAlbumContractView(){
        return mView;
    }
}
