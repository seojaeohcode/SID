package com.example.sidapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class SplashActivity extends AppCompatActivity {
    //#0046FF
    private View mLayout;
    private Animation fadeInAnim;
    private Animation fadeOutAnim;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_splash);

        mLayout = findViewById(R.id.splash); // ID가 splash인 뷰를 찾아야 합니다.

        // 애니메이션 로드
        fadeInAnim = AnimationUtils.loadAnimation(this, R.anim.fade_in);
        //fadeOutAnim = AnimationUtils.loadAnimation(this, R.anim.fade_out);

        // 페이드 인 애니메이션 시작
        mLayout.startAnimation(fadeInAnim);

        // 애니메이션 종료 리스너 설정
        fadeInAnim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                // 애니메이션 시작 시의 동작
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                // 애니메이션 종료 후 fade out 시작
                //mLayout.startAnimation(fadeOutAnim);
                Intent intent = new Intent(SplashActivity.this, GptReportActivity.class);
                startActivity(intent);
                finish(); // 현재 액티비티 종료
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
                // 애니메이션 반복 시의 동작
            }
        });

        /*
        // fade out 애니메이션 종료 리스너 설정
        fadeOutAnim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                // 애니메이션 시작 시의 동작
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                // fade out 후 MainActivity로 이동
                Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                startActivity(intent);
                finish(); // 현재 액티비티 종료
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
                // 애니메이션 반복 시의 동작
            }
        });
        */
    }
}