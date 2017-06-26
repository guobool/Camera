package com.swift.camera.utils;

import android.util.DisplayMetrics;
import android.view.MotionEvent;

import com.swift.camera.TheApplication;

/**
 * Created by junnikokuki on 2017/4/20.
 */

public class ScreenUtils {
    public static float getScreenDensity() {
        return TheApplication.getAppContext().getResources().getDisplayMetrics().density;
    }

    public static int getScreenHeight() {
        return TheApplication.getAppContext().getResources().getDisplayMetrics().heightPixels;
    }

    public static int getScreenWidth() {
        return TheApplication.getAppContext().getResources().getDisplayMetrics().widthPixels;
    }

    public static int dp2px(float f)
    {
        return (int)(0.5F + f * getScreenDensity());
    }

    public static int px2dp(float pxValue) {
        return (int) (pxValue / getScreenDensity() + 0.5f);
    }

    /**
     * 两点的距离
     */
    public static float spacing(MotionEvent event) {
        if (event == null) {
            return 0;
        }
        float x = event.getX(0) - event.getX(1);
        float y = event.getY(0) - event.getY(1);
        return (float) Math.sqrt((double)(x * x + y * y));
    }
}
