package swift.com.camera.ui.activity;

import android.graphics.Bitmap;
import android.opengl.GLSurfaceView;
import android.view.OrientationEventListener;
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
        void updateZoom(int currentZoom, int maxZoom);
        void setGLSurfaceViewRenderMode(int renderMode);
        void updateLastPhoto();
        void toggleScreenBrightness();
    }

    // 逻辑处理
    interface Presenter {
        void setGLSurfaceView(GLSurfaceView view);
        boolean canSwitchCamera();
        void switchCamera();
        boolean canSwitchFlashMode();
        void switchFlashMode();
        void chooseFilter(String filterId);
        void takePhoto();
        boolean canZoom();
        void updateZoom(int zoom);
        void pointFocus(int x, int y);
        OrientationEventListener getOrientationEventListener();
    }

    // 数据存储
    interface Support {
        Bitmap getLastPhoto();
    }
}
