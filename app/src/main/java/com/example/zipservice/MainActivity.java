package com.example.zipservice;

import android.os.Bundle;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ZipperScreenLockView zipperScreenLockView = findViewById(R.id.zip);
        zipperScreenLockView.setCompleteListener(new ZipperScreenLockView.IZipperListener() {
            @Override
            public void zipperSuccess() {

            }

            @Override
            public void zipperMoving() {
            }

            @Override
            public void zipperCancel() {

            }
        });
    }
}