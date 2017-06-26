package com.swift.camera.data;

import javax.inject.Singleton;

import dagger.Component;
import com.swift.camera.ApplicationModule;

/**
 * Created by bool on 17-4-13.
 */
@Singleton
@Component(modules = {ApplicationModule.class})
public interface PictureRepositoryComponent {
    PictureRepository getPictureRepository();
}
