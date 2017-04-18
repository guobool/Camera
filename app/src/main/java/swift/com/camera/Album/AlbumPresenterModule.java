package swift.com.camera.Album;

import dagger.Module;
import dagger.Provides;

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
