package com.swift.camera;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Environment;

import com.squareup.leakcanary.LeakCanary;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.InputStream;

import com.swift.camera.data.DaggerPictureRepositoryComponent;
import com.swift.camera.data.PictureRepositoryComponent;

/**
 * Created by skye on 4/11/17.
 */

public class TheApplication extends Application {

    private static TheApplication sInstance;

    PictureRepositoryComponent mRepositoryComponent;

    @Override
    public void onCreate() {
        super.onCreate();

        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState()) || !Environment.isExternalStorageRemovable()) {
            String filterPath = Environment.getExternalStorageDirectory() + "/SwiftCamera/filters/";
            File filterPlugins = new File(filterPath);
            filterPlugins.mkdirs();

            SharedPreferences sp = getSharedPreferences("SwiftCamera", MODE_PRIVATE);
            if (!sp.getBoolean("FiltersCopied", false)) {
                try {
                    String[] filters = getAssets().list("filters");
                    for (int i = 0; i < filters.length; i ++) {
                        InputStream is = getAssets().open("filters/" + filters[i]);
                        FileOutputStream fos = new FileOutputStream(new File(filterPath + filters[i]));
                        byte[] buffer = new byte[1024];
                        while (true) {
                            int len = is.read(buffer);
                            if (len == -1) {
                                break;
                            }
                            fos.write(buffer, 0, len);
                        }
                        is.close();
                        fos.close();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                SharedPreferences.Editor editor = getSharedPreferences("SwiftCamera", MODE_PRIVATE).edit();
                editor.putBoolean("FiltersCopied", true);
                editor.commit();
            }
        }

        mRepositoryComponent = DaggerPictureRepositoryComponent.builder()
                .applicationModule(new ApplicationModule((getApplicationContext())))
                .build();
        if (LeakCanary.isInAnalyzerProcess(this)) {
            // This process is dedicated to LeakCanary for heap analysis.
            // You should not init your app in this process.

            return;
        }
        LeakCanary.install(this);
    }

    public PictureRepositoryComponent getTasksRepositoryComponent() {
        return mRepositoryComponent;
    }

    public static TheApplication getInstance() {
        return sInstance;
    }

    public TheApplication() {
        sInstance = this;
    }

    public static Context getAppContext() {
        return sInstance.getApplicationContext();
    }
}
