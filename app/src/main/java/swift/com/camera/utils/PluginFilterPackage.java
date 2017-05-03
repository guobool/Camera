package swift.com.camera.utils;

import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import cn.m15.gpuimage.GPUImageFilter;
import dalvik.system.DexClassLoader;
import swift.com.camera.TheApplication;

/**
 * Created by junnikokuki on 2017/5/3.
 */

public class PluginFilterPackage {
    public String mApkPath;
    public String mPackageName;
    public DexClassLoader mClassLoader;
    public AssetManager mAssetManager;
    public Resources mResources;
    public PackageInfo mPackageInfo;

    public PluginFilterPackage(String apkPath, DexClassLoader loader, Resources resources, PackageInfo packageInfo) {
        this.mApkPath = apkPath;
        this.mPackageName = packageInfo.packageName;
        this.mClassLoader = loader;
        this.mAssetManager = resources.getAssets();
        this.mResources = resources;
        this.mPackageInfo = packageInfo;
    }

    public int getFilterColor() {
        try {
            Class clazz = mClassLoader.loadClass(mPackageName + ".R$color");
            Field field = clazz.getField("filterColor");
            int resId = (int)field.get(null);
            return mResources.getColor(resId);
        } catch(Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    public Drawable getFilterThumb() {
        ApplicationInfo appInfo = mPackageInfo.applicationInfo;
        appInfo.sourceDir = mApkPath;
        appInfo.publicSourceDir = mApkPath;
        try {
            return appInfo.loadIcon(TheApplication.getAppContext().getPackageManager());
        } catch (OutOfMemoryError e) {
            e.printStackTrace();
            return null;
        }
    }

    public String getFilterName() {
        ApplicationInfo appInfo = mPackageInfo.applicationInfo;
        return appInfo.loadLabel(TheApplication.getAppContext().getPackageManager()).toString();
    }

    public GPUImageFilter getFilter() {
        try {
            final Class<Object> classToLoad = (Class<Object>) mClassLoader.loadClass(mPackageName + ".GPUImagePluginFilter");
            final Object myInstance  = classToLoad.newInstance();
            final Method doSomething = classToLoad.getMethod("pluginFilter");
            GPUImageFilter filter = (GPUImageFilter) doSomething.invoke(myInstance);
            return filter;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
