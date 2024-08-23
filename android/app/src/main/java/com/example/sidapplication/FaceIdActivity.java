package com.example.sidapplication;

import android.graphics.Bitmap;
import android.os.Bundle;

import android.content.Intent;
import org.opencv.android.CameraActivity;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewFrame;
import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.CvException;
import org.opencv.core.Mat;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewListener2;

import android.util.Log;
import android.view.SurfaceView;
import android.view.WindowManager;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.util.Collections;
import java.util.List;

import java.lang.Math;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.List;

import org.opencv.android.CameraActivity;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewFrame;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewListener2;
import org.opencv.objdetect.FaceDetectorYN;
import org.opencv.imgproc.Imgproc;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

public class FaceIdActivity extends CameraActivity implements CvCameraViewListener2 {
    private static final String TAG = "OCV";

    private static final Scalar BOX_COLOR         = new Scalar(0, 255, 0);
    private static final Scalar    RIGHT_EYE_COLOR   = new Scalar(255, 0, 0);
    private static final Scalar    LEFT_EYE_COLOR    = new Scalar(0, 0, 255);
    private static final Scalar    NOSE_TIP_COLOR    = new Scalar(0, 255, 0);
    private static final Scalar    MOUTH_RIGHT_COLOR = new Scalar(255, 0, 255);
    private static final Scalar    MOUTH_LEFT_COLOR  = new Scalar(0, 255, 255);

    private Mat mRgba;
    private Mat                    mBgr;
    private Mat                    mBgrScaled;
    private Size mInputSize = null;
    private float                  mScale = 2.f;
    private MatOfByte mModelBuffer;
    private MatOfByte              mConfigBuffer;
    private FaceDetectorYN mFaceDetector;
    private Mat                    mFaces;

    long startTime = 0; // 시작 시간
    boolean isFaceInFrame = false; // 얼굴이 프레임 내에 있는지 여부
    int validFrames = 0; // 유효한 프레임 카운트

    private CameraBridgeViewBase mOpenCvCameraView;

    public void Tutorial1Activity() {
        Log.i(TAG, "Instantiated new " + this.getClass());
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.i(TAG, "called onCreate");
        super.onCreate(savedInstanceState);

        if (OpenCVLoader.initLocal()) {
            Log.i(TAG, "OpenCV loaded successfully");
        } else {
            Log.e(TAG, "OpenCV initialization failed!");
            (Toast.makeText(this, "OpenCV initialization failed!", Toast.LENGTH_LONG)).show();
            return;
        }

        byte[] buffer;
        try {
            // load cascade file from application resources
            InputStream is = getResources().openRawResource(R.raw.face_detection_yunet_2023mar);

            int size = is.available();
            buffer = new byte[size];
            int bytesRead = is.read(buffer);
            is.close();
        } catch (IOException e) {
            e.printStackTrace();
            Log.e(TAG, "Failed to ONNX model from resources! Exception thrown: " + e);
            (Toast.makeText(this, "Failed to ONNX model from resources!", Toast.LENGTH_LONG)).show();
            return;
        }

        mModelBuffer = new MatOfByte(buffer);
        mConfigBuffer = new MatOfByte();

        mFaceDetector = FaceDetectorYN.create("onnx", mModelBuffer, mConfigBuffer, new Size(320, 320));
        if (mFaceDetector == null) {
            Log.e(TAG, "Failed to create FaceDetectorYN!");
            (Toast.makeText(this, "Failed to create FaceDetectorYN!", Toast.LENGTH_LONG)).show();
            return;
        } else
            Log.i(TAG, "FaceDetectorYN initialized successfully!");


        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_opencv);
        mOpenCvCameraView = (CameraBridgeViewBase) findViewById(R.id.tutorial1_activity_java_surface_view);
        mOpenCvCameraView.setCameraIndex(CameraBridgeViewBase.CAMERA_ID_FRONT);
        mOpenCvCameraView.setVisibility(SurfaceView.VISIBLE);
        mOpenCvCameraView.setCvCameraViewListener(this);
    }

    @Override
    public void onPause()
    {
        super.onPause();
        if (mOpenCvCameraView != null)
            mOpenCvCameraView.disableView();
    }

    @Override
    public void onResume()
    {
        super.onResume();
        if (mOpenCvCameraView != null)
            mOpenCvCameraView.enableView();
    }

    @Override
    protected List<? extends CameraBridgeViewBase> getCameraViewList() {
        return Collections.singletonList(mOpenCvCameraView);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mOpenCvCameraView != null)
            mOpenCvCameraView.disableView();
    }

    public void onCameraViewStarted(int width, int height) {
        mRgba = new Mat();
        mBgr = new Mat();
        mBgrScaled = new Mat();
        mFaces = new Mat();
    }

    public void onCameraViewStopped() {
        mRgba.release();
        mBgr.release();
        mBgrScaled.release();
        mFaces.release();
    }

    public void visualize(Mat rgba, Mat faces) {

        int thickness = 2;
        float[] faceData = new float[faces.cols() * faces.channels()];

        for (int i = 0; i < faces.rows(); i++)
        {
            faces.get(i, 0, faceData);

            Log.d(TAG, "Detected face (" + faceData[0] + ", " + faceData[1] + ", " +
                    faceData[2] + ", " + faceData[3] + ")");

            // 사각형 그리기
            Rect faceRect = new Rect(
                    Math.round(mScale * faceData[0]),
                    Math.round(mScale * faceData[1]),
                    Math.round(mScale * faceData[2]),
                    Math.round(mScale * faceData[3])
            );

            // Draw bounding box
            Imgproc.rectangle(rgba, faceRect, BOX_COLOR, thickness);

            // Draw landmarks
            Point rightEye = new Point(Math.round(mScale * faceData[4]), Math.round(mScale * faceData[5]));
            Point leftEye = new Point(Math.round(mScale * faceData[6]), Math.round(mScale * faceData[7]));
            Point nose = new Point(Math.round(mScale * faceData[8]), Math.round(mScale * faceData[9]));
            Point mouthRight = new Point(Math.round(mScale * faceData[10]), Math.round(mScale * faceData[11]));
            Point mouthLeft = new Point(Math.round(mScale * faceData[12]), Math.round(mScale * faceData[13]));

            // 랜드마크 그리기
            Imgproc.circle(rgba, rightEye, 2, RIGHT_EYE_COLOR, thickness);
            Imgproc.circle(rgba, leftEye, 2, LEFT_EYE_COLOR, thickness);
            Imgproc.circle(rgba, nose, 2, NOSE_TIP_COLOR, thickness);
            Imgproc.circle(rgba, mouthRight, 2, MOUTH_RIGHT_COLOR, thickness);
            Imgproc.circle(rgba, mouthLeft, 2, MOUTH_LEFT_COLOR, thickness);


            // 눈, 코, 입이 모두 프레임 안에 있는지 체크
            if (isPointInFrame(rightEye) && isPointInFrame(leftEye)
                    && isPointInFrame(nose) && isPointInFrame(mouthRight)
                    && isPointInFrame(mouthLeft)) {

                if (!isFaceInFrame) {
                    startTime = System.currentTimeMillis(); // 시작 시간 기록
                    isFaceInFrame = true;
                }

                // 3초 동안 체크
                if (System.currentTimeMillis() - startTime >= 3000) {
                    //mOpenCvCameraView.disableView();

                    long mNow;
                    Date mDate;
                    SimpleDateFormat mFormat = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss"); // 포맷 수정

                    mNow = System.currentTimeMillis();
                    mDate = new Date(mNow);
                    String formattedDate = mFormat.format(mDate); // 포맷된 문자열 생성

                    // 얼굴 영역 잘라내기
                    Mat faceROI = new Mat(rgba, faceRect);

                    // faceROI를 Bitmap으로 변환
                    Bitmap faceBitmap = Bitmap.createBitmap(faceROI.cols(), faceROI.rows(), Bitmap.Config.ARGB_8888);
                    org.opencv.android.Utils.matToBitmap(faceROI, faceBitmap);

                    // Bitmap을 byte 배열로 변환 (Intent에 추가하기 위해)
                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    faceBitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
                    byte[] faceBitmapData = stream.toByteArray();

                    // Intent에 추가
                    Intent intent = new Intent(FaceIdActivity.this, CheckPhotoActivity.class);

                    HashMap<String, String> userInfo = (HashMap<String, String>) getIntent().getSerializableExtra("userInfo");
                    // 해시맵에 날짜 추가
                    if (userInfo != null) {
                        userInfo.put("formattedDate", formattedDate);
                    }

                    intent.putExtra("face_image", faceBitmapData);
                    intent.putExtra("userInfo", userInfo);

                    startActivity(intent);

                    // Bitmap 메모리 해제
                    faceBitmap.recycle();
                    faceROI.release(); // Mat 객체 메모리 해제
                    finish();
                }
            } else {
                isFaceInFrame = false; // 얼굴이 프레임에서 벗어남
            }
        }
    }

    private boolean isPointInFrame(Point point) {
        // 카메라 뷰의 크기 가져오기
        int viewWidth = mOpenCvCameraView.getWidth();
        int viewHeight = mOpenCvCameraView.getHeight();

        // 포인트가 카메라 뷰의 범위 내에 있는지 확인
        return point.x >= 0 && point.x < viewWidth && point.y >= 0 && point.y < viewHeight;
    }

    @Override
    public Mat onCameraFrame(CameraBridgeViewBase.CvCameraViewFrame inputFrame) {
        mRgba = inputFrame.rgba();

        Size inputSize = new Size(Math.round(mRgba.cols()/mScale), Math.round(mRgba.rows()/mScale));
        if (mInputSize == null || !mInputSize.equals(inputSize)) {
            mInputSize = inputSize;
            mFaceDetector.setInputSize(mInputSize);
        }

        Imgproc.cvtColor(mRgba, mBgr, Imgproc.COLOR_RGBA2BGR);
        Imgproc.resize(mBgr, mBgrScaled, mInputSize);

        if (mFaceDetector != null) {
            int status = mFaceDetector.detect(mBgrScaled, mFaces);
            Log.d(TAG, "Detector returned status " + status);
            visualize(mRgba, mFaces);
        }

        return mRgba;

        //return inputFrame.rgba();
    }
}
