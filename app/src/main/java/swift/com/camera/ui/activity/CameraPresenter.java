package swift.com.camera.ui.activity;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.hardware.Camera;
import android.media.CamcorderProfile;
import android.media.MediaRecorder;
import android.net.Uri;
import android.opengl.GLSurfaceView;
import android.os.Build;
import android.os.Environment;
import android.util.Log;
import android.view.OrientationEventListener;
import android.view.Surface;
import android.view.SurfaceHolder;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import cn.m15.gpuimage.GPUImage;
import cn.m15.gpuimage.GPUImageFilter;
import cn.m15.gpuimage.Rotation;
import cn.m15.gpuimage.video.GPUImageVideo;
import cn.m15.gpuimage.video.RecordCoderState;
import swift.com.camera.R;
import swift.com.camera.utils.CameraHelper;
import swift.com.camera.utils.PluginFilterHelper;
import swift.com.camera.utils.PluginFilterPackage;
import swift.com.camera.utils.ScreenUtils;

/**
 * Created by junnikokuki on 2017/4/12.
 */

public class CameraPresenter implements CameraContract.Presenter, SurfaceHolder.Callback {
    private final CameraContract.View mCameraView;
    private final CameraContract.Support mCameraSupport;
    private final Context mContext;

    private OrientationEventListener mOrientationListener;

    private GPUImageVideo mGPUImage;
    private GPUImageFilter mFilter;
    private String mFilterId = "";

    private CameraHelper mCameraHelper;

    private Camera.Parameters mParameters = null;
    private Camera mCameraInst = null;

    private Camera.Size mAdapterSize = null;
    private Camera.Size mPreviewSize = null;
    private int mCurrentCameraId = 0;  //1是前置 0是后置
    private int mCurOrientation = 0;

    public CameraPresenter(CameraContract.View cameraView, CameraContract.Support cameraSupport) {
        mCameraView = cameraView;
        mCameraSupport = cameraSupport;
        mContext = (Context) cameraView;
        mGPUImage = new GPUImageVideo(mContext);
        mCameraHelper = new CameraHelper(mContext);
        mOrientationListener = new OrientationEventListener(mContext) {
            @Override
            public void onOrientationChanged(int i) {
                int rotation = 0;
                if (i > 325 || i <= 45) {
                    rotation = 90;
                } else if (i > 45 && i <= 135) {
                    rotation = 180;
                } else if (i > 135 && i < 225) {
                    rotation = 270;
                }

                if (rotation != mCurOrientation) {
                    mCurOrientation = rotation;
                }
            }
        };
    }

    @Override
    public boolean canSwitchCamera() {
        return mCameraHelper.hasFrontCamera() && mCameraHelper.hasBackCamera();
    }

    @Override
    public void switchCamera() {
        mCurrentCameraId = (mCurrentCameraId + 1) % mCameraHelper.getNumberOfCameras();
        releaseCamera();
        setUpCamera(mCurrentCameraId);
        if (canZoom()) {
            mCameraView.updateZoom(currentZoom(), maxZoom());
        }
    }

    @Override
    public boolean canSwitchFlashMode() {
        return (mCameraInst != null && mCameraInst.getParameters().getFlashMode() != null);
    }

    @Override
    public void switchFlashMode() {
        if (isFrontCamera()) {
            turnLight(mCameraInst);
        } else {
            turnFlash(mCameraInst);
        }
    }

    @Override
    public void setGLSurfaceView(GLSurfaceView view) {
        mGPUImage.setGLSurfaceView(view);
    }

    @Override
    public void chooseFilter(String filterId) {
        mFilterId = filterId;
        GPUImageFilter filter = getFilter(mFilterId);
        if (filter != null) {
            switchFilterTo(filter);
        }
    }

    private GPUImageFilter getFilter(String filterId) {
        if (filterId.length() == 0) {
            return new GPUImageFilter();
        } else {
            PluginFilterPackage p = PluginFilterHelper.getInstance(mContext).getPackage(filterId);
            if (p != null) {
                GPUImageFilter f = p.getFilter();
                if (f != null) {
                    return f;
                }
            }
            return null;
        }
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
    public boolean canZoom() {
        Camera.Parameters params = mCameraInst.getParameters();
        return params.isZoomSupported() && params.getMaxZoom() > 0;
    }

    private int maxZoom() {
        if (canZoom()) {
            Camera.Parameters params = mCameraInst.getParameters();
            return params.getMaxZoom();
        } else {
            return 0;
        }
    }

    private int currentZoom() {
        if (canZoom()) {
            Camera.Parameters params = mCameraInst.getParameters();
            return params.getZoom();
        } else {
            return 0;
        }
    }

    @Override
    public void updateZoom(int zoom) {
        try {
            Camera.Parameters params = mCameraInst.getParameters();
            if (!params.isZoomSupported() || params.getMaxZoom() == 0) {
                return;
            }
            int curZoomValue = zoom;
            if (curZoomValue < 0) {
                curZoomValue = 0;
            } else if (curZoomValue > params.getMaxZoom()) {
                curZoomValue = params.getMaxZoom();
            }

            if (!params.isSmoothZoomSupported()) {
                params.setZoom(curZoomValue);
                mCameraInst.setParameters(params);
            } else {
                mCameraInst.startSmoothZoom(curZoomValue);
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
        showPoint(x, y);
        mCameraInst.setParameters(mParameters);
        autoFocus();
    }

    private void switchFilterTo(final GPUImageFilter filter) {
        if (mFilter == null
                || (filter != null && !mFilter.getClass().equals(filter.getClass()))) {
            mFilter = filter;
            mGPUImage.setFilter(mFilter);
        }
    }

    private boolean isFrontCamera() {
        Camera.CameraInfo info = new Camera.CameraInfo();
        Camera.getCameraInfo(mCurrentCameraId, info);
        return (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT);
    }

    private void startRecord() {
        if (mGPUImage.getCurrentRecordState() == RecordCoderState.IDLE) {
            final File videoFile = getOutputMediaFile(MEDIA_TYPE_VIDEO);
            if (videoFile == null) {
                Log.d("SwiftCamera", "Error creating media file, check storage permissions");
                return;
            }

            Camera.Parameters params = mCameraInst.getParameters();
            // 设置对焦模式
            List<String> focusModes = params.getSupportedFocusModes();
            if (focusModes.contains(Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO)) {
                params.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO);
            }

            // 2.配置录制参数 init recorder params
            CamcorderProfile profile = CamcorderProfile.get(CamcorderProfile.QUALITY_HIGH);
            profile.videoFrameWidth = mPreviewSize.height;
            profile.videoFrameHeight = mPreviewSize.width;
            profile.videoFrameRate = cn.m15.gpuimage.video.VideoConfig.VIDEO_FRAME_RATE;
            profile.fileFormat = MediaRecorder.OutputFormat.MPEG_4;
            profile.videoCodec = MediaRecorder.VideoEncoder.H264;
            profile.audioCodec = MediaRecorder.AudioEncoder.AAC;

            mCameraInst.setParameters(params);

            mGPUImage.createNewRecorder(videoFile, profile.videoFrameWidth, profile.videoFrameHeight, mCurOrientation, 800000);
            mGPUImage.startRecord();
        } else if (mGPUImage.getCurrentRecordState() == RecordCoderState.START) {
            mGPUImage.stopRecord();
        }

    }

    private void takePicture() {
        Camera.Parameters params = mCameraInst.getParameters();
        int rotation = mCurOrientation;
        if (isFrontCamera()) {
            rotation = (360 - rotation) % 360;
        }
        params.setRotation(rotation);
        mCameraInst.setParameters(params);
        mCameraInst.takePicture(null, null,
                new Camera.PictureCallback() {
                    @Override
                    public void onPictureTaken(byte[] data, final Camera camera) {
                        final File pictureFile = getOutputMediaFile(MEDIA_TYPE_IMAGE);
                        if (pictureFile == null) {
                            Log.d("SwiftCamera", "Error creating media file, check storage permissions");
                            return;
                        }

                        camera.startPreview();
                        GPUImageFilter filter = getFilter(mFilterId);
                        mGPUImage.saveToPictures(data, filter, "SwiftCamera", pictureFile.getName(), new GPUImage.OnPictureSavedListener() {
                                    @Override
                                    public void onPictureSaved(final Uri uri) {
                                        System.gc();
                                        mCameraView.updateLastPhoto();
                                    }
                                });
                    }
                });
    }

    private static final int MEDIA_TYPE_IMAGE = 1;
    private static final int MEDIA_TYPE_VIDEO = 2;

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
                            //initCamera();//实现相机的参数初始化
                        }
                    }
                });
            }
        };
    }

    private void initCamera() {
        mParameters = mCameraInst.getParameters();
        mParameters.setPictureFormat(PixelFormat.JPEG);

        setUpPicSize();
        setUpPreviewSize();

        if (mAdapterSize != null) {
            mParameters.setPictureSize(mAdapterSize.width, mAdapterSize.height);
        }
        if (mPreviewSize != null) {
            mParameters.setPreviewSize(mPreviewSize.width, mPreviewSize.height);
            mCameraView.updatePreviewRatio(mPreviewSize.width > mPreviewSize.height ? ((float) mPreviewSize.height / (float)mPreviewSize.width) : ((float)mPreviewSize.width / (float)mPreviewSize.height));
        }

        mParameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);//1连续对焦

        //int orientation = mCameraHelper.getCameraDisplayOrientation((Activity) mContext, mCurrentCameraId);
        //mCameraInst.setDisplayOrientation(orientation);

        if (!isFrontCamera()) {
            if (canSwitchFlashMode()) {
                mParameters.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
                mCameraView.setFlashViewResourceId(R.mipmap.camera_flash_off);
            } else {
                mCameraView.setFlashViewResourceId(-1);
            }
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

    private void setUpPicSize() {
        if (mAdapterSize == null) {
            mAdapterSize = findBestPictureResolution();
        }
    }

    private void setUpPreviewSize() {
        if (mPreviewSize == null) {
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
            return supportedPreviewResolutions.get(0);
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
                initCamera();
                Camera.CameraInfo info = new Camera.CameraInfo();
                Camera.getCameraInfo(mCurrentCameraId, info);
                mGPUImage.setUpCamera(mCameraInst, Rotation.fromInt(info.orientation), isFrontCamera(), !isFrontCamera());
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

    @Override
    public OrientationEventListener getOrientationEventListener() {
        return mOrientationListener;
    }
}
