package com.swift.camera;

import android.content.Context;

import dagger.Module;
import dagger.Provides;

/**
 * Created by bool on 17-4-14.
 */
@Module
public class ApplicationModule {
    private final Context mContext;

    ApplicationModule(Context context) {
        mContext = context;
    }

    @Provides
    Context provideContext() {
        return mContext;
    }
}
