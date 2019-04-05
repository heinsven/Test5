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
        //为activity注册的默认音频通道
        context.setVolumeControlStream(AudioManager.STREAM_MUSIC);

        final MediaPlayer mediaPlayer = new MediaPlayer();
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);//指定播放的声音通道为 STREAM_MUSIC
        //当播放完毕一次后，重新指向流文件的开头，以准备下次播放
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
        //设定要播放的音乐文件的 数据源，并准备播放
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
