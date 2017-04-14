package swift.com.camera.processing;

import dagger.Component;
import swift.com.camera.data.PictureRepositoryComponent;

/**
 * Created by bool on 17-4-13.
 */
@Component(dependencies = PictureRepositoryComponent.class, modules = ProcessingPresenterModule.class)
public interface ProcessingComponent {
    void inject(ProcessingActivity activity);
}
