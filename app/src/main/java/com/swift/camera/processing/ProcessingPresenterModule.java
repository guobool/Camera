package com.swift.camera.processing;

import dagger.Module;
import dagger.Provides;

/**
 * Created by bool on 17-4-13.
 */
@Module
public class ProcessingPresenterModule {
    private final ProcessingContract.View mView;

    public ProcessingPresenterModule(ProcessingContract.View mView) {
        this.mView = mView;
    }


    @Provides
    ProcessingContract.View provideProcessingContrackView(){
        return mView;
    }

}
