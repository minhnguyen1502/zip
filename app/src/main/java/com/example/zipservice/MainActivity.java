package com.example.zipservice;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ToggleButton;

import androidx.appcompat.app.AppCompatActivity;

import com.example.zipservice.service.LockScreen;

public class MainActivity extends AppCompatActivity {

    ToggleButton toggleButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toggleButton = (ToggleButton)findViewById(R.id.toggleButton);
        LockScreen.getInstance().init(this,true);
        if(LockScreen.getInstance().isActive()){
            toggleButton.setChecked(true);
        }else{
            toggleButton.setChecked(false);

        }

        Button preview = (Button) findViewById(R.id.preview);
        preview.setOnClickListener(v -> startActivity(new Intent(this, PreviewActivity.class)));
        toggleButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean checked) {
                if(checked){

                    LockScreen.getInstance().active();
                }else{
                    LockScreen.getInstance().deactivate();
                }
            }
        });

    }


}