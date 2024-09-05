package com.example.sidapplication;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import android.util.Log;
import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;

public class CheckPhotoActivity extends AppCompatActivity {
    private static final String TAG = "UploadImageToFirebaseStorage";
    Bitmap sendBit = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photocheck);

        // Intent에서 ByteArray 받기
        byte[] faceBitmapData = getIntent().getByteArrayExtra("face_image");

        if (faceBitmapData != null) {
            // ByteArray를 Bitmap으로 변환
            Bitmap faceBitmap = BitmapFactory.decodeByteArray(faceBitmapData, 0, faceBitmapData.length);

            ImageView imageView = findViewById(R.id.cvcapture);
            imageView.setImageBitmap(faceBitmap); // Bitmap을 ImageView에 설정

            sendBit = faceBitmap;
        }

        ImageButton save = findViewById(R.id.save);

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ImageUpload(sendBit);
            }
        });
    }

    private void ImageUpload(Bitmap faceBitmap) {
        // Firebase Storage 인스턴스 가져오기
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();

        // Bitmap을 byte[]로 변환
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        faceBitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos); // JPEG 형식으로 압축
        byte[] data = baos.toByteArray();

        HashMap<String, String> userInfo = (HashMap<String, String>) getIntent().getSerializableExtra("userInfo");

        // HashMap에서 uid 가져오기
        String uid = userInfo.get("uid");
        String formattedDate = userInfo.get("formattedDate");

        // 원하는 파일명 설정
        String fileName = uid + "_" + formattedDate + ".jpg"; // uid_날짜.jpg 형식으로 파일명 설정
        StorageReference imageRef = storageRef.child("userface/" + fileName); // "images/"는 저장할 경로

        // 이미지 업로드
        UploadTask uploadTask = imageRef.putBytes(data);
        uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                // 업로드 성공 시 처리
                Log.d("Upload", "Image uploaded successfully");
                // MainActivity로 이동
                deleteImage(imageRef); // 업로드 후 이미지 삭제
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // 업로드 실패 시 처리
                Log.e("Upload", "Image upload failed: " + exception.getMessage());
            }
        });
    }

    private void deleteImage(StorageReference imageRef) {
        // Firebase Storage 인스턴스 가져오기
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();

        // 이미지 삭제
        imageRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                // 삭제 성공 시 처리
                Log.d(TAG, "Image deleted successfully");
                goToMainActivity(); // 삭제 후 MainActivity로 이동
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // 삭제 실패 시 처리
                Log.e(TAG, "Image deletion failed: " + exception.getMessage());
            }
        });
    }


    private void goToMainActivity() {
        Intent intent = new Intent(CheckPhotoActivity.this, MainActivity.class);
        byte[] faceBitmapData = getIntent().getByteArrayExtra("face_image");
        HashMap<String, String> userInfo = (HashMap<String, String>) getIntent().getSerializableExtra("userInfo");

        intent.putExtra("face_image", faceBitmapData);
        intent.putExtra("userInfo", userInfo);

        startActivity(intent);
        finish();
    }
}