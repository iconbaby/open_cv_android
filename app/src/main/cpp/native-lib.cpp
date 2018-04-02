#include <jni.h>
#include <string>
#include "com_slkk_opencvdemo_JniActivity.h"
#include "android/bitmap.h"
#include <opencv2/opencv.hpp>
#include <opencv2/core.hpp>
#include <opencv2/imgproc.hpp>
#include <opencv2/features2d.hpp>
#include <vector>

using namespace cv;
using namespace std;

#define camera_name "摄像头"
#define picwindow_name "截图图片"

JNIEXPORT void JNICALL Java_com_slkk_opencvdemo_JniActivity_getEge(JNIEnv
                                                                   *env,
                                                                   jobject,
                                                                   jobject bitmap) {
    AndroidBitmapInfo info;
    void *pixels;
    CV_Assert(AndroidBitmap_getInfo(env, bitmap, &info) >= 0);
    CV_Assert(info.format == ANDROID_BITMAP_FORMAT_RGBA_8888 ||
              info.format == ANDROID_BITMAP_FORMAT_RGB_565);
    CV_Assert(AndroidBitmap_lockPixels(env, bitmap, &pixels) >= 0);
    CV_Assert(pixels);
    if (info.format == ANDROID_BITMAP_FORMAT_RGBA_8888) {
        Mat temp(info.height, info.width, CV_8UC4, pixels);
        Mat gray;
        cvtColor(temp, gray, COLOR_RGBA2GRAY);
        Canny(gray, gray, 3, 9, 3);
        cvtColor(gray, temp, COLOR_GRAY2RGBA);
    } else {
        Mat temp(info.height, info.width, CV_8UC2, pixels);
        Mat gray;
        cvtColor(temp, gray, COLOR_RGB2GRAY);
        Canny(gray, gray, 3, 9, 3);
        cvtColor(gray, temp, COLOR_GRAY2RGB);
    }
    AndroidBitmap_unlockPixels(env, bitmap);

}

JNIEXPORT void JNICALL
Java_com_slkk_opencvdemo_Tutorial2Activity_FindFeatures(JNIEnv *, jobject, jlong addrGray,
                                                        jlong addrRgba) {
    Mat &mGr = *(Mat *) addrGray;
    Mat &mRgb = *(Mat *) addrRgba;
    vector<KeyPoint> v;

    Ptr<FeatureDetector> detector = FastFeatureDetector::create(50);
    detector->detect(mGr, v);
    for (unsigned int i = 0; i < v.size(); i++) {
        const KeyPoint &kp = v[i];
        circle(mRgb, Point(kp.pt.x, kp.pt.y), 10, Scalar(255, 0, 0, 255));
    }
}

JNIEXPORT void JNICALL Java_com_slkk_opencvdemo_CaptureImageActivity_capImage(JNIEnv *, jobject) {
    VideoCapture capture(0);
    if (!capture.isOpened()) {
        cout << "摄像头打开失败!" << endl;

    }
    Mat frame;
    char picture_name[200];//用于存放保存图片的文件名
    int i = 0;//计数
    namedWindow(camera_name);
    namedWindow(picwindow_name);
    while (1) {

        capture >> frame;
        imshow(camera_name, frame);
        if (i <= 10) {
            sprintf(picture_name, "/data/local/pic%d.jpg", i++);
            //sprintf(picture_name, "pic%d.jpg", i++);//若保存在根目录,这样即可
            imwrite(picture_name, frame);
            imshow(picwindow_name, frame);
        }
    }
}

