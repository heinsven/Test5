package com.example.uestc.test5;

import android.app.Activity;
import android.content.pm.PackageManager;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioRecord;
import android.media.AudioTrack;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class Test5 extends AppCompatActivity {
    private ProgressBar mProgressBar;
    private Button mBtn1;
    private Button mBtn2;
    private Button mBtn3;

    private AudioRecord mAudioRecord;
    private boolean isRecording = false;

    private byte[] audioData;
    int laiyuan = MediaRecorder.AudioSource.MIC;//来源
    int samHz = 16000;//采样频率
    int shengdao = AudioFormat.CHANNEL_IN_MONO;//声道
    int audioFormat = AudioFormat.ENCODING_PCM_16BIT;//格式
    int bufferSize = 2 * AudioRecord.getMinBufferSize(samHz, shengdao, audioFormat);//缓冲区大小

    private String voicePath = Environment.getExternalStorageDirectory().getPath() + "/audio";
    private String voiceName = "/test.pcm";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test5);
        init();
    }

    private void init() {
        verifyStoragePermissions(this);
        mProgressBar = findViewById(R.id.progressbar);
        mProgressBar.setVisibility(View.GONE);
        mBtn1 = findViewById(R.id.btn1);
        mBtn2 = findViewById(R.id.btn2);
        mBtn3 = findViewById(R.id.btn3);

        mBtn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mAudioRecord == null) {
                    startRecord();
                }
            }
        });

        mBtn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mAudioRecord != null) {
                    stopRecord();
                }
            }
        });

        mBtn3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    InputStream in = new FileInputStream(voicePath + voiceName);
                    try {
                        ByteArrayOutputStream out = new ByteArrayOutputStream(264848);
                        for (int b; (b = in.read()) != -1; ) {
                            out.write(b);
                        }
                        audioData = out.toByteArray();
                    } finally {
                        in.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }

                AudioTrack audioTrack = new AudioTrack(AudioManager.STREAM_MUSIC, samHz,
                        AudioFormat.CHANNEL_OUT_MONO, audioFormat,
                        audioData.length, AudioTrack.MODE_STATIC);
                audioTrack.write(audioData, 0, audioData.length);
                audioTrack.play();
            }
        });
    }

    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {"android.permission.READ_EXTERNAL_STORAGE", "android.permission.WRITE_EXTERNAL_STORAGE", "android.permission.RECORD_AUDIO"};

    public static void verifyStoragePermissions(Activity activity) {
        try {
            //检测是否有写的权限
            int permission1 = ActivityCompat.checkSelfPermission(activity,
                    "android.permission.WRITE_EXTERNAL_STORAGE");
            int permission2 = ActivityCompat.checkSelfPermission(activity,
                    "android.permission.RECORD_AUDIO");
            if (permission1 != PackageManager.PERMISSION_GRANTED || permission2 != PackageManager.PERMISSION_GRANTED) {
                // 没有写的权限，去申请写的权限，会弹出对话框
                ActivityCompat.requestPermissions(activity, PERMISSIONS_STORAGE, REQUEST_EXTERNAL_STORAGE);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void startRecord() {
        mAudioRecord = new AudioRecord(laiyuan, samHz, shengdao, audioFormat, bufferSize);

        final byte data[] = new byte[bufferSize];
        final File file = new File(voicePath);
        final File fileaudio = new File(voicePath + voiceName);

        if (fileaudio.exists()) {
            fileaudio.delete();
        }
        if (!file.exists()) {
            file.mkdirs();
        }
        mAudioRecord.startRecording();
        isRecording = true;
        mProgressBar.setVisibility(View.VISIBLE);

        new Thread(new Runnable() {
            @Override
            public void run() {
                FileOutputStream fos = null;
                try {
                    fos = new FileOutputStream(fileaudio);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
                if (null != fos) {
                    while (isRecording) {
                        int read = mAudioRecord.read(data, 0, bufferSize);
                        //返回正确时才读取数据
                        if (AudioRecord.ERROR_INVALID_OPERATION != read) {
                            try {
                                fos.write(data);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                    try {
                        fos.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }


    public void stopRecord() {
        isRecording = false;
        if (mAudioRecord != null) {
            mProgressBar.setVisibility(View.GONE);
            mAudioRecord.stop();
            mAudioRecord.release();
            //调用release之后必须置为null
            mAudioRecord = null;
        }
    }
}

