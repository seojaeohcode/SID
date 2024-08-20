package com.example.sidapplication;

import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import android.graphics.Bitmap;
import android.widget.ImageView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Intent에서 ByteArray 받기
        byte[] faceBitmapData = getIntent().getByteArrayExtra("face_image");

        if (faceBitmapData != null) {
            // ByteArray를 Bitmap으로 변환
            Bitmap faceBitmap = BitmapFactory.decodeByteArray(faceBitmapData, 0, faceBitmapData.length);

            ImageView imageView = findViewById(R.id.imageView2);
            imageView.setImageBitmap(faceBitmap); // Bitmap을 ImageView에 설정
        }
    }
}