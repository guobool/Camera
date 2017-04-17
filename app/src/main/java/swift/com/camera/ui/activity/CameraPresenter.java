package swift.com.camera.ui.activity;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Camera;
import android.net.Uri;
import android.opengl.GLSurfaceView;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import cn.m15.gpuimage.GPUImage;
import cn.m15.gpuimage.GPUImageFilter;
import swift.com.camera.utils.CameraHelper;
import swift.com.camera.utils.CameraHelper.CameraInfo2;
import swift.com.camera.utils.GPUImageFilterTools;

/**
 * Created by junnikokuki on 2017/4/12.
 */

public class CameraPresenter implements CameraContract.Presenter {
    private final CameraContract.View mCameraView;
    private final CameraContract.Support mCameraSupport;
    private final Context mContext;

    private GPUImage mGPUImage;
    private GPUImageFilter mFilter;
    private GPUImageFilterTools.FilterAdjuster mFilterAdjuster;

    private CameraHelper mCameraHelper;
    private CameraLoader mCamera;

    public CameraPresenter(CameraContract.View cameraView, CameraContract.Support cameraSupport) {
        mCameraView = cameraView;
        mCameraSupport = cameraSupport;
        mContext = (Context) cameraView;

        mGPUImage = new GPUImage(mContext);

        mCameraHelper = new CameraHelper(mContext);
        mCamera = new CameraLoader();
    }

    @Override
    public void onResume() {
        mCamera.onResume();
    }

    @Override
    public void onPause() {
        mCamera.onPause();
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
        mCamera.switchCamera();
    }

    @Override
    public boolean canSwitchFlashMode() {
        if (mCamera.mCameraInstance != null && mCamera.mCameraInstance.getParameters().getFlashMode() != null) {
            return true;
        }

        return false;
    }

    @Override
    public void switchFlashMode() {
        Camera.Parameters params = mCamera.mCameraInstance.getParameters();
        List<String> supportedFlashModes = mCamera.mCameraInstance.getParameters().getSupportedFlashModes();
        int currentIndex = supportedFlashModes.indexOf(params.getFlashMode());
        currentIndex ++;
        currentIndex = currentIndex % supportedFlashModes.size();
        params.setFlashMode(supportedFlashModes.get(currentIndex));
        mCamera.mCameraInstance.setParameters(params);
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
        if (mCamera.mCameraInstance.getParameters().getFocusMode().equals(
                Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE)) {
            takePicture();
        } else {
            mCamera.mCameraInstance.autoFocus(new Camera.AutoFocusCallback() {

                @Override
                public void onAutoFocus(final boolean success, final Camera camera) {
                    takePicture();
                }
            });
        }
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
        Camera.Parameters params = mCamera.mCameraInstance.getParameters();
        params.setRotation(90);
        mCamera.mCameraInstance.setParameters(params);
        for (Camera.Size size : params.getSupportedPictureSizes()) {
            Log.i("ASDF", "Supported: " + size.width + "x" + size.height);
        }
        mCamera.mCameraInstance.takePicture(null, null,
                new Camera.PictureCallback() {

                    @Override
                    public void onPictureTaken(byte[] data, final Camera camera) {

                        final File pictureFile = getOutputMediaFile(MEDIA_TYPE_IMAGE);
                        if (pictureFile == null) {
                            Log.d("ASDF",
                                    "Error creating media file, check storage permissions");
                            return;
                        }

                        try {
                            FileOutputStream fos = new FileOutputStream(pictureFile);
                            fos.write(data);
                            fos.close();
                        } catch (FileNotFoundException e) {
                            Log.d("ASDF", "File not found: " + e.getMessage());
                        } catch (IOException e) {
                            Log.d("ASDF", "Error accessing file: " + e.getMessage());
                        }

                        data = null;
                        final Bitmap bitmap = BitmapFactory.decodeFile(pictureFile.getAbsolutePath());
                        // mGPUImage.setImage(bitmap);
                        mCameraView.setGLSurfaceViewRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
                        mGPUImage.saveToPictures(bitmap, "GPUImage",
                                System.currentTimeMillis() + ".jpg",
                                new GPUImage.OnPictureSavedListener() {

                                    @Override
                                    public void onPictureSaved(final Uri
                                                                       uri) {
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
                Environment.DIRECTORY_PICTURES), "MyCameraApp");
        // This location works best if you want the created images to be shared
        // between applications and persist after your app has been uninstalled.

        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                Log.d("MyCameraApp", "failed to create directory");
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

    private class CameraLoader {

        private int mCurrentCameraId = 0;
        private Camera mCameraInstance;

        public void onResume() {
            setUpCamera(mCurrentCameraId);
        }

        public void onPause() {
            releaseCamera();
        }

        public void switchCamera() {
            releaseCamera();
            mCurrentCameraId = (mCurrentCameraId + 1) % mCameraHelper.getNumberOfCameras();
            setUpCamera(mCurrentCameraId);
        }

        private void setUpCamera(final int id) {
            mCameraInstance = getCameraInstance(id);
            Camera.Parameters parameters = mCameraInstance.getParameters();
            // TODO adjust by getting supportedPreviewSizes and then choosing
            // the best one for screen size (best fill screen)
            if (parameters.getSupportedFocusModes().contains(
                    Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE)) {
                parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
            }
            mCameraInstance.setParameters(parameters);

            int orientation = mCameraHelper.getCameraDisplayOrientation(
                    (Activity) mContext, mCurrentCameraId);
            CameraInfo2 cameraInfo = new CameraInfo2();
            mCameraHelper.getCameraInfo(mCurrentCameraId, cameraInfo);
            boolean flipHorizontal = cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_FRONT;
            mGPUImage.setUpCamera(mCameraInstance, orientation, flipHorizontal, false);
        }

        /** A safe way to get an instance of the Camera object. */
        private Camera getCameraInstance(final int id) {
            Camera c = null;
            try {
                c = mCameraHelper.openCamera(id);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return c;
        }

        private void releaseCamera() {
            mCameraInstance.setPreviewCallback(null);
            mCameraInstance.release();
            mCameraInstance = null;
        }
    }
}
