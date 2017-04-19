package swift.com.camera.beautify;

import dagger.Component;
import swift.com.camera.data.PictureRepositoryComponent;
import swift.com.camera.utils.ActivityScoped;

/**
 * Created by bool on 17-4-19.
 */
@ActivityScoped
@Component(modules = BeautifyPresenterModule.class, dependencies = PictureRepositoryComponent.class)
public interface BeautifyComponent {
    void inject(BeautifyActivity activity);
}
