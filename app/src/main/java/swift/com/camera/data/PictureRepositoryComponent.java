package swift.com.camera.data;

import dagger.Component;

/**
 * Created by bool on 17-4-13.
 */
@Component(modules = PictureRepositoryModule.class)
public interface PictureRepositoryComponent {
    PictureRepository getPictureRepository();
}
