package com.example.myapplication;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.VideoView;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    Button btnStart, btnLogin;
    MediaPlayer music;
    VideoView videoView;
    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        music = MediaPlayer.create(MainActivity.this,R.raw.menu);
        music.setLooping(true);
        music.start();

        btnStart = (Button)findViewById(R.id.btnStart);
        btnLogin = findViewById(R.id.btnLogin);
        videoView = findViewById(R.id.videoView);

        Uri videoUri = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.candy_crush_bg3);
        videoView.setVideoURI(videoUri);
        videoView.setOnPreparedListener(mp -> {
            mp.setLooping(true);
            videoView.start();
        });

        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                music.stop();
                Intent intent1 = new Intent(MainActivity.this, SelectLvlActivity.class);
                startActivity(intent1);
            }
        });
    }
    @Override
    protected void onResume() {
        super.onResume();
        if (!videoView.isPlaying()) {
            videoView.start();
        }
    }
}