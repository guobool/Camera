package swift.com.camera.ui.activity;

import android.graphics.Bitmap;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import swift.com.camera.R;
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
            cameraSwitchView.setVisibility(View.GONE);
        }
    }

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
                break;

            case R.id.goBack:
                finish();
                break;
        }
    }

    @Override
    public SurfaceView surfaceView() {
        GLSurfaceView view = (GLSurfaceView) findViewById(R.id.surfaceView);
        return view;
    }

    @Override
    public void setFlashViewResourceId(int resourceId) {
        ImageView v = (ImageView) findViewById(R.id.flashMode);
        v.setImageResource(resourceId);
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
        lp = surfaceView.getLayoutParams();
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
}
