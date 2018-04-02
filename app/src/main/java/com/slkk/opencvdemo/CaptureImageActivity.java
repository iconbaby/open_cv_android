package com.slkk.opencvdemo;

import android.content.ContentValues;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.JavaCameraView;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.Mat;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.videoio.Videoio;

import java.io.File;
/*
* captureImgae with opencv
*
* */
public class CaptureImageActivity extends AppCompatActivity implements CameraBridgeViewBase.CvCameraViewListener2 {
    private static final String TAG = "CaptureImageActivity";
    private Mat mBgr;

    static {
        System.loadLibrary("native-lib");
    }

    private JavaCameraView cameraView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_capture_image);
        cameraView = findViewById(R.id.cameraView);
        cameraView.setVisibility(View.VISIBLE);
        cameraView.setCvCameraViewListener(this);
    }

    private BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) {
            switch (status) {
                case LoaderCallbackInterface.SUCCESS:
                    Log.i(TAG, "OpenCV loaded successfully");
                    cameraView.enableView();
                    mBgr = new Mat();
                    break;
                default:
                    super.onManagerConnected(status);
                    break;
            }
        }
    };

    @Override
    protected void onResume() {
        super.onResume();
        if (!OpenCVLoader.initDebug()) {
            Log.d(TAG, "Internal OpenCV library not found. Using OpenCV Manager for initialization");
            OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION, this, mLoaderCallback);
        } else {
            Log.d(TAG, "OpenCV library found inside package. Using it!");
            mLoaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (cameraView != null)
            cameraView.disableView();
    }

    public void onDestroy() {
        super.onDestroy();
        if (cameraView != null)
            cameraView.disableView();
    }

    @Override
    public void onCameraViewStarted(int width, int height) {

    }

    @Override
    public void onCameraViewStopped() {

    }

    @Override
    public Mat onCameraFrame(CameraBridgeViewBase.CvCameraViewFrame inputFrame) {
        final Mat captureImage = inputFrame.rgba();
//        takePhoto(captureImage);

        return inputFrame.rgba();
    }

    private void takePhoto(Mat captureImage) {
        final long currentTimeMillis = System.currentTimeMillis();
        final String appName = getString(R.string.app_name);
        final String galleryPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).toString();
        Log.i(TAG, "takePhoto: " + galleryPath);
        final String albumPath = galleryPath + "/" + appName;
        final String photoPath = albumPath + "/" + currentTimeMillis + ".jpg";
        final ContentValues values = new ContentValues();
        values.put(MediaStore.MediaColumns.DATA, photoPath);
        values.put(MediaStore.Images.Media.MIME_TYPE, "11");
        values.put(MediaStore.Images.Media.TITLE, appName);
        values.put(MediaStore.Images.Media.DESCRIPTION, appName);
        values.put(MediaStore.Images.Media.DATE_TAKEN, currentTimeMillis);
        //确保相片目录存在
        File album = new File(albumPath);
        if (!album.isDirectory() && album.mkdirs()) {
            Log.e(TAG, "Failed to create album diretory at" + albumPath);
            return;
        }

        //尝试创建照片
        Imgproc.cvtColor(captureImage, mBgr, Imgproc.COLOR_RGBA2BGR, 3);
        if (!Imgcodecs.imwrite(photoPath, mBgr)) {
            Log.e(TAG, "Failed to save photo to " + photoPath);//图片没有保存到
        }

    }

    native void capImage();
}
