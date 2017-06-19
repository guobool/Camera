package swift.com.camera.puzzle;

import javax.inject.Inject;

/**
 * Created by bool on 17-6-19.
 */

public class PuzzlePresenter implements PuzzleContract.Presenter {
    private PuzzleContract.View mView;
    @Inject
    public PuzzlePresenter(PuzzleContract.View view) {
        mView = view;
    }

    @Inject
    public void setupListeners() {
        mView.setPresenter(this);
    }
}
