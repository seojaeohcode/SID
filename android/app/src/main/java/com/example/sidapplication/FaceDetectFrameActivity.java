package com.example.sidapplication;

import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.media.Image;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.OptIn;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.AspectRatio;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ExperimentalGetImage;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.ImageProxy;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.app.ActivityCompat;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;

public class FaceDetectFrameActivity extends AppCompatActivity {
    PreviewView previewView;
    Button startButton;
    Button stopButton;
    ImageView imageView;
    String TAG = "MainActivity";
    ProcessCameraProvider processCameraProvider;
    int lensFacing = CameraSelector.LENS_FACING_FRONT;
    //int lensFacing = CameraSelector.LENS_FACING_BACK;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_testface);

        previewView = findViewById(R.id.previewView);
        startButton = findViewById(R.id.startButton);
        stopButton = findViewById(R.id.stopButton);
        imageView = findViewById(R.id.imageView);

        ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.CAMERA}, 1);

        try {
            processCameraProvider = ProcessCameraProvider.getInstance(this).get();
        }
        catch (ExecutionException e) {
            e.printStackTrace();
        }
        catch (InterruptedException e) {
            e.printStackTrace();
        }

        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ActivityCompat.checkSelfPermission(FaceDetectFrameActivity.this, android.Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                    bindPreview();
                    bindImageAnalysis();
                }
            }
        });

        stopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                processCameraProvider.unbindAll();
            }
        });
    }

    void bindPreview() {
        previewView.setScaleType(PreviewView.ScaleType.FIT_CENTER);
        CameraSelector cameraSelector = new CameraSelector.Builder()
                .requireLensFacing(lensFacing)
                .build();
        Preview preview = new Preview.Builder()
                .setTargetAspectRatio(AspectRatio.RATIO_4_3) //디폴트 표준 비율
                .build();
        preview.setSurfaceProvider(previewView.getSurfaceProvider());

        processCameraProvider.bindToLifecycle(this, cameraSelector, preview);
    }

    void bindImageAnalysis() {
        CameraSelector cameraSelector = new CameraSelector.Builder()
                .requireLensFacing(lensFacing)
                .build();
        ImageAnalysis imageAnalysis = new ImageAnalysis.Builder()
                .build();
        imageAnalysis.setAnalyzer(Executors.newSingleThreadExecutor(),
                new ImageAnalysis.Analyzer() {
                    @OptIn(markerClass = ExperimentalGetImage.class)
                    @Override
                    public void analyze(@NonNull ImageProxy image) {
                        /*
                        @SuppressLint("UnsafeExperimentalUsageError")
                        Image mediaImage = image.getImage();
                        */
                        ///*
                        @SuppressLint("UnsafeExperimentalUsageError")
                        Image mediaImage = image.getImage();
                        Bitmap bitmap = ImageUtil.mediaImageToBitmap(mediaImage);
                        //*/
                        /*
                        @SuppressLint("UnsafeExperimentalUsageError")
                        Image mediaImage = image.getImage();
                        byte[] byteArray = ImageUtil.mediaImageToByteArray(mediaImage);
                        */
                        /*
                        @SuppressLint("UnsafeExperimentalUsageError")
                        Image mediaImage = image.getImage();
                        ByteBuffer byteBuffer = ImageUtil.mediaImageToByteBuffer(mediaImage);
                        */

                        int rotationDegrees = image.getImageInfo().getRotationDegrees();
                        Log.d(TAG, Float.toString(rotationDegrees)); //90 //0, 90, 180, 90 //이미지를 바르게 하기위해 시계 방향으로 회전해야할 각도
                        bitmap = ImageUtil.rotateBitmap(bitmap, rotationDegrees);
                        final Bitmap finalBitmap = ImageUtil.flipHorizontallyBitmap(bitmap);

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                imageView.setImageBitmap(finalBitmap);
                            }
                        });

                        image.close();
                    }
                }
        );

        processCameraProvider.bindToLifecycle(this, cameraSelector, imageAnalysis);
    }

    @Override
    protected void onPause() {
        super.onPause();
        processCameraProvider.unbindAll();
    }
}
