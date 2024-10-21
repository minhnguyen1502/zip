package com.example.zipservice.test;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import com.example.zipservice.MainActivity;

public class MyService extends Service {

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // Mở Activity khi Service được khởi chạy
        Intent activityIntent = new Intent(this, MainActivity.class);
        activityIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(activityIntent);

        // Service chỉ cần mở Activity, không cần chạy nền
        stopSelf(); 
        return START_NOT_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null; // Không cần bind cho Service này
    }
}