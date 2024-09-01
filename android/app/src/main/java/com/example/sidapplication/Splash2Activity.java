package com.example.sidapplication;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Handler;
import android.view.WindowManager;


public class Splash2Activity extends AppCompatActivity  {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_splash2);

        // 3초 후에 LoginStartActivity로 이동
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(Splash2Activity.this, DiagnosisCameraActivity.class);
                startActivity(intent);
                finish(); // 현재 액티비티 종료
            }
        }, 3000); // 3000ms = 3초
    }
}
