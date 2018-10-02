package com.example.davidkladd.randomradioservice;

import android.Manifest;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {
    Button buttstart, buttstop, buttgong, buttpause;
    TextView textView;
    final String TAG = "Dave";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
// Check whether this app has write external storage permission or not.
        int readExternalStoragePermission = ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE);
// If do not grant write external storage permission.
        if(readExternalStoragePermission!= PackageManager.PERMISSION_GRANTED)
        {
// Request user to grant write external storage permission.
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 5);
        }

        textView = findViewById(R.id.textView);
        if (isMyServiceRunning(MyService.class)){
            textView.setText("Service Already Running");
        } else {
            textView.setText("Starting Service");
            Intent myIntent = new Intent(getApplicationContext(), MyService.class);
            startService(myIntent);
            MyService.setContext(getApplicationContext());
        }

        buttstart = findViewById(R.id.buttstart);
        buttstart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: choosin song with media finder " );
                MyService.ChooseNewSong();

            }
        });
        
        buttstop = findViewById(R.id.buttstop);
        buttstop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: stop");
                int ttt = MyService.getPos();
                textView.setText(String.valueOf(ttt));

            }
        });

        buttgong = findViewById(R.id.buttgong);
        buttgong.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: gongged");
                MyService.ChooseNewSong();
            }
        });
        buttpause = findViewById(R.id.buttpause);
        buttpause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: pausing" );
                MyService.pauseMusic();
            }
        });

    }

    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }
}
