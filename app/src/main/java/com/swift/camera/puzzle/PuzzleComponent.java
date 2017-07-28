package com.swift.camera.puzzle;

import dagger.Component;

/**
 * Created by bool on 17-6-19.
 */
@Component()
public interface PuzzleComponent {
    void inject(PuzzleContract.View view);
}
