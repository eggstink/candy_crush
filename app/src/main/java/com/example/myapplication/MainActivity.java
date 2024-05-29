package com.example.myapplication;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;
import android.widget.VideoView;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    Button btnLogin, btnRegister, btnStart, btnLogOut;
    MediaPlayer music;
    MediaPlayer forward;
    VideoView videoView;
    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        music = MediaPlayer.create(MainActivity.this,R.raw.menu);
        forward = MediaPlayer.create(MainActivity.this,R.raw.clickbuttonforwardsfx);
        music.setLooping(true);
        music.start();

        btnLogin = (Button) findViewById(R.id.btnLogin);
        btnRegister = (Button) findViewById(R.id.btnRegister);
        btnStart = (Button)findViewById(R.id.btnStart);
        btnLogOut = (Button) findViewById(R.id.btnLogout);
        videoView = findViewById(R.id.videoView);

        Uri videoUri = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.candy_crush_bg3);
        videoView.setVideoURI(videoUri);
        videoView.setOnPreparedListener(mp -> {
            mp.setLooping(true);
            videoView.start();
        });

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        String loggedInUser = preferences.getString("loggedID", "");

        if(!loggedInUser.isEmpty()) {
            btnStart.setVisibility(View.VISIBLE);
            btnLogOut.setVisibility(View.VISIBLE);
            //hide
            btnLogin.setVisibility(View.GONE);
            btnRegister.setVisibility(View.GONE);
        } else {
            btnLogin.setVisibility(View.VISIBLE);
            btnRegister.setVisibility(View.VISIBLE);
            //hide
            btnStart.setVisibility(View.GONE);
            btnLogOut.setVisibility(View.GONE);
        }

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentLogIn = new Intent(MainActivity.this, LogInActivity.class);
                startActivity(intentLogIn);
                finish();
            }
        });

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent1 = new Intent(MainActivity.this, RegisterActivity.class);
                startActivity(intent1);
            }
        });

        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                music.stop();
                Intent intent1 = new Intent(MainActivity.this, SelectLvlActivity.class);
                forward.start();
                startActivity(intent1);
            }
        });

        btnLogOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                music.stop();

                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);
                SharedPreferences.Editor editor = preferences.edit();
                editor.remove("loggedID");
                editor.apply();

                Intent intentSplash = new Intent(MainActivity.this, SplashScreen.class);
                startActivity(intentSplash);
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