package com.example.uestc.test5;

import android.app.Activity;
import android.content.res.AssetFileDescriptor;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.media.SoundPool;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import java.io.IOException;

public class Test34 extends AppCompatActivity implements View.OnClickListener {
    private Ringtone ringtone;
    private SoundPool soundPool;
    private MediaPlayer mediaPlayer;

    private int loadId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test34);
        //初始化这三个方法
        ringtone = initRingtone(this);
        soundPool = initSoundPool();
        mediaPlayer = initMediaPlayer(this, 0);

        loadId = soundPool.load(this, R.raw.beep, 1);

        findViewById(R.id.bt_ringtone).setOnClickListener(this);
        findViewById(R.id.bt_sound_pool).setOnClickListener(this);
        findViewById(R.id.bt_media).setOnClickListener(this);
    }

    public MediaPlayer initMediaPlayer(Activity context, int rawId) {
        context.setVolumeControlStream(AudioManager.STREAM_MUSIC);

        final MediaPlayer mediaPlayer = new MediaPlayer();
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                mediaPlayer.seekTo(0);
            }
        });
        mediaPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(MediaPlayer mp, int what, int extra) {
                mediaPlayer.release();
                return true;
            }
        });

        AssetFileDescriptor file;
        if (rawId == 0) {
            file = context.getResources().openRawResourceFd(R.raw.beep);
        } else {
            file = context.getResources().openRawResourceFd(rawId);
        }
        try {
            mediaPlayer.setDataSource(file.getFileDescriptor(),
                    file.getStartOffset(), file.getLength());
            file.close();
            float BEEP_VOLUME = 1.0f;
            mediaPlayer.setVolume(BEEP_VOLUME, BEEP_VOLUME);
            mediaPlayer.prepare();
        } catch (IOException e) {
            e.printStackTrace();
            Log.i("initSpecifiedSound", "what happened to init sound? you need to deal it .");
            return null;
        }
        return mediaPlayer;
    }

    private SoundPool initSoundPool() {
        return new SoundPool(10, AudioManager.STREAM_NOTIFICATION, 5);
    }

    private Ringtone initRingtone(Activity context) {
        Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);
        return RingtoneManager.getRingtone(context, notification);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bt_ringtone:
                ringtone.play();
                break;
            case R.id.bt_sound_pool:
                soundPool.play(loadId, 1.0f, 1.0f, 0, 0, 1.0f);
                break;
            case R.id.bt_media:
                mediaPlayer.start();
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        soundPool.release();
        mediaPlayer.release();
    }
}
