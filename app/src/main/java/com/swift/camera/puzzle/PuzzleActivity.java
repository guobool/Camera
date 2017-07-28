package com.swift.camera.puzzle;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PointF;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.ViewTreeObserver;
import android.widget.SeekBar;

import com.swift.camera.R;
import com.swift.camera.ui.view.puzzle.Polygon;
import com.swift.camera.ui.view.puzzle.PuzzleView;

import javax.inject.Inject;


/**
 * Created by bool on 17-6-19.
 * 拼图细节选择页面
 */

public class PuzzleActivity extends AppCompatActivity implements PuzzleContract.View{
    @Inject
    PuzzleContract.Presenter mPresenter;
    protected PuzzleView mPuzzleView;
    protected SeekBar mSbBorderWidth, mSbAngleRoundness;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_puzzle);
        DaggerPuzzleComponent.builder()
                //.puzzlePresenterModule(new PuzzlePresenterModule(this))
                .build()
                .inject(this);
        mPuzzleView = (PuzzleView)findViewById(R.id.pv_puzzle);
        mSbBorderWidth = (SeekBar)findViewById(R.id.sb_border_width);
        mSbAngleRoundness = (SeekBar)findViewById(R.id.sb_angle_roundness);
        mSbBorderWidth.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                mPuzzleView.setBorderWidth(mSbBorderWidth.getProgress());
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });
        mSbAngleRoundness.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                mPuzzleView.setAngleRoundness(mSbAngleRoundness.getProgress());
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });
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

                mPuzzleView.setPolygons(polygon, polygon1);
                mPuzzleView.setBorderWidth(20);
                mPuzzleView.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                mPuzzleView.invalidate();
            }
        });
    }

    @Override
    public void setPresenter(PuzzleContract.Presenter presenter) {
        mPresenter = presenter;
    }

}
