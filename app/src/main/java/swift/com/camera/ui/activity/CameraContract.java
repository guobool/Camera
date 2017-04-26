package swift.com.camera.ui.activity;

import android.graphics.Bitmap;
import android.opengl.GLSurfaceView;
import android.view.SurfaceView;

/**
 * Created by junnikokuki on 2017/4/12.
 */

public class CameraContract {
    // UI展示
    interface View {
        GLSurfaceView surfaceView();
        void setFlashViewResourceId(int resourceId);
        void updatePreviewRatio(float ratio);
        void setGLSurfaceViewRenderMode(int renderMode);
        void toggleScreenBrightness();
    }

    // 逻辑处理
    interface Presenter {
        void setGLSurfaceView(GLSurfaceView view);
        boolean canSwitchCamera();
        void switchCamera();
        boolean canSwitchFlashMode();
        void switchFlashMode();
        void chooseFilter();
        void takePhoto();
        void addZoomIn(int delta);
        void pointFocus(int x, int y);
    }

    // 数据存储
    interface Support {
        Bitmap getLastPhoto();
    }
}
