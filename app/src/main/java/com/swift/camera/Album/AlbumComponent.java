package com.swift.camera.Album;

import com.swift.camera.data.PictureRepositoryComponent;
import com.swift.camera.utils.ActivityScoped;

import dagger.Component;
/**
 * Created by bool on 17-4-18.
 */
@ActivityScoped
@Component(modules = AlbumPresenterModule.class, dependencies = PictureRepositoryComponent.class)
public interface AlbumComponent {
    void inject(AlbumActivity activity);
}
