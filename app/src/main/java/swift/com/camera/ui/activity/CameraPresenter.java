package swift.com.camera.ui.activity;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.hardware.Camera;
import android.net.Uri;
import android.opengl.GLSurfaceView;
import android.os.Build;
import android.os.Environment;
import android.util.Log;
import android.view.SurfaceHolder;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import cn.m15.gpuimage.GPUImage;
import cn.m15.gpuimage.GPUImageFilter;
import swift.com.camera.utils.CameraHelper;
import swift.com.camera.utils.GPUImageFilterTools;

import swift.com.camera.R;
import swift.com.camera.utils.ScreenUtils;

/**
 * Created by junnikokuki on 2017/4/12.
 */

public class CameraPresenter implements CameraContract.Presenter, SurfaceHolder.Callback {
    private final CameraContract.View mCameraView;
    private final CameraContract.Support mCameraSupport;
    private final Context mContext;

    private GPUImage mGPUImage;
    private GPUImageFilter mFilter;
    private GPUImageFilterTools.FilterAdjuster mFilterAdjuster;

    private CameraHelper mCameraHelper;

    private Camera.Parameters mParameters = null;
    private Camera mCameraInst = null;

    private Camera.Size mAdapterSize = null;
    private Camera.Size mPreviewSize = null;
    private int mCurrentCameraId = 0;  //1是前置 0是后置
    //放大缩小
    private int mCurZoomValue = 0;

    public CameraPresenter(CameraContract.View cameraView, CameraContract.Support cameraSupport) {
        mCameraView = cameraView;
        mCameraSupport = cameraSupport;
        mContext = (Context) cameraView;

        mGPUImage = new GPUImage(mContext);

        mCameraHelper = new CameraHelper(mContext);
    }

    @Override
    public boolean canSwitchCamera() {
        if (!mCameraHelper.hasFrontCamera() || !mCameraHelper.hasBackCamera()) {
            return false;
        }
        return true;
    }

    @Override
    public void switchCamera() {
        mCurrentCameraId = (mCurrentCameraId + 1) % mCameraHelper.getNumberOfCameras();
        releaseCamera();
        setUpCamera(mCurrentCameraId);
    }

    @Override
    public boolean canSwitchFlashMode() {
        if (mCameraInst != null && mCameraInst.getParameters().getFlashMode() != null) {
            return true;
        }

        return false;
    }

    @Override
    public void switchFlashMode() {
        if (mCurrentCameraId == 0) {
            turnFlash(mCameraInst);
        } else {
            turnLight(mCameraInst);
        }
    }

    @Override
    public void setGLSurfaceView(GLSurfaceView view) {
        mGPUImage.setGLSurfaceView(view);
    }

    @Override
    public void chooseFilter() {
        GPUImageFilterTools.showDialog(mContext, new GPUImageFilterTools.OnGpuImageFilterChosenListener() {

            @Override
            public void onGpuImageFilterChosenListener(final GPUImageFilter filter) {
                switchFilterTo(filter);
            }
        });
    }

    @Override
    public void takePhoto() {
        if (mCameraInst.getParameters().getFocusMode().equals(
                Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE)) {
            takePicture();
        } else {
            mCameraInst.autoFocus(new Camera.AutoFocusCallback() {

                @Override
                public void onAutoFocus(final boolean success, final Camera camera) {
                    takePicture();
                }
            });
        }
    }

    @Override
    public void addZoomIn(int delta) {
        try {
            Camera.Parameters params = mCameraInst.getParameters();
            Log.d("Camera", "Is support Zoom " + params.isZoomSupported());
            if (!params.isZoomSupported()) {
                return;
            }
            mCurZoomValue += delta;
            if (mCurZoomValue < 0) {
                mCurZoomValue = 0;
            } else if (mCurZoomValue > params.getMaxZoom()) {
                mCurZoomValue = params.getMaxZoom();
            }

            if (!params.isSmoothZoomSupported()) {
                params.setZoom(mCurZoomValue);
                mCameraInst.setParameters(params);
                return;
            } else {
                mCameraInst.startSmoothZoom(mCurZoomValue);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //定点对焦的代码
    @Override
    public void pointFocus(int x, int y) {
        mCameraInst.cancelAutoFocus();
        mParameters = mCameraInst.getParameters();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            showPoint(x, y);
        }
        mCameraInst.setParameters(mParameters);
        autoFocus();
    }

    private void switchFilterTo(final GPUImageFilter filter) {
        if (mFilter == null
                || (filter != null && !mFilter.getClass().equals(filter.getClass()))) {
            mFilter = filter;
            mGPUImage.setFilter(mFilter);
            mFilterAdjuster = new GPUImageFilterTools.FilterAdjuster(mFilter);
        }
    }

    private void takePicture() {
        // TODO get a size that is about the size of the screen
        Camera.Parameters params = mCameraInst.getParameters();
        params.setRotation(90);
        mCameraInst.setParameters(params);
        for (Camera.Size size : params.getSupportedPictureSizes()) {
            Log.i("SwiftCamera", "Supported: " + size.width + "x" + size.height);
        }
        mCameraInst.takePicture(null, null,
                new Camera.PictureCallback() {

                    @Override
                    public void onPictureTaken(byte[] data, final Camera camera) {

                        final File pictureFile = getOutputMediaFile(MEDIA_TYPE_IMAGE);
                        if (pictureFile == null) {
                            Log.d("SwiftCamera", "Error creating media file, check storage permissions");
                            return;
                        }

                        try {
                            FileOutputStream fos = new FileOutputStream(pictureFile);
                            fos.write(data);
                            fos.close();
                        } catch (FileNotFoundException e) {
                            Log.d("SwiftCamera", "File not found: " + e.getMessage());
                        } catch (IOException e) {
                            Log.d("SwiftCamera", "Error accessing file: " + e.getMessage());
                        }

                        data = null;
                        final Bitmap bitmap = BitmapFactory.decodeFile(pictureFile.getAbsolutePath());
                        // mGPUImage.setImage(bitmap);
                        mCameraView.setGLSurfaceViewRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
                        mGPUImage.saveToPictures(bitmap, "SwiftCamera", System.currentTimeMillis() + ".jpg", new GPUImage.OnPictureSavedListener() {

                                    @Override
                                    public void onPictureSaved(final Uri uri) {
                                        if(bitmap != null && !bitmap.isRecycled()){
                                            bitmap.recycle();
                                        }

                                        System.gc();

                                        pictureFile.delete();
                                        camera.startPreview();
                                        mCameraView.setGLSurfaceViewRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);
                                    }
                                });
                    }
                });
    }

    public static final int MEDIA_TYPE_IMAGE = 1;
    public static final int MEDIA_TYPE_VIDEO = 2;

    private static File getOutputMediaFile(final int type) {
        // To be safe, you should check that the SDCard is mounted
        // using Environment.getExternalStorageState() before doing this.

        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), "SwiftCamera");
        // This location works best if you want the created images to be shared
        // between applications and persist after your app has been uninstalled.

        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                Log.d("SwiftCamera", "failed to create directory");
                return null;
            }
        }

        // Create a media file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File mediaFile;
        if (type == MEDIA_TYPE_IMAGE) {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator +
                    "IMG_" + timeStamp + ".jpg");
        } else if (type == MEDIA_TYPE_VIDEO) {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator +
                    "VID_" + timeStamp + ".mp4");
        } else {
            return null;
        }

        return mediaFile;
    }

    //实现自动对焦
    private void autoFocus() {
        new Thread() {
            @Override
            public void run() {
                try {
                    sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                if (mCameraInst == null) {
                    return;
                }
                mCameraInst.autoFocus(new Camera.AutoFocusCallback() {
                    @Override
                    public void onAutoFocus(boolean success, Camera camera) {
                        if (success) {
                            initCamera();//实现相机的参数初始化
                        }
                    }
                });
            }
        };
    }

    private void initCamera() {
        mParameters = mCameraInst.getParameters();
        mParameters.setPictureFormat(PixelFormat.JPEG);
        //if (adapterSize == null) {
        setUpPicSize(mParameters);
        setUpPreviewSize(mParameters);
        //}
        if (mAdapterSize != null) {
            mParameters.setPictureSize(mAdapterSize.width, mAdapterSize.height);
        }
        if (mPreviewSize != null) {
            mParameters.setPreviewSize(mPreviewSize.width, mPreviewSize.height);
            mCameraView.updatePreviewRatio(mPreviewSize.width > mPreviewSize.height ? ((float) mPreviewSize.height / (float)mPreviewSize.width) : ((float)mPreviewSize.width / (float)mPreviewSize.height));
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            mParameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);//1连续对焦
        } else {
            mParameters.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);
        }
        setDispaly(mParameters, mCameraInst);

        if (mCurrentCameraId == 0) {
            mParameters.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
            mCameraView.setFlashViewResourceId(R.mipmap.camera_flash_off);
        } else {
            mCameraView.setFlashViewResourceId(R.mipmap.camera_light_off);
        }

        try {
            mCameraInst.setParameters(mParameters);
        } catch (Exception e) {
            e.printStackTrace();
        }
        mCameraInst.startPreview();
        mCameraInst.cancelAutoFocus();// 2如果要实现连续的自动对焦，这一句必须加上
    }

    private void setUpPicSize(Camera.Parameters parameters) {

        if (mAdapterSize != null) {
            return;
        } else {
            mAdapterSize = findBestPictureResolution();
        }
    }

    private void setUpPreviewSize(Camera.Parameters parameters) {

        if (mPreviewSize != null) {
            return;
        } else {
            mPreviewSize = findBestPreviewResolution();
        }
    }

    /**
     * 最小预览界面的分辨率
     */
    private static final int MIN_PREVIEW_PIXELS = 480 * 320;
    /**
     * 最大宽高比差
     */
    private static final double MAX_ASPECT_DISTORTION = 0.15;
    private static final String TAG = "Camera";

    /**
     * 找出最适合的预览界面分辨率
     *
     * @return
     */
    private Camera.Size findBestPreviewResolution() {
        Camera.Parameters cameraParameters = mCameraInst.getParameters();
        Camera.Size defaultPreviewResolution = cameraParameters.getPreviewSize();

        List<Camera.Size> rawSupportedSizes = cameraParameters.getSupportedPreviewSizes();
        if (rawSupportedSizes == null) {
            return defaultPreviewResolution;
        }

        // 按照分辨率从大到小排序
        List<Camera.Size> supportedPreviewResolutions = new ArrayList<Camera.Size>(rawSupportedSizes);
        Collections.sort(supportedPreviewResolutions, new Comparator<Camera.Size>() {
            @Override
            public int compare(Camera.Size a, Camera.Size b) {
                int aPixels = a.height * a.width;
                int bPixels = b.height * b.width;
                if (bPixels < aPixels) {
                    return -1;
                }
                if (bPixels > aPixels) {
                    return 1;
                }
                return 0;
            }
        });

        StringBuilder previewResolutionSb = new StringBuilder();
        for (Camera.Size supportedPreviewResolution : supportedPreviewResolutions) {
            previewResolutionSb.append(supportedPreviewResolution.width).append('x').append(supportedPreviewResolution.height)
                    .append(' ');
        }
        Log.v(TAG, "Supported preview resolutions: " + previewResolutionSb);


        // 移除不符合条件的分辨率
        double screenAspectRatio = (double) ScreenUtils.getScreenWidth()
                / (double) ScreenUtils.getScreenHeight();
        Iterator<Camera.Size> it = supportedPreviewResolutions.iterator();
        while (it.hasNext()) {
            Camera.Size supportedPreviewResolution = it.next();
            int width = supportedPreviewResolution.width;
            int height = supportedPreviewResolution.height;

            // 移除低于下限的分辨率，尽可能取高分辨率
            if (width * height < MIN_PREVIEW_PIXELS) {
                it.remove();
                continue;
            }

            // 在camera分辨率与屏幕分辨率宽高比不相等的情况下，找出差距最小的一组分辨率
            // 由于camera的分辨率是width>height，我们设置的portrait模式中，width<height
            // 因此这里要先交换然preview宽高比后在比较
            boolean isCandidatePortrait = width > height;
            int maybeFlippedWidth = isCandidatePortrait ? height : width;
            int maybeFlippedHeight = isCandidatePortrait ? width : height;
            double aspectRatio = (double) maybeFlippedWidth / (double) maybeFlippedHeight;
            double distortion = Math.abs(aspectRatio - screenAspectRatio);
            if (distortion > MAX_ASPECT_DISTORTION) {
                it.remove();
                continue;
            }

            // 找到与屏幕分辨率完全匹配的预览界面分辨率直接返回
            if (maybeFlippedWidth == ScreenUtils.getScreenWidth()
                    && maybeFlippedHeight == ScreenUtils.getScreenHeight()) {
                return supportedPreviewResolution;
            }
        }

        // 如果没有找到合适的，并且还有候选的像素，则设置其中最大比例的，对于配置比较低的机器不太合适
        if (!supportedPreviewResolutions.isEmpty()) {
            Camera.Size largestPreview = supportedPreviewResolutions.get(0);
            return largestPreview;
        }

        // 没有找到合适的，就返回默认的

        return defaultPreviewResolution;
    }

    private Camera.Size findBestPictureResolution() {
        Camera.Parameters cameraParameters = mCameraInst.getParameters();
        List<Camera.Size> supportedPicResolutions = cameraParameters.getSupportedPictureSizes(); // 至少会返回一个值

        StringBuilder picResolutionSb = new StringBuilder();
        for (Camera.Size supportedPicResolution : supportedPicResolutions) {
            picResolutionSb.append(supportedPicResolution.width).append('x')
                    .append(supportedPicResolution.height).append(" ");
        }
        Log.d(TAG, "Supported picture resolutions: " + picResolutionSb);

        Camera.Size defaultPictureResolution = cameraParameters.getPictureSize();
        Log.d(TAG, "default picture resolution " + defaultPictureResolution.width + "x"
                + defaultPictureResolution.height);

        // 排序
        List<Camera.Size> sortedSupportedPicResolutions = new ArrayList<Camera.Size>(
                supportedPicResolutions);
        Collections.sort(sortedSupportedPicResolutions, new Comparator<Camera.Size>() {
            @Override
            public int compare(Camera.Size a, Camera.Size b) {
                int aPixels = a.height * a.width;
                int bPixels = b.height * b.width;
                if (bPixels < aPixels) {
                    return -1;
                }
                if (bPixels > aPixels) {
                    return 1;
                }
                return 0;
            }
        });

        // 移除不符合条件的分辨率
        double screenAspectRatio = (double) ScreenUtils.getScreenWidth()
                / (double) ScreenUtils.getScreenHeight();
        Iterator<Camera.Size> it = sortedSupportedPicResolutions.iterator();
        while (it.hasNext()) {
            Camera.Size supportedPreviewResolution = it.next();
            int width = supportedPreviewResolution.width;
            int height = supportedPreviewResolution.height;

            // 在camera分辨率与屏幕分辨率宽高比不相等的情况下，找出差距最小的一组分辨率
            // 由于camera的分辨率是width>height，我们设置的portrait模式中，width<height
            // 因此这里要先交换然后在比较宽高比
            boolean isCandidatePortrait = width > height;
            int maybeFlippedWidth = isCandidatePortrait ? height : width;
            int maybeFlippedHeight = isCandidatePortrait ? width : height;
            double aspectRatio = (double) maybeFlippedWidth / (double) maybeFlippedHeight;
            double distortion = Math.abs(aspectRatio - screenAspectRatio);
            if (distortion > MAX_ASPECT_DISTORTION) {
                it.remove();
                continue;
            }
        }

        // 如果没有找到合适的，并且还有候选的像素，对于照片，则取其中最大比例的，而不是选择与屏幕分辨率相同的
        if (!sortedSupportedPicResolutions.isEmpty()) {
            return sortedSupportedPicResolutions.get(0);
        }

        // 没有找到合适的，就返回默认的
        return defaultPictureResolution;
    }


    //控制图像的正确显示方向
    private void setDispaly(Camera.Parameters parameters, Camera camera) {
        if (Build.VERSION.SDK_INT >= 8) {
            setDisplayOrientation(camera, 90);
        } else {
            parameters.setRotation(90);
        }
    }

    //实现的图像的正确显示
    private void setDisplayOrientation(Camera camera, int i) {
        Method downPolymorphic;
        try {
            downPolymorphic = camera.getClass().getMethod("setDisplayOrientation",
                    new Class[]{int.class});
            if (downPolymorphic != null) {
                downPolymorphic.invoke(camera, new Object[]{i});
            }
        } catch (Exception e) {
            Log.e("Came_e", "图像出错");
        }
    }

    private void turnLight(Camera mCamera) {
        mCameraView.toggleScreenBrightness();
    }

    /**
     * 闪光灯开关   开->关->自动
     *
     * @param mCamera
     */
    private void turnFlash(Camera mCamera) {
        if (mCamera == null || mCamera.getParameters() == null
                || mCamera.getParameters().getSupportedFlashModes() == null) {
            return;
        }
        Camera.Parameters parameters = mCamera.getParameters();
        String flashMode = mCamera.getParameters().getFlashMode();
        List<String> supportedModes = mCamera.getParameters().getSupportedFlashModes();

        List<String> availableModes = new ArrayList<>();
        List<Integer> availableResources = new ArrayList<>();
        if (supportedModes.contains(Camera.Parameters.FLASH_MODE_AUTO)) {
            availableModes.add(Camera.Parameters.FLASH_MODE_AUTO);
            availableResources.add(R.mipmap.camera_flash_auto);
        }
        if (supportedModes.contains(Camera.Parameters.FLASH_MODE_ON)) {
            availableModes.add(Camera.Parameters.FLASH_MODE_ON);
            availableResources.add(R.mipmap.camera_flash_on);
        }
        if (supportedModes.contains(Camera.Parameters.FLASH_MODE_TORCH)) {
            availableModes.add(Camera.Parameters.FLASH_MODE_TORCH);
            availableResources.add(R.mipmap.camera_flash_torch);
        }
        if (supportedModes.contains(Camera.Parameters.FLASH_MODE_OFF)) {
            availableModes.add(Camera.Parameters.FLASH_MODE_OFF);
            availableResources.add(R.mipmap.camera_flash_off);
        }

        if (availableModes.size() > 0) {
            int currentIndex = availableModes.indexOf(flashMode);
            currentIndex ++;

            if (currentIndex >= availableModes.size()) {
                currentIndex = 0;
            }

            parameters.setFlashMode(availableModes.get(currentIndex));
            mCamera.setParameters(parameters);
            mCameraView.setFlashViewResourceId(availableResources.get(currentIndex));
        }
    }

    private void releaseCamera() {
        if (mCameraInst != null) {
            mCameraInst.stopPreview();
            mCameraInst.setPreviewCallback(null);
            mCameraInst.release();
            mCameraInst = null;
        }
        mAdapterSize = null;
        mPreviewSize = null;
    }

    /**
     * @param mCurrentCameraId2
     */
    private void setUpCamera(int mCurrentCameraId2) {
        mCameraInst = getCameraInstance(mCurrentCameraId2);
        if (mCameraInst != null) {
            try {
                //mCameraInst.setPreviewDisplay(mCameraView.surfaceView().getHolder());
                int orientation = mCameraHelper.getCameraDisplayOrientation((Activity) mContext, mCurrentCameraId);
                mGPUImage.setUpCamera(mCameraInst, orientation, mCurrentCameraId == 0 ? false : true, false);
                initCamera();
                mCameraInst.startPreview();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private Camera getCameraInstance(final int id) {
        Camera c = null;
        try {
            c = mCameraHelper.openCamera(id);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return c;
    }

    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    private void showPoint(int x, int y) {
        if (mParameters.getMaxNumMeteringAreas() > 0) {
            List<Camera.Area> areas = new ArrayList<Camera.Area>();
            //xy变换了
            int rectY = -x * 2000 / ScreenUtils.getScreenWidth() + 1000;
            int rectX = y * 2000 / ScreenUtils.getScreenHeight() - 1000;

            int left = rectX < -900 ? -1000 : rectX - 100;
            int top = rectY < -900 ? -1000 : rectY - 100;
            int right = rectX > 900 ? 1000 : rectX + 100;
            int bottom = rectY > 900 ? 1000 : rectY + 100;
            Rect area1 = new Rect(left, top, right, bottom);
            areas.add(new Camera.Area(area1, 800));
            mParameters.setMeteringAreas(areas);
        }

        mParameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        try {
            releaseCamera();
        } catch (Exception e) {
            //相机已经关了
        }
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        if (null == mCameraInst) {
            setUpCamera(mCurrentCameraId);
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        autoFocus();
    }
}
