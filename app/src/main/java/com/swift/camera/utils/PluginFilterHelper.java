package com.swift.camera.utils;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.os.Environment;

import java.io.File;
import java.io.FilenameFilter;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import dalvik.system.DexClassLoader;

public class PluginFilterHelper {
	private static PluginFilterHelper sInstance;
	private Context mContext;
	private final HashMap<String, PluginFilterPackage> mPackagesHolder = new HashMap<String, PluginFilterPackage>();
	//private String mNativeLibDir = null;

	private PluginFilterHelper(Context context) {
		mContext = context.getApplicationContext();
		//mNativeLibDir = mContext.getDir("pluginFilterLib", Context.MODE_PRIVATE).getAbsolutePath();

		if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState()) || !Environment.isExternalStorageRemovable()) {
			String filterPath = Environment.getExternalStorageDirectory() + "/SwiftCamera/filters/";
			File filterPlugins = new File(filterPath);
			File[] plugins = filterPlugins.listFiles(new FilenameFilter() {
				@Override
				public boolean accept(File file, String s) {
					return s.endsWith(".zip");
				}
			});
			if (plugins != null && plugins.length > 0) {
				for (File plugin : plugins) {
					loadApk(plugin.getAbsolutePath());
				}
			}
		}
	}

	public static PluginFilterHelper getInstance(Context context) {
		if (sInstance == null) {
			synchronized (PluginFilterHelper.class) {
				if (sInstance == null) {
					sInstance = new PluginFilterHelper(context);
				}
			}
		}

		return sInstance;
	}

	private PluginFilterPackage loadApk(final String dexPath) {
		PackageInfo packageInfo = mContext.getPackageManager().getPackageArchiveInfo(dexPath, 0);
		if (packageInfo == null) {
			return null;
		}

		PluginFilterPackage pluginPackage = preparePluginEnv(packageInfo, dexPath);
		return pluginPackage;
	}

	private PluginFilterPackage preparePluginEnv(PackageInfo packageInfo, String dexPath) {
		PluginFilterPackage pluginPackage = mPackagesHolder.get(packageInfo.packageName);
		if (pluginPackage != null) {
			return pluginPackage;
		}
		DexClassLoader dexClassLoader = createDexClassLoader(dexPath);
		AssetManager assetManager = createAssetManager(dexPath);
		Resources resources = createResources(assetManager);
		// create pluginPackage
		pluginPackage = new PluginFilterPackage(dexPath, dexClassLoader, resources, packageInfo);
		mPackagesHolder.put(packageInfo.packageName, pluginPackage);
		return pluginPackage;
	}

	private DexClassLoader createDexClassLoader(String dexPath) {
		File dexOutputDir = mContext.getDir("dex", Context.MODE_PRIVATE);
		String dexOutputPath = dexOutputDir.getAbsolutePath();
		DexClassLoader loader = new DexClassLoader(dexPath, dexOutputPath, null, mContext.getClassLoader());
		return loader;
	}

	private AssetManager createAssetManager(String dexPath) {
		try {
			AssetManager assetManager = AssetManager.class.newInstance();
			Method addAssetPath = assetManager.getClass().getMethod("addAssetPath", String.class);
			addAssetPath.invoke(assetManager, dexPath);
			return assetManager;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public PluginFilterPackage getPackage(String packageName) {
		return mPackagesHolder.get(packageName);
	}

	public List<String> getFilters() {
		List<String> filters = new ArrayList<>();

		filters.add("");
		Iterator it = mPackagesHolder.keySet().iterator();
		while(it.hasNext()){
			filters.add((String) it.next());
		}

		return filters;
	}

	private Resources createResources(AssetManager assetManager) {
		Resources superRes = mContext.getResources();
		Resources resources = new Resources(assetManager, superRes.getDisplayMetrics(), superRes.getConfiguration());
		return resources;
	}
}
