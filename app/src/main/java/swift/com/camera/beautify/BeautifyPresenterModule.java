package swift.com.camera.beautify;

import dagger.Module;
import dagger.Provides;
import swift.com.camera.Album.AlbumContract;

/**
 * Created by bool on 17-4-19.
 */
@Module
class BeautifyPresenterModule {
    private final BeautifyContract.View mView;

    public BeautifyPresenterModule(BeautifyContract.View mView) {
        this.mView = mView;
    }

    @Provides
    BeautifyContract.View provideBeautifyContractView(){
        return mView;
    }
}

