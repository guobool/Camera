package app.m15.cn.camera.processing;

import android.app.Fragment;
import app.m15.cn.camera.ui.view.ProcessingContract;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import static com.google.common.base.Preconditions.checkNotNull;
/**
 * Created by bool on 17-4-11.
 */

public class ProcessingPresenter extends Fragment implements ProcessingContract.View{
    private ProcessingContract.Presenter mPresenter;
    @Override
    public void setPresenter(@NonNull ProcessingContract.Presenter presenter) {
        mPresenter = checkNotNull(presenter);
    }
}
