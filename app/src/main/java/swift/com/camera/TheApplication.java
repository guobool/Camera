package swift.com.camera;

import android.app.Application;

import com.squareup.leakcanary.LeakCanary;

import swift.com.camera.data.DaggerPictureRepositoryComponent;
import swift.com.camera.data.PictureRepositoryComponent;

/**
 * Created by skye on 4/11/17.
 */

public class TheApplication extends Application {
    PictureRepositoryComponent mRepositoryComponent;
    @Override
    public void onCreate() {
        super.onCreate();
        mRepositoryComponent = DaggerPictureRepositoryComponent.builder()
                .applicationModule(new ApplicationModule((getApplicationContext())))
                .build();
        if (LeakCanary.isInAnalyzerProcess(this)) {
            // This process is dedicated to LeakCanary for heap analysis.
            // You should not init your app in this process.

            return;
        }
        LeakCanary.install(this);
    }

    public PictureRepositoryComponent getTasksRepositoryComponent() {
        return mRepositoryComponent;
    }
}
