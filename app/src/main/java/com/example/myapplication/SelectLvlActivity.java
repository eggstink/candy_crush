package com.example.myapplication;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Button;
import android.widget.ImageButton;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class SelectLvlActivity extends AppCompatActivity {
    ImageButton btnLvl1, btnLvl2, btnLvl3;
    MediaPlayer music,forward;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_select_lvl);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        btnLvl1 = findViewById(R.id.btnLvl1);
        btnLvl2 = findViewById(R.id.btnLvl2);
        btnLvl3 = findViewById(R.id.btnLvl3);

        music = MediaPlayer.create(SelectLvlActivity.this,R.raw.level_music);
        music.setLooping(true);
        music.start();

        forward = MediaPlayer.create(SelectLvlActivity.this,R.raw.clickbuttonforwardsfx);

        btnLvl1.setOnClickListener(view->{
            btnLvl1.setImageResource(R.drawable.btnlvl1_pressed);
            new Handler().postDelayed(() -> {
                music.stop();
                forward.start();
                btnLvl1.setImageResource(R.drawable.btnlvl1_unpressed);
                new Handler().postDelayed(() -> {
                    startActivity(new Intent(SelectLvlActivity.this, Level1.class));
                }, 100);
            }, 200);
        });
        btnLvl2.setOnClickListener(view->{
            btnLvl2.setImageResource(R.drawable.btnlvl2_pressedv2);
            new Handler().postDelayed(() -> {
                music.stop();
                forward.start();
                btnLvl2.setImageResource(R.drawable.btnlvl2_unpressed);
                new Handler().postDelayed(() -> {
                    startActivity(new Intent(SelectLvlActivity.this, Level2.class));
                }, 100);
            }, 200);
        });
        btnLvl3.setOnClickListener(view->{
            btnLvl3.setImageResource(R.drawable.btnlvl3_pressedv2);
            new Handler().postDelayed(() -> {
                music.stop();
                forward.start();
                btnLvl3.setImageResource(R.drawable.btnlvl3_unpressed);
                new Handler().postDelayed(() -> {
                    startActivity(new Intent(SelectLvlActivity.this, Level3.class));
                }, 100);
            }, 200);
        });
    }
}