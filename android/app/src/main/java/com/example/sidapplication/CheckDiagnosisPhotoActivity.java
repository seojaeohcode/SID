package com.example.sidapplication;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;

public class CheckDiagnosisPhotoActivity extends AppCompatActivity {
    private static final String TAG = "UploadImageToFirebaseStorage";
    Bitmap sendBit = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkdiagnosisphoto);

        // Intent에서 ByteArray 받기
        byte[] faceBitmapData = getIntent().getByteArrayExtra("captured_image");

        if (faceBitmapData != null) {
            // ByteArray를 Bitmap으로 변환
            Bitmap faceBitmap = BitmapFactory.decodeByteArray(faceBitmapData, 0, faceBitmapData.length);

            ImageView imageView = findViewById(R.id.diagnosisphotopreview);
            imageView.setImageBitmap(faceBitmap); // Bitmap을 ImageView에 설정

            sendBit = faceBitmap;
        }

//        ImageButton save = findViewById(R.id.save);
//
//        save.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//            }
//        });
    }
}
