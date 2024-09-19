package com.example.sidapplication;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;

<<<<<<< HEAD
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;

=======
import androidx.appcompat.app.AppCompatActivity;

>>>>>>> 972c3405a8efe59f8de52140939afd25a9973e23
public class CheckDiagnosisPhotoActivity extends AppCompatActivity {
    private static final String TAG = "UploadImageToFirebaseStorage";
    Bitmap sendBit = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkdiagnosisphoto);

        // Intent에서 ByteArray 받기
<<<<<<< HEAD
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
=======
        byte[] frameBitmapData = getIntent().getByteArrayExtra("captured_image");

        if (frameBitmapData != null) {
            Bitmap faceBitmap = BitmapFactory.decodeByteArray(frameBitmapData, 0, frameBitmapData.length);

            if (faceBitmap != null) {
                ImageView imageView = findViewById(R.id.diagnosisphotopreview);
                imageView.setImageBitmap(faceBitmap); // Bitmap을 ImageView에 설정
            } else {
                Log.e("CheckDiagnosisPhotoActivity", "Bitmap 변환 실패");
            }
        } else {
            Log.e("CheckDiagnosisPhotoActivity", "받은 ByteArray가 null입니다.");
        }

        ImageButton sending = findViewById(R.id.sendimg);

        sending.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 새로운 Intent 생성
                Intent newIntent = new Intent(CheckDiagnosisPhotoActivity.this, GptReportActivity.class);
                // ByteArray를 Intent에 추가
                newIntent.putExtra("captured_image", getIntent().getByteArrayExtra("captured_image"));
                // 새로운 Activity 시작
                startActivity(newIntent);
            }
        });
>>>>>>> 972c3405a8efe59f8de52140939afd25a9973e23
    }
}
