package com.example.sidapplication;

<<<<<<< HEAD
import androidx.annotation.NonNull;
import androidx.annotation.OptIn;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.media.Image;
import android.os.Bundle;
=======
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
>>>>>>> 972c3405a8efe59f8de52140939afd25a9973e23
import android.util.Log;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;
<<<<<<< HEAD
import android.widget.Button;
=======
>>>>>>> 972c3405a8efe59f8de52140939afd25a9973e23
import android.widget.ImageButton;
import android.widget.Toast;

import org.opencv.android.CameraActivity;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
<<<<<<< HEAD
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.FaceDetectorYN;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutionException;
=======
import org.opencv.core.Size;
import org.opencv.objdetect.FaceDetectorYN;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.List;

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


>>>>>>> 972c3405a8efe59f8de52140939afd25a9973e23

public class DiagnosisCameraActivity extends CameraActivity implements CameraBridgeViewBase.CvCameraViewListener2 {
    private static final String TAG = "OCVSample::Activity";

    private Mat                    mRgba;
    private Mat                    mBgr;
    private Mat                    mBgrScaled;
    private Size                   mInputSize = null;
    private float                  mScale = 2.f;
    private MatOfByte              mModelBuffer;
    private MatOfByte              mConfigBuffer;
    private FaceDetectorYN         mFaceDetector;
    private Mat                    mFaces;

    private CameraBridgeViewBase mOpenCvCameraView;

<<<<<<< HEAD
=======
    private static final int PICK_IMAGE = 1;

>>>>>>> 972c3405a8efe59f8de52140939afd25a9973e23
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
        setContentView(R.layout.activity_diagnosis_camera);
        mOpenCvCameraView = (CameraBridgeViewBase) findViewById(R.id.diagnosispreview);
        mOpenCvCameraView.setVisibility(SurfaceView.VISIBLE);
        mOpenCvCameraView.setCvCameraViewListener(this);
<<<<<<< HEAD
=======

        ImageButton diagnosisgo = findViewById(R.id.diagnosis_save);
        ImageButton diagnosisgo2 = findViewById(R.id.diagnosis_gallery);

        diagnosisgo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Mat currentFrame = mRgba;

                // 현재 프레임의 크기 확인
                if (currentFrame != null && currentFrame.cols() > 0 && currentFrame.rows() > 0) {
                    // Mat을 Bitmap으로 변환
                    Bitmap frameBitmap = Bitmap.createBitmap(currentFrame.cols(), currentFrame.rows(), Bitmap.Config.ARGB_8888);
                    org.opencv.android.Utils.matToBitmap(currentFrame, frameBitmap);

                    // 크기를 조정할 비율 설정 (예: 0.5로 설정하면 50% 축소)
                    float scaleFactor = 0.5f; // 원하는 축소 비율
                    int newWidth = Math.round(frameBitmap.getWidth() * scaleFactor);
                    int newHeight = Math.round(frameBitmap.getHeight() * scaleFactor);

                    // Bitmap 크기 조정
                    Bitmap scaledBitmap = Bitmap.createScaledBitmap(frameBitmap, newWidth, newHeight, true);

                    // Bitmap을 byte 배열로 변환 (Intent에 추가하기 위해)
                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    scaledBitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
                    byte[] frameBitmapData = stream.toByteArray();

                    Intent intent = new Intent(DiagnosisCameraActivity.this, CheckDiagnosisPhotoActivity.class);
                    intent.putExtra("captured_image", frameBitmapData); // Bitmap이 아닌 byte 배열을 전달

                    startActivity(intent);

                    // Bitmap 메모리 해제
                    frameBitmap.recycle();
                    scaledBitmap.recycle();
                } else {
                    Log.e("DiagnosisCameraActivity", "Current frame is invalid: width=" + (currentFrame != null ? currentFrame.cols() : "null") + ", height=" + (currentFrame != null ? currentFrame.rows() : "null"));
                    Toast.makeText(DiagnosisCameraActivity.this, "현재 프레임의 크기가 유효하지 않습니다.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        diagnosisgo2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, PICK_IMAGE);
            }
        });
    }

    // 갤러리에서 선택한 이미지 처리
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE && resultCode == RESULT_OK && data != null) {
            Uri selectedImageUri = data.getData();

            try {
                // URI를 Bitmap으로 변환
                Bitmap selectedBitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImageUri);

                // Bitmap 크기 조정
                float scaleFactor = 0.5f; // 원하는 축소 비율
                int newWidth = Math.round(selectedBitmap.getWidth() * scaleFactor);
                int newHeight = Math.round(selectedBitmap.getHeight() * scaleFactor);
                Bitmap scaledBitmap = Bitmap.createScaledBitmap(selectedBitmap, newWidth, newHeight, true);

                // Bitmap을 byte 배열로 변환 (Intent에 추가하기 위해)
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                scaledBitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
                byte[] imageData = stream.toByteArray();

                // 다음 Activity로 Intent 전송
                Intent intent = new Intent(DiagnosisCameraActivity.this, CheckDiagnosisPhotoActivity.class);
                intent.putExtra("captured_image", imageData);
                startActivity(intent);

                // Bitmap 메모리 해제
                selectedBitmap.recycle();
                scaledBitmap.recycle();
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(DiagnosisCameraActivity.this, "이미지 처리 중 오류가 발생했습니다.", Toast.LENGTH_SHORT).show();
            }
        }
>>>>>>> 972c3405a8efe59f8de52140939afd25a9973e23
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

    }

    @Override
    public Mat onCameraFrame(CameraBridgeViewBase.CvCameraViewFrame inputFrame) {
<<<<<<< HEAD
        return inputFrame.rgba();
=======
        mRgba = inputFrame.rgba();
        return mRgba;
>>>>>>> 972c3405a8efe59f8de52140939afd25a9973e23
    }
}
