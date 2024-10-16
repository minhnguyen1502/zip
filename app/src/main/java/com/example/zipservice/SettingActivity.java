package com.example.zipservice;

import android.app.Activity;
import android.app.admin.DevicePolicyManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;

import androidx.appcompat.widget.SwitchCompat;

public class SettingActivity extends Activity {
    private Switch mSwitchd = null;
    private Context mContext = null;
    TextView tv_status;
    int choser;
    RelativeLayout playico, rateus, aboutus, disable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setings);

        mContext = this;

        SharedPreferencesUtil.init(mContext);
        tv_status = findViewById(R.id.text_en);
        disable = findViewById(R.id.disablescreenlock);
        playico = findViewById(R.id.playicon);
        rateus = findViewById(R.id.rateus);
        aboutus = findViewById(R.id.aboutus);


        mSwitchd = this.findViewById(R.id.switch_locksetting);
        boolean lockState = SharedPreferencesUtil.get(Lockscreen.ISLOCK);
        mSwitchd.setChecked(lockState);

        mSwitchd.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    SharedPreferencesUtil.setBoolean(Lockscreen.ISLOCK, true);
                    Lockscreen.getInstance(mContext).startLockscreenService();
                    tv_status.setText("Disable Lock Screen");
                    finish();

                } else {
                    SharedPreferencesUtil.setBoolean(Lockscreen.ISLOCK, false);
                    Lockscreen.getInstance(mContext).stopLockscreenService();
                    tv_status.setText("Enable Lock Screen");

                }

            }
        });

        playico.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse("https://play.google.com/store/apps/developer?id=New+and+Top+Apps"));
                startActivity(i);


            }
        });
        rateus.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse("https://play.google.com/store/apps/details?id=com.topandnewapps.newyearzipperlock"));
                startActivity(i);


            }
        });

        disable.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                Intent intent = new Intent(DevicePolicyManager.ACTION_SET_NEW_PASSWORD);
                startActivity(intent);


            }
        });


    }

}



