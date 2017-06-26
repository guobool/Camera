package com.swift.camera.beautify;

import dagger.Component;
import com.swift.camera.data.PictureRepositoryComponent;
import com.swift.camera.utils.ActivityScoped;

/**
 * Created by bool on 17-4-19.
 */
@ActivityScoped
@Component(modules = BeautifyPresenterModule.class, dependencies = PictureRepositoryComponent.class)
public interface BeautifyComponent {
    void inject(BeautifyActivity activity);
}
