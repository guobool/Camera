package swift.com.camera.puzzle;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import javax.inject.Inject;

/**
 * Created by bool on 17-6-19.
 */

public class PuzzleActivity extends AppCompatActivity implements PuzzleContract.View{
    @Inject
    PuzzleContract.Presenter mPresenter;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        DaggerPuzzleComponent.builder()
                //.puzzlePresenterModule(new PuzzlePresenterModule(this))
                .build()
                .inject(this);
    }

    @Override
    public void setPresenter(PuzzleContract.Presenter presenter) {

    }
}
