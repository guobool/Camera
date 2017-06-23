package swift.com.camera.puzzle;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PointF;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.ViewTreeObserver;

import javax.inject.Inject;
import swift.com.camera.R;
import swift.com.camera.utils.puzzle.Polygon;
import swift.com.camera.utils.puzzle.PuzzleView;

/**
 * Created by bool on 17-6-19.
 * 拼图细节选择页面
 */

public class PuzzleActivity extends AppCompatActivity implements PuzzleContract.View{
    @Inject
    PuzzleContract.Presenter mPresenter;
    protected PuzzleView mPuzzleView;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_puzzle);
        DaggerPuzzleComponent.builder()
                //.puzzlePresenterModule(new PuzzlePresenterModule(this))
                .build()
                .inject(this);
        mPuzzleView = (PuzzleView)findViewById(R.id.pv_puzzle);

        //增加整体布局监听
        ViewTreeObserver vto = mPuzzleView.getViewTreeObserver();
        vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener(){
            @Override
            public void onGlobalLayout() {
                float left = mPuzzleView.getContainLeft();
                float top = mPuzzleView.getContainTop();
                float right = mPuzzleView.getContainRight();
                float button = mPuzzleView.getContainBottom();
                PointF point = new PointF(left, top);
                PointF point2 = new PointF(right, top);
                PointF point3 = new PointF(right, button);

                PointF point4 = new PointF(left, button);
                Bitmap b = BitmapFactory.decodeResource(getResources(), R.mipmap.background);
                Polygon polygon = new Polygon(new BitmapDrawable(getResources(), b), point, point2, point3);
                Bitmap c = BitmapFactory.decodeResource(getResources(), R.mipmap.main_flow);
                Polygon polygon1 = new Polygon(new BitmapDrawable(getResources(), c), point, point3, point4);

                //polygon.setBitmap(b);

                //polygon1.setBitmap(c);
                mPuzzleView.setPolygons(polygon, polygon1);
                mPuzzleView.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                mPuzzleView.invalidate();
            }
        });
    }

    @Override
    public void setPresenter(PuzzleContract.Presenter presenter) {
        float left = mPuzzleView.getLeft();
        float top = mPuzzleView.getTop();
        float right = mPuzzleView.getRight();
        float button = mPuzzleView.getBottom();

    }

    @Override
    protected void onStart() {
        super.onStart();
        float left = mPuzzleView.getLeft();
        float top = mPuzzleView.getTop();
        float right = mPuzzleView.getRight();
        float button = mPuzzleView.getBottom();
    }

    @Override
    protected void onPause() {
        super.onPause();
        float left = mPuzzleView.getLeft();
        float top = mPuzzleView.getTop();
        float right = mPuzzleView.getRight();
        float button = mPuzzleView.getBottom();
    }

    @Override
    protected void onResume() {
        super.onResume();
        float left = mPuzzleView.getLeft();
        float top = mPuzzleView.getTop();
        float right = mPuzzleView.getRight();
        float button = mPuzzleView.getBottom();
    }
}
