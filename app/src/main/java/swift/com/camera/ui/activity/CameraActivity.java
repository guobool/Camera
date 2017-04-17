package swift.com.camera.ui.activity;

import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import swift.com.camera.R;

/**
 * Created by bool on 17-4-11.
 */

public class CameraActivity extends AppCompatActivity implements CameraContract.View, View.OnClickListener {
    private CameraContract.Presenter mCameraPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);

        findViewById(R.id.goBack).setOnClickListener(this);
        findViewById(R.id.switchCamera).setOnClickListener(this);
        findViewById(R.id.flashMode).setOnClickListener(this);
        findViewById(R.id.timeLapse).setOnClickListener(this);
        findViewById(R.id.capture).setOnClickListener(this);
        findViewById(R.id.galley).setOnClickListener(this);
        findViewById(R.id.filter).setOnClickListener(this);

        mCameraPresenter = new CameraPresenter(this, new CameraSupport());
        mCameraPresenter.setGLSurfaceView((GLSurfaceView) findViewById(R.id.surfaceView));
        View cameraSwitchView = findViewById(R.id.switchCamera);
        if (!mCameraPresenter.canSwitchCamera()) {
            cameraSwitchView.setVisibility(View.GONE);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        mCameraPresenter.onResume();

        View flashSwitchView = findViewById(R.id.flashMode);
        if (!mCameraPresenter.canSwitchFlashMode()) {
            flashSwitchView.setVisibility(View.GONE);
        }
    }

    @Override
    protected void onPause() {
        mCameraPresenter.onPause();
        super.onPause();
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
        }
    }

    @Override
    public void setGLSurfaceViewRenderMode(int renderMode) {
        GLSurfaceView view = (GLSurfaceView) findViewById(R.id.surfaceView);
        view.setRenderMode(renderMode);
    }
}
