package com.swift.camera.processing;

import dagger.Component;
import com.swift.camera.data.PictureRepositoryComponent;
import com.swift.camera.utils.ActivityScoped;

/**
 * Created by bool on 17-4-13.
 */
@ActivityScoped
@Component(dependencies = PictureRepositoryComponent.class, modules = ProcessingPresenterModule.class)
public interface ProcessingComponent {
    void inject(ProcessingActivity activity);
}
