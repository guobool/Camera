package swift.com.camera.processing;


import android.support.annotation.NonNull;

import swift.com.camera.data.PictureRepository;

import static com.squareup.haha.guava.base.Joiner.checkNotNull;

/**
 * Created by bool on 17-4-12.
 */

public class ProcessingPresenter implements ProcessingContract.Presenter{

    private PictureRepository mRepository;
    private ProcessingContract.View mProcessingView;
    public ProcessingPresenter(@NonNull PictureRepository repository,
                               @NonNull ProcessingContract.View view) {
        mRepository = checkNotNull(repository);
        mProcessingView = checkNotNull(view);
        mProcessingView.setPresenter(this);
    }

    @Override
    public void jumpAlbumPage() {
        mProcessingView.jumpAlbumPage();
    }

    @Override
    public void jumpBeautifyPage() {
        mProcessingView.jumpBeautifyPage();
    }
}
