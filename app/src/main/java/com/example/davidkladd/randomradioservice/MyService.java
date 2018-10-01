package com.example.davidkladd.randomradioservice;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentSender;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Looper;
import android.os.Parcel;
import android.os.RemoteException;
import android.os.UserHandle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.Display;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class MyService extends Service {
    final  static String  TAG = "Dave";
    static  MediaPlayer mp;
    public static Handler mHandler;
    public static Context mContext;

public static void setContext(Context ctx){
    mContext = ctx;
}

    public MyService() {
        Log.d(TAG, "MyService: ");
        mp = new MediaPlayer();
        mHandler = new Handler();
        //mContext = this.getApplicationContext();
    }

    public static void ChooseNewSong(){
        Log.d(TAG, "playMusic: choosing a file");
        Runnable runnable = new RunnableMediaFinder();
        new Thread(runnable).start();
    }

    public static void songChoosen( Bundle songBundle){
    // called when the RunnableMediaFinder finishes
        Log.d(TAG, "songChoosen: done");
        //songBundle.getString("data");
        playMusic(songBundle.getString("data"));
    }

 public static void playMusic(String pathToSong) {
     Log.d("Dave", "playMusic: ");
     killPlayer();
     try {
         mp.setDataSource(pathToSong);
         //mp.setDataSource("/storage/sdcard/Music/01-What's the Matter Here_.mp3");
         mp.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
             @Override
             public void onPrepared(MediaPlayer mp) {
                 Log.d("Dave", "onPrepared: " + mp.getCurrentPosition());
                 //mp.isPlaying();
                 mp.start();
             }
         });
         mp.prepareAsync();
     } catch (Exception eee) {
         Log.d("Dave", "music player error  " + eee);
     }
 }

 public static int getPos(){
        if (mp != null){
            return mp.getCurrentPosition();
        }
        return 333;
 }

 public static void pauseMusic(){
        if (mp.isPlaying()){
            mp.pause();
        } else {
            mp.start();
        }
 }

 private static void killPlayer(){
        if (mp != null){
            Log.d(TAG, "killPlayer: ");
            if (mp.isPlaying()) {
                mp.stop();
            }
            mp.reset();
        }
 }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
