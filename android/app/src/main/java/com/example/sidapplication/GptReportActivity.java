package com.example.sidapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class GptReportActivity extends AppCompatActivity {
    private static final String TAG = "GPTAssistant";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gptreport);

        // API 호출
        //fetchHelloWorld();


        byte[] frameBitmapData = getIntent().getByteArrayExtra("captured_image");

        if (frameBitmapData != null) {
            // ByteArray를 Bitmap으로 변환
            Bitmap frameBitmap = BitmapFactory.decodeByteArray(frameBitmapData, 0, frameBitmapData.length);

            if (frameBitmap != null) {
                // Bitmap을 PNG 파일로 저장
                File file = saveBitmapToFile(frameBitmap);
                if (file != null) {
                    // 파일을 API로 전송
                    uploadFile(file);
                } else {
                    Log.e("CheckDiagnosisPhotoActivity", "파일 저장 실패");
                }
            } else {
                Log.e("CheckDiagnosisPhotoActivity", "Bitmap 변환 실패");
            }
        } else {
            Log.e("CheckDiagnosisPhotoActivity", "받은 ByteArray가 null입니다.");
        }
    }

    private void fetchHelloWorld() {
        // 타임아웃 설정
        int timeout = 300; // 30초로 설정
        OkHttpClient client = new OkHttpClient.Builder()
                .callTimeout(timeout, TimeUnit.SECONDS)
                .connectTimeout(timeout, TimeUnit.SECONDS)
                .readTimeout(timeout, TimeUnit.SECONDS)
                .writeTimeout(timeout, TimeUnit.SECONDS)
                .build();

        Request request = new Request.Builder()
                .url("http://192.168.0.8:8080/")
                .build();

        client.newCall(request).enqueue(new okhttp3.Callback() {
            @Override
            public void onFailure(okhttp3.Call call, IOException e) {
                Log.e("GptReportActivity", "API 호출 실패", e);
            }

            @Override
            public void onResponse(okhttp3.Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String responseData = response.body().string();
                    Log.d("GptReportActivity", "응답 데이터: " + responseData);

                    // UI 업데이트는 UI 스레드에서 수행
                    runOnUiThread(() -> {
                        TextView reportTextView = findViewById(R.id.report);
                        reportTextView.setText(responseData); // 결과를 TextView에 설정
                    });
                } else {
                    Log.e("GptReportActivity", "API 호출 실패: " + response.message());
                }
            }
        });
    }

    // Bitmap을 파일로 저장하는 메서드
    private File saveBitmapToFile(Bitmap bitmap) {
        File file = new File(getCacheDir(), "captured_image.png");
        try (FileOutputStream fos = new FileOutputStream(file)) {
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos); // PNG 형식으로 저장
            return file;
        } catch (IOException e) {
            Log.e("CheckDiagnosisPhotoActivity", "파일 저장 실패", e);
            return null;
        }
    }
    private void uploadFile(File file) {
        // 타임아웃 설정 (예: 10초 -> 30초로 변경)
        int timeout = 300; // 30초로 설정 (3배 증가)
        OkHttpClient client = new OkHttpClient.Builder()
                .callTimeout(timeout, TimeUnit.SECONDS) // 호출 타임아웃 설정
                .connectTimeout(timeout, TimeUnit.SECONDS) // 연결 타임아웃 설정
                .readTimeout(timeout, TimeUnit.SECONDS) // 읽기 타임아웃 설정
                .writeTimeout(timeout, TimeUnit.SECONDS) // 쓰기 타임아웃 설정
                .build();

        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("file", file.getName(),
                        RequestBody.create(file, MediaType.parse("image/png")))
                .build();
        Request request = new Request.Builder()
                .url("http://172.30.1.58:8080/diagnosis")
                .post(requestBody)
                .build();

        client.newCall(request).enqueue(new okhttp3.Callback() {
            @Override
            public void onFailure(okhttp3.Call call, IOException e) {
                Log.e("CheckDiagnosisPhotoActivity", "파일 전송 실패", e);
            }

            @Override
            public void onResponse(okhttp3.Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String responseData = response.body().string();
                    TextView reportTextView = findViewById(R.id.report);
                    Log.d("CheckDiagnosisPhotoActivity", "파일 전송 성공: " + responseData);
                    runOnUiThread(() -> reportTextView.setText(responseData)); // 결과를 TextView에 설정
                } else {
                    Log.e("CheckDiagnosisPhotoActivity", "파일 전송 실패: " + response.message());
                }
            }
        });
    }
}
