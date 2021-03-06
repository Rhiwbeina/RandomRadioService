package com.example.davidkladd.randomradioservice;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.speech.tts.TextToSpeech;
import android.support.annotation.Nullable;
import android.util.Log;

public class MyService extends Service {
    final  static String  TAG = "Dave";
    static  MediaPlayer mp;
    public static Handler mHandler;
    public static Context mContext;
    static DavesTTS TTS;
    static DavesSpeechComposer dsc;
    public static boolean songDueToEnd;
    public static String Anno = "";
    final int fadeOutTime = 20000;

public static void setContext(Context ctx){
    mContext = ctx;
}

    public MyService() {
        Log.d(TAG, "MyService: ");
        mp = new MediaPlayer();
        mHandler = new Handler();
        TTS = new DavesTTS(mContext, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                Log.d(TAG, "onInit: Text to Speech ready i guess");
                ChooseNewSong();
            }
        }, mHandler);

        dsc = new DavesSpeechComposer(mContext);
        songDueToEnd = false;
        Runnable rr = new Runnable(){
            public void run(){
                if (MyService.mp.isPlaying() && !songDueToEnd){
                    if (mp.getDuration() - mp.getCurrentPosition() < fadeOutTime){
                        songDueToEnd = true;
                        ChooseNewSong();
                        Log.d(TAG, "Song due to end");
                    }
                }
                mHandler.postDelayed(this, 1000);
            }
        };
        mHandler.postDelayed(rr, 500);
    }

    public static void ChooseNewSong(){
        Log.d(TAG, "playMusic: choosing a file");
        Runnable runnable = new RunnableMediaFinder();
        new Thread(runnable).start();
    }

    public static void songChoosen( Bundle songBundle){
    // called when the RunnableMediaFinder finishes
        Log.d(TAG, "songChoosen: done");
        String speech = dsc.getSentence(songBundle);
        Anno = speech;

        TTS.sayIt(speech, songBundle);
    }

 public static void playMusic(Bundle songBundle) {
     Log.d("Dave", "playMusic: ");
     killPlayer();
     try {
         mp.setDataSource(songBundle.getString("data"));
         //mp.setDataSource("/storage/sdcard/Music/01-What's the Matter Here_.mp3");
         mp.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
             @Override
             public void onPrepared(MediaPlayer mp) {
                 Log.d("Dave", "onPrepared: " + mp.getCurrentPosition());
                 mp.setVolume(1.0f, 1.0f);
                 mp.seekTo(70);
                 mp.start();
                 songDueToEnd = false;
             }
         });
         mp.prepareAsync();
     } catch (Exception eee) {
         Log.d("Dave", "music player error  " + eee);
     }
 }

 public static void killService(){
        killPlayer();

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
