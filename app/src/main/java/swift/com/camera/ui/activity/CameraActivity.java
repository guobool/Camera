package swift.com.camera.ui.activity;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.graphics.Bitmap;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import swift.com.camera.R;
import swift.com.camera.adapter.FilterAdapter;
import swift.com.camera.ui.view.CameraGrid;
import swift.com.camera.utils.ScreenUtils;

/**
 * Created by bool on 17-4-11.
 */

public class CameraActivity extends AppCompatActivity implements CameraContract.View, View.OnClickListener {
    private CameraContract.Presenter mCameraPresenter;
    private CameraContract.Support mCameraSupport;

    private float mFocusPointX, mFocusPointY;
    static final int FOCUS = 1;            // 聚焦
    static final int ZOOM = 2;            // 缩放
    private int mMode;                      //0是聚焦 1是放大
    private float mDist;
    private Handler mHandler = new Handler();
    private float mScreenBrightness;

    private LinearLayout mFilterLayout;
    private RecyclerView mFilterListView;
    private FilterAdapter mFilterAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);

        mCameraSupport = new CameraSupport(this);
        mCameraPresenter = new CameraPresenter(this, mCameraSupport);
        initView();
    }

    @Override
    protected void onResume() {
        super.onResume();
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        mScreenBrightness = lp.screenBrightness;
        if (mScreenBrightness == 1f) {
            mScreenBrightness = 254f / 255f;
        }
    }

    private void initView() {
        GLSurfaceView surfaceView = (GLSurfaceView) findViewById(R.id.surfaceView);
        mCameraPresenter.setGLSurfaceView(surfaceView);
        SurfaceHolder surfaceHolder = surfaceView.getHolder();
        surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        surfaceHolder.setKeepScreenOn(true);
        surfaceView.setFocusable(true);
        surfaceView.setBackgroundColor(TRIM_MEMORY_BACKGROUND);
        surfaceView.getHolder().addCallback((SurfaceHolder.Callback)mCameraPresenter);

        mFilterLayout = (LinearLayout)findViewById(R.id.layout_filter);
        mFilterListView = (RecyclerView) findViewById(R.id.filter_listView);
        mFilterAdapter = new FilterAdapter(this);
        mFilterListView.setAdapter(mFilterAdapter);
        mFilterAdapter.setOnFilterChangeListener(onFilterChangeListener);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        mFilterListView.setLayoutManager(linearLayoutManager);

        findViewById(R.id.goBack).setOnClickListener(this);
        findViewById(R.id.switchCamera).setOnClickListener(this);
        findViewById(R.id.flashMode).setOnClickListener(this);
        findViewById(R.id.capture).setOnClickListener(this);
        findViewById(R.id.filter).setOnClickListener(this);

        ImageView galleyView = (ImageView) findViewById(R.id.galley);
        galleyView.setOnClickListener(this);
        Bitmap lastPhoto = mCameraSupport.getLastPhoto();
        if (lastPhoto != null) {
            galleyView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            galleyView.setImageBitmap(lastPhoto);
        }

        surfaceView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction() & MotionEvent.ACTION_MASK) {
                    // 主点按下
                    case MotionEvent.ACTION_DOWN:
                        mFocusPointX = event.getX();
                        mFocusPointY = event.getY();
                        mMode = FOCUS;
                        break;
                    // 副点按下
                    case MotionEvent.ACTION_POINTER_DOWN:
                        mDist = ScreenUtils.spacing(event);
                        // 如果连续两点距离大于10，则判定为多点模式
                        if (ScreenUtils.spacing(event) > 10f) {
                            mMode = ZOOM;
                        }
                        break;
                    case MotionEvent.ACTION_UP:
                    case MotionEvent.ACTION_POINTER_UP:
                        mMode = FOCUS;
                        break;
                    case MotionEvent.ACTION_MOVE:
                        if (mMode == FOCUS) {
                            //pointFocus((int) event.getRawX(), (int) event.getRawY());
                        } else if (mMode == ZOOM) {
                            float newDist = ScreenUtils.spacing(event);
                            if (newDist > 10f) {
                                float tScale = (newDist - mDist) / mDist;
                                if (tScale < 0) {
                                    tScale = tScale * 10;
                                }
                                mCameraPresenter.addZoomIn((int) tScale);
                            }
                        }
                        break;
                }
                return false;
            }
        });

        surfaceView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    mCameraPresenter.pointFocus((int) mFocusPointX, (int) mFocusPointY);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                final View focusIndex = findViewById(R.id.focus_index);
                RelativeLayout.LayoutParams layout = new RelativeLayout.LayoutParams(focusIndex.getLayoutParams());
                layout.setMargins((int) mFocusPointX - 60, (int) mFocusPointY - 60, 0, 0);
                focusIndex.setLayoutParams(layout);
                focusIndex.setVisibility(View.VISIBLE);
                ScaleAnimation sa = new ScaleAnimation(3f, 1f, 3f, 1f,
                        ScaleAnimation.RELATIVE_TO_SELF, 0.5f, ScaleAnimation.RELATIVE_TO_SELF, 0.5f);
                sa.setDuration(800);
                focusIndex.startAnimation(sa);
                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        focusIndex.setVisibility(View.INVISIBLE);
                    }
                }, 800);
            }
        });

        View cameraSwitchView = findViewById(R.id.switchCamera);
        if (!mCameraPresenter.canSwitchCamera()) {
            cameraSwitchView.setVisibility(View.INVISIBLE);
        }

        View flashSwitchView = findViewById(R.id.flashMode);
        if (!mCameraPresenter.canSwitchFlashMode()) {
            flashSwitchView.setVisibility(View.INVISIBLE);
        }
    }

    private FilterAdapter.onFilterChangeListener onFilterChangeListener = new FilterAdapter.onFilterChangeListener(){
        @Override
        public void onFilterChanged(String filterId) {

        }
    };

    @Override
    public void onClick(final View v) {
        switch (v.getId()) {
            case R.id.flashMode:
                mCameraPresenter.switchFlashMode();
                break;

            case R.id.capture:
                mCameraPresenter.takePhoto();
                break;

            case R.id.switchCamera:
                mCameraPresenter.switchCamera();
                break;

            case R.id.filter:
                mCameraPresenter.chooseFilter();
                showFilters();
                break;

            case R.id.goBack:
                finish();
                break;
        }
    }

    @Override
    public GLSurfaceView surfaceView() {
        GLSurfaceView view = (GLSurfaceView) findViewById(R.id.surfaceView);
        return view;
    }

    @Override
    public void setFlashViewResourceId(int resourceId) {
        ImageView v = (ImageView) findViewById(R.id.flashMode);
        if (resourceId == -1) {
            v.setVisibility(View.INVISIBLE);
        } else {
            v.setImageResource(resourceId);
            v.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void updatePreviewRatio(float ratio) {
        int sWidth = ScreenUtils.getScreenWidth();
        int sHeight = ScreenUtils.getScreenHeight();

        int realWidth = sWidth;
        int realHeight = (int)(sWidth / ratio);

        if (realHeight > sHeight) {
            realWidth = (int)(sHeight * ratio);
            realHeight = sHeight;
        }

        GLSurfaceView surfaceView = (GLSurfaceView) findViewById(R.id.surfaceView);
        ViewGroup.LayoutParams lp = surfaceView.getLayoutParams();
        lp.width = realWidth;
        lp.height = realHeight;
        surfaceView.setLayoutParams(lp);

        CameraGrid gridView = (CameraGrid) findViewById(R.id.masking);
        lp = gridView.getLayoutParams();
        lp.width = realWidth;
        lp.height = realHeight;
        gridView.setLayoutParams(lp);
    }

    @Override
    public void setGLSurfaceViewRenderMode(int renderMode) {
        GLSurfaceView view = (GLSurfaceView) findViewById(R.id.surfaceView);
        view.setRenderMode(renderMode);
    }

    @Override
    public void toggleScreenBrightness() {
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        if (lp.screenBrightness == 1f) {
            lp.screenBrightness = mScreenBrightness;
            setFlashViewResourceId(R.mipmap.camera_light_off);
        } else {
            lp.screenBrightness = 1f;
            setFlashViewResourceId(R.mipmap.camera_light_on);
        }
        getWindow().setAttributes(lp);
    }

    private void showFilters(){
        ObjectAnimator animator = ObjectAnimator.ofFloat(mFilterLayout, "translationY", mFilterLayout.getHeight(), 0);
        animator.setDuration(200);
        animator.addListener(new Animator.AnimatorListener() {

            @Override
            public void onAnimationStart(Animator animation) {
                findViewById(R.id.capture).setClickable(false);
                mFilterLayout.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {

            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }
        });
        animator.start();
    }

    private void hideFilters(){
        ObjectAnimator animator = ObjectAnimator.ofFloat(mFilterLayout, "translationY", 0 ,  mFilterLayout.getHeight());
        animator.setDuration(200);
        animator.addListener(new Animator.AnimatorListener() {

            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                mFilterLayout.setVisibility(View.INVISIBLE);
                findViewById(R.id.capture).setClickable(true);
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                mFilterLayout.setVisibility(View.INVISIBLE);
                findViewById(R.id.capture).setClickable(true);
            }
        });
        animator.start();
    }
}
