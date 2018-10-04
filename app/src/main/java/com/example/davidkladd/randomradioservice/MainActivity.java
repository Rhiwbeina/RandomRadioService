package com.example.davidkladd.randomradioservice;

import android.Manifest;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {
    Button buttquit, buttgong, buttpause;
    TextView textView, textViewSongCount;
    final String TAG = "Dave";
    Handler maHandler;
    Intent myIntent;

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
            myIntent = new Intent(getApplicationContext(), MyService.class);
            startService(myIntent);
            MyService.setContext(getApplicationContext());
        }

        textViewSongCount = findViewById(R.id.textViewSongCount);
        textViewSongCount.setText("Songs found in library " + getLibraryCount());

        buttquit = findViewById(R.id.buttquit);
        buttquit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopService(new Intent(getApplicationContext(), MyService.class));
                Log.d(TAG, "onClick: stopping service");
                finishAffinity();
                System.exit(0);
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
// update text display regularly with announcement text
        maHandler = new Handler();
        Runnable rr = new Runnable(){
            public void run(){
                textView.setText(MyService.Anno);
                maHandler.postDelayed(this, 1000);
            }
        };
        maHandler.postDelayed(rr, 500);
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

    public String getLibraryCount() {
        Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;

        String[] columns = {MediaStore.Audio.Media.TITLE,
                MediaStore.Audio.Media.ARTIST,
                MediaStore.Audio.Media.ALBUM,
                MediaStore.Audio.Media.YEAR,
                MediaStore.Audio.Media.DURATION,
                MediaStore.Audio.Media.DATA
        };

        String libraryCount = "";
        String[] searchy = {"%" + "" + "%", "30000"};
        try {
            Cursor cursor = getContentResolver().query(uri, columns,  MediaStore.Audio.Media.TITLE + " LIKE ? AND duration > ?" , searchy, null);

            assert cursor != null;
            libraryCount = String.valueOf(cursor.getCount());
            Log.d(TAG, "onCreate: Library count = " + String.valueOf(cursor.getCount()));
            cursor.close();
            //textViewLibraryCount.setText("Soungs found in library: " + String.valueOf(cursor.getCount()) );
        }
        catch( Exception eee){
            Log.d(TAG, "onCreate: error " + eee);
            libraryCount = "error";
        }
        return libraryCount;
    }

}
