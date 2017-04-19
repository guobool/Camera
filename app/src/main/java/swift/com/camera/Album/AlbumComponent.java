package swift.com.camera.Album;

import dagger.Component;
import swift.com.camera.data.PictureRepositoryComponent;
import swift.com.camera.utils.ActivityScoped;

/**
 * Created by bool on 17-4-18.
 */
@ActivityScoped
@Component(modules = AlbumPresenterModule.class, dependencies = PictureRepositoryComponent.class)
public interface AlbumComponent {
    void inject(AlbumActivity activity);
}
