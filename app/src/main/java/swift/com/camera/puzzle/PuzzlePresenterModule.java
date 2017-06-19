package swift.com.camera.puzzle;

import dagger.Module;
import dagger.Provides;

/**
 * Created by bool on 17-6-19.
 */
@Module
public class PuzzlePresenterModule {
    private PuzzleContract.View mView;
    public PuzzlePresenterModule(PuzzleContract.View view) {
        mView = view;
    }

    @Provides
    PuzzleContract.View proPuzzleContractView() {
        return mView;
    }
}
