package swift.com.camera.select;


import dagger.Module;
import dagger.Provides;

/**
 * Created by bool on 17-6-12.
 */
@Module
public class AlbumPresenterModule {
    private final AlbumContract.View mView;
    public AlbumPresenterModule(AlbumContract.View view) {
        mView = view;
    }

    @Provides
    AlbumContract.View provideAlbumContractView(){
        return mView;
    }
}
