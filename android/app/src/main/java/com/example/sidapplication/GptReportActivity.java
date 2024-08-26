package com.example.sidapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class GptReportActivity extends AppCompatActivity {
    private String API_KEY;  // OpenAI API 키
    private static final String TAG = "GPTAssistant";
    OkHttpClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gptreport);


        // API_KEY 초기화
        API_KEY = getResources().getString(R.string.GPTKey);

        //연결시간 설정. 60초/120초/60초
        client = new OkHttpClient().newBuilder()
                .connectTimeout(60, TimeUnit.SECONDS)
                .writeTimeout(120, TimeUnit.SECONDS)
                .readTimeout(60, TimeUnit.SECONDS)
                .build();

        // GPT Assistant 호출
        callCareGPT();
    }


    private void callCareGPT() {
        String url = "https://api.openai.com/v1/chat/completions"; // OpenAI API URL
        String jsonBody = createJsonRequest(); // 질문을 JSON 형태로 만듭니다.

        RequestBody body = RequestBody.create(MediaType.parse("application/json"), jsonBody);
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .addHeader("Authorization", "Bearer " + API_KEY)
                .addHeader("Content-Type", "application/json")
                .addHeader("OpenAI-Beta", "assistants=v2")
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                Log.e(TAG, "API 호출 실패: " + e.getMessage());
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (response.isSuccessful()) {
                    String responseData = response.body().string();
                    parseResponse(responseData);
                } else {
                    Log.e(TAG, "응답 실패: " + response.code());
                }
            }
        });
    }

    private String createJsonRequest() {
        JSONObject json = new JSONObject();
        try {
            json.put("model", "Care_GPT"); // 사용할 모델
            JSONArray messages = new JSONArray();
            JSONObject message = new JSONObject();
            message.put("role", "user");
            message.put("content", "건선 3");
            messages.put(message);
            json.put("messages", messages);
        } catch (JSONException e) {
            Log.e(TAG, "JSON 생성 실패: " + e.getMessage());
        }
        return json.toString();
    }

    private void parseResponse(String responseData) {
        try {
            JSONObject jsonResponse = new JSONObject(responseData);
            JSONArray choices = jsonResponse.getJSONArray("choices");
            String reply = choices.getJSONObject(0).getJSONObject("message").getString("content");

            runOnUiThread(() -> {
                // UI 업데이트: TextView에 GPT의 응답을 표시
                TextView textView = findViewById(R.id.report);
                textView.setText(reply);
            });
        } catch (JSONException e) {
            Log.e(TAG, "응답 파싱 실패: " + e.getMessage());
        }
    }
}
