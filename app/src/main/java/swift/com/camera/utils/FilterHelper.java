package swift.com.camera.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Environment;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import cn.m15.gpuimage.GPUImageFilter;
import cn.m15.gpuimage.GPUImageFilterGroup;
import swift.com.camera.TheApplication;

public class FilterHelper {
	public static int filterColor(String filterId){
		return 0;
	}
	
	public static Bitmap filterThumb(String filterId){
		return null;
	}
	
	public static String filterName(String filterId){
		return "Name";
	}

	public static GPUImageFilter filter(String filterId){
		try {
			List<GPUImageFilter> filters = new ArrayList<>();

			Map filterMap = null;//TODO
			try {
				String path = getFilesDir(TheApplication.getAppContext()) + "/filters/" + filterId + "/filter.info";

				FileInputStream freader = new FileInputStream(path);
				ObjectInputStream objectInputStream = new ObjectInputStream(freader);
				filterMap = (HashMap) objectInputStream.readObject();
				objectInputStream.close();
				freader.close();
			} catch (IOException e) {
				e.printStackTrace();
			}

			if (filterMap == null) {
				return null;
			}



//			List<Map> sf = new ArrayList<>();
//			Map sfm = new HashMap();
//			sfm.put("class", "Sketch");
//			sf.add(sfm);
//			filterMap.put("subfilters", sf);
//
//			try {
//				String path = getFilesDir(TheApplication.getAppContext()) + "/filters/" + filterId + "/";
//				File dir = new File(path);
//				dir.mkdirs();
//
//				FileOutputStream outStream = new FileOutputStream(path + "filter.info");
//				ObjectOutputStream objectOutputStream = new ObjectOutputStream(outStream);
//
//				objectOutputStream.writeObject(filterMap);
//				outStream.close();
//			} catch (FileNotFoundException e) {
//				e.printStackTrace();
//			} catch (IOException e) {
//				e.printStackTrace();
//			}



			List<Map> subFilters = (List<Map>) filterMap.get("subfilters");
			for (int i = 0; i < subFilters.size(); i++) {
				Map subFilterMap = subFilters.get(i);
				String className = (String) subFilterMap.get("class");
				if (className == null) {
					continue;
				}

				List<Object> initParams = (List<Object>) subFilterMap.get("initparams");
				GPUImageFilter filter = (GPUImageFilter) ReflectHelper.newInstance(String.format("cn.m15.gpuimage.GPUImage%sFilter", className), initParams == null ? null : initParams.toArray());
				if (filter != null) {
					Map properties = (Map) subFilterMap.get("properties");
					if (properties != null) {
						Set<String> propertiesKeySet = properties.keySet();
						for (String property : propertiesKeySet) {
							ReflectHelper.setProperty(filter, property, properties.get(property));
						}
					}

					String image = (String) subFilterMap.get("image");
					if (image != null) {
						ReflectHelper.invokeMethod(filter, "setBitmap", new Object[] { image });
					}
					filters.add(filter);
				}
			}

			if (filters.size() > 0) {
				if (filters.size() == 1) {
					return filters.get(0);
				} else {
					return new GPUImageFilterGroup(filters);
				}
			} else {
				return null;
			}
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public static String getFilesDir(Context context) {
		String cachePath = null;
		if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())
				|| !Environment.isExternalStorageRemovable()) {
			cachePath = context.getExternalFilesDir(null).getPath();
		} else {
			cachePath = context.getFilesDir().getPath();
		}
		return cachePath;
	}
}
