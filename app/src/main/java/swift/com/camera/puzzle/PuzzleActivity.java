package swift.com.camera.puzzle;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PointF;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import javax.inject.Inject;

import swift.com.camera.R;
import swift.com.camera.utils.puzzle.Polygon;
import swift.com.camera.utils.puzzle.PolygonView;

/**
 * Created by bool on 17-6-19.
 */

public class PuzzleActivity extends AppCompatActivity implements PuzzleContract.View{
    @Inject
    PuzzleContract.Presenter mPresenter;
    protected PolygonView mPolygonView;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_puzzle);
        DaggerPuzzleComponent.builder()
                //.puzzlePresenterModule(new PuzzlePresenterModule(this))
                .build()
                .inject(this);
        mPolygonView = (PolygonView)findViewById(R.id.pv_puzzle);
        PointF point = new PointF(0, 0);
        PointF point2 = new PointF(100, 0);
        PointF point3 = new PointF(100, 100);
        PointF point4 = new PointF(0, 100);

        Polygon polygon = new Polygon(point, point2, point3, point4);
        Bitmap b = BitmapFactory.decodeResource(getResources(), R.raw.tone_cuver_sample);
        polygon.setBitmap(b);
        mPolygonView.setPolygons(polygon);
    }

    @Override
    public void setPresenter(PuzzleContract.Presenter presenter) {

    }
}
