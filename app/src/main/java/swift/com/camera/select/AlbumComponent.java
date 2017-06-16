package swift.com.camera.select;

import dagger.Component;
import swift.com.camera.data.PictureRepositoryComponent;
import swift.com.camera.utils.ActivityScoped;

/**
 * Created by bool on 17-6-13.
 */
@ActivityScoped
@Component(modules = AlbumPresenterModule.class, dependencies = {PictureRepositoryComponent.class})
public interface AlbumComponent {
    void inject(AlbumActivity activity);
}
