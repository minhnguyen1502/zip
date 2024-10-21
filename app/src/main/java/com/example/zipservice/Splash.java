package com.example.zipservice;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

public class Splash extends Activity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);


        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                    finish();
                    // InterstitialAdmob();
                    Intent in = new Intent(getApplicationContext(), SettingActivity.class);
                    startActivity(in);



            }
        }, 2600);
    }


}
