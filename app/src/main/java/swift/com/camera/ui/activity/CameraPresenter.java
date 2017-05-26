package swift.com.camera.ui.activity;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.hardware.Camera;
import android.location.Location;
import android.location.LocationManager;
import android.media.CamcorderProfile;
import android.media.MediaRecorder;
import android.net.Uri;
import android.opengl.GLSurfaceView;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.OrientationEventListener;
import android.view.SurfaceHolder;

import com.coremedia.iso.IsoFile;
import com.coremedia.iso.boxes.Box;
import com.coremedia.iso.boxes.ChunkOffsetBox;
import com.coremedia.iso.boxes.SampleTableBox;
import com.coremedia.iso.boxes.StaticChunkOffsetBox;
import com.coremedia.iso.boxes.TrackBox;
import com.coremedia.iso.boxes.UserDataBox;
import com.googlecode.mp4parser.boxes.apple.AppleGPSCoordinatesBox;
import com.googlecode.mp4parser.util.Matrix;
import com.googlecode.mp4parser.util.Path;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
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
import swift.com.camera.R;
import swift.com.camera.utils.CameraHelper;
import swift.com.camera.utils.PluginFilterHelper;
import swift.com.camera.utils.PluginFilterPackage;
import swift.com.camera.utils.ScreenUtils;

/**
 * Created by junnikokuki on 2017/4/12.
 */

public class CameraPresenter implements CameraContract.Presenter, SurfaceHolder.Callback, GPUImageVideo.OnRecordFinishedListener {
    private final CameraContract.View mCameraView;
    private final CameraContract.Support mCameraSupport;
    private final Context mContext;

    private OrientationEventListener mOrientationListener;

    private GPUImage mGPUImage;
    private GPUImageFilter mFilter;
    private String mFilterId = "";

    private CameraHelper mCameraHelper;

    private Camera.Parameters mParameters = null;
    private Camera mCameraInst = null;

    private Camera.Size mAdapterSize = null;
    private Camera.Size mPreviewSize = null;
    private int mCurrentCameraId = 0;  //1是前置 0是后置
    private int mCurOrientation = 0;

    private int mRecordOrientation = 0;

    private boolean mRecorderMode = false;

    public CameraPresenter(CameraContract.View cameraView, CameraContract.Support cameraSupport) {
        mCameraView = cameraView;
        mCameraSupport = cameraSupport;
        mContext = (Context) cameraView;
        mGPUImage = new GPUImage(mContext);
        mCameraHelper = new CameraHelper(mContext);
        mOrientationListener = new OrientationEventListener(mContext) {
            @Override
            public void onOrientationChanged(int i) {
                int rotation = 0;
                if (i > 315 || i <= 45) {
                    rotation = 90;
                } else if (i > 45 && i <= 135) {
                    rotation = 180;
                } else if (i > 135 && i <= 225) {
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
    public void toggleRecord() {
        if (!((GPUImageVideo)mGPUImage).isRecording()) {
            final File videoFile = getOutputMediaFile(MEDIA_TYPE_VIDEO, true);
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
            profile.videoFrameRate = cn.m15.gpuimage.video.EncoderConfig.VIDEO_FRAME_RATE;
            profile.fileFormat = MediaRecorder.OutputFormat.MPEG_4;
            profile.videoCodec = MediaRecorder.VideoEncoder.H264;
            profile.audioCodec = MediaRecorder.AudioEncoder.AAC;

            mCameraInst.setParameters(params);
            ((GPUImageVideo)mGPUImage).startRecord(videoFile, profile.videoFrameWidth, profile.videoFrameHeight, this);
            mCameraView.updateRecordViews(CameraContract.RecordState.START);

            int rotation = (mCurOrientation + 270) % 360;
            mRecordOrientation = rotation;
        } else {
            ((GPUImageVideo)mGPUImage).stopRecord();
            mCameraView.updateRecordViews(CameraContract.RecordState.STOP);
        }
    }

    @Override
    public void switchMode() {
        mRecorderMode = !mRecorderMode;
        if (mRecorderMode) {
            mGPUImage = new GPUImageVideo(mContext);
        } else {
            mGPUImage = new GPUImage(mContext);
        }
        if (mFilter != null) {
            mGPUImage.setFilter(mFilter);
        }
    }

    @Override
    public boolean isRecorderMode() {
        return mRecorderMode;
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
                        final File pictureFile = getOutputMediaFile(MEDIA_TYPE_IMAGE, false);
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

    private static File getOutputMediaFile(final int type, boolean temp) {
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
                    "IMG_" + timeStamp + (temp ? "_temp" : "") + ".jpg");
        } else if (type == MEDIA_TYPE_VIDEO) {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator +
                    "VID_" + timeStamp + (temp ? "_temp" : "") + ".mp4");
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
            if (mRecorderMode) {
                mPreviewSize = findBestRecordResolution();
            } else {
                mPreviewSize = findBestPreviewResolution();
            }
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

    private Camera.Size findBestRecordResolution() {
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
        Log.v(TAG, "Supported record resolutions: " + previewResolutionSb);

        return supportedPreviewResolutions.get(0);
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

    private void parseVideoFile() {

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
            if (canZoom()) {
                mCameraView.updateZoom(currentZoom(), maxZoom());
            }
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

    @Override
    public void onRecordFinished(final File outputRecFile) {
        Thread thread = new Thread(new Runnable() {
            public void run() {
                long originalUserDataSize = 0;
                long finalUserDataSize = 0;

                try {
                    Matrix rotation = Matrix.ROTATE_0;
                    if (mRecordOrientation == 90) {
                        rotation = Matrix.ROTATE_90;
                    } else if (mRecordOrientation == 180) {
                        rotation = Matrix.ROTATE_180;
                    } else if (mRecordOrientation == 270) {
                        rotation = Matrix.ROTATE_270;
                    }

                    IsoFile isoFile = new IsoFile(outputRecFile.getAbsolutePath());
                    List<TrackBox> trackBoxes = isoFile.getMovieBox().getBoxes(TrackBox.class);
                    for (TrackBox trackBox : trackBoxes) {
                        trackBox.getTrackHeaderBox().setMatrix(rotation);
                    }

                    LocationManager locationManager = (LocationManager) mContext.getSystemService(Context.LOCATION_SERVICE);
                    if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                        Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                        if (location != null) {
                            double latitude = location.getLatitude();
                            double longitude = location.getLongitude();

                            if (latitude != 0 && longitude != 0) {
                                UserDataBox userDataBox = Path.getPath(isoFile, "/moov/udta");
                                if (userDataBox == null) {
                                    userDataBox = new UserDataBox();
                                    isoFile.getMovieBox().addBox(userDataBox);
                                } else {
                                    originalUserDataSize = userDataBox.getSize();
                                }

                                AppleGPSCoordinatesBox locationBox = null;
                                List<AppleGPSCoordinatesBox> locationBoxes = isoFile.getMovieBox().getBoxes(AppleGPSCoordinatesBox.class);
                                if (locationBoxes.size() > 0) {
                                    locationBox = locationBoxes.get(0);
                                } else {
                                    locationBox = new AppleGPSCoordinatesBox();
                                    userDataBox.addBox(locationBox);
                                }

                                locationBox.setValue((longitude >= 0 ? "+" : "") + String.format("%.4f", longitude) + (latitude >= 0 ? "+" : "") + String.format("%.4f", latitude) + "/");

                                finalUserDataSize = userDataBox.getSize();
                            }
                        }
                    }

                    if (needsOffsetCorrection(isoFile)) {
                        correctChunkOffsets(isoFile, finalUserDataSize - originalUserDataSize);
                    }

                    String finalFileName = outputRecFile.getAbsolutePath();
                    finalFileName = finalFileName.replace("_temp", "");
                    File finalFile = new File(finalFileName);

                    FileOutputStream videoFileOutputStream = new FileOutputStream(finalFile.getAbsoluteFile());
                    isoFile.getBox(videoFileOutputStream.getChannel());

                    isoFile.close();
                    videoFileOutputStream.close();

                    outputRecFile.delete();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                Handler mainHandler = new Handler(Looper.getMainLooper());
                mainHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        mCameraView.updateRecordViews(CameraContract.RecordState.IDLE);
                    }
                });
            }
        });
        thread.start();
    }

    private static boolean needsOffsetCorrection(IsoFile isoFile) {
        if (Path.getPaths(isoFile, "mdat").size() > 1) {
            throw new RuntimeException("There might be the weird case that a file has two mdats. One before" +
                    " moov and one after moov. That would need special handling therefore I just throw an " +
                    "exception here. ");
        }

        if (Path.getPaths(isoFile, "moof").size() > 0) {
            throw new RuntimeException("Fragmented MP4 files need correction, too. (But I would need to look where)");
        }

        for (Box box : isoFile.getBoxes()) {
            if ("mdat".equals(box.getType())) {
                return false;
            }
            if ("moov".equals(box.getType())) {
                return true;
            }
        }
        throw new RuntimeException("Hmmm - shouldn't happen");
    }

    private static void correctChunkOffsets(IsoFile tempIsoFile, long correction) {
        List<SampleTableBox> sampleTableBoxes = Path.getPaths(tempIsoFile, "/moov[0]/trak/mdia[0]/minf[0]/stbl[0]");

        for (SampleTableBox sampleTableBox : sampleTableBoxes) {

            List<Box> stblChildren = new ArrayList<Box>(sampleTableBox.getBoxes());
            ChunkOffsetBox chunkOffsetBox = Path.getPath(sampleTableBox, "stco");
            if (chunkOffsetBox == null) {
                stblChildren.remove(Path.getPath(sampleTableBox, "co64"));
            }
            stblChildren.remove(chunkOffsetBox);

            assert chunkOffsetBox != null;
            long[] cOffsets = chunkOffsetBox.getChunkOffsets();
            for (int i = 0; i < cOffsets.length; i++) {
                cOffsets[i] += correction;
            }

            StaticChunkOffsetBox cob = new StaticChunkOffsetBox();
            cob.setChunkOffsets(cOffsets);
            stblChildren.add(cob);
            sampleTableBox.setBoxes(stblChildren);
        }
    }
}
