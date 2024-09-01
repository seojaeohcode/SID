package com.example.sidapplication;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

public class CheckDiagnosisPhotoActivity extends AppCompatActivity {
    private static final String TAG = "UploadImageToFirebaseStorage";
    Bitmap sendBit = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkdiagnosisphoto);

        // Intent에서 ByteArray 받기
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
    }
}
