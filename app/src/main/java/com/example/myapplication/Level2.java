package com.example.myapplication;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Level2 extends AppCompatActivity {
    TextView tvMoves;
    int[] tiles = {
            R.drawable.diamond,
            R.drawable.gold,
            R.drawable.iron,
            R.drawable.lapis,
            R.drawable.netherite,
            R.drawable.redstone,
    };

    int maxNumOfMoves = 50;
    int widthOfBlock, noOfBlocks = 8, widthOfScreen, heightofScreen;
    ArrayList<ImageView> tile = new ArrayList<>();
    int tileToBeDragged, tileToBeReplaced;
    int notTile = R.drawable.ic_launcher_background;
    Handler mHandler;
    int interval = 300;
    TextView scoreRes;
    int score = 0;
    boolean swiped = false;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_level2);
        tvMoves = findViewById(R.id.moves2);
        scoreRes = findViewById(R.id.score2);

        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        widthOfScreen = dm.widthPixels;
        heightofScreen = dm.heightPixels;
        widthOfBlock = widthOfScreen / noOfBlocks;

        createboard();
        mHandler = new Handler();
        startRepeat();

        for (ImageView imageView : tile) {
            imageView.setOnTouchListener(new OnSwipeListener(this) {
                @Override
                void onSwipeLeft() {
                    super.onSwipeLeft();
                    tileToBeDragged = imageView.getId();
                    tileToBeReplaced = tileToBeDragged - 1;
                    swiped = true;
                    candyInterchange();
                }

                @Override
                void onSwipeRight() {
                    super.onSwipeRight();
                    tileToBeDragged = imageView.getId();
                    tileToBeReplaced = tileToBeDragged + 1;
                    swiped = true;
                    candyInterchange();
                }

                @Override
                void onSwipeTop() {
                    super.onSwipeTop();
                    tileToBeDragged = imageView.getId();
                    tileToBeReplaced = tileToBeDragged - noOfBlocks;
                    swiped = true;
                    candyInterchange();
                }

                @SuppressLint("ClickableViewAccessibility")
                @Override
                void onSwipeBottom() {
                    super.onSwipeBottom();
                    tileToBeDragged = imageView.getId();
                    tileToBeReplaced = tileToBeDragged + noOfBlocks;
                    swiped = true;
                    candyInterchange();
                }
            });
        }
    }

    private void createboard() {
        GridLayout gridLayout = findViewById(R.id.board2);
        gridLayout.setRowCount(noOfBlocks);
        gridLayout.setColumnCount(noOfBlocks);
        gridLayout.getLayoutParams().width = widthOfScreen;
        gridLayout.getLayoutParams().height = widthOfScreen;

        boolean[][] crossPattern = {
                {false, false, true, true, true, true, false, false},
                {false, false, true, true, true, true, false, false},
                {true, true, true, true, true, true, true, true},
                {true, true, true, true, true, true, true, true},
                {true, true, true, true, true, true, true, true},
                {true, true, true, true, true, true, true, true},
                {false, false, true, true, true, true, false, false},
                {false, false, true, true, true, true, false, false}
        };

        for (int i = 0; i < noOfBlocks * noOfBlocks; i++) {
            ImageView imageView = new ImageView(this);
            imageView.setId(i);
            imageView.setLayoutParams(new ViewGroup.LayoutParams(widthOfBlock, widthOfBlock));
            imageView.setMaxHeight(widthOfBlock);
            imageView.setMaxWidth(widthOfBlock);

            int row = i / noOfBlocks;
            int col = i % noOfBlocks;

            if (crossPattern[row][col]) {
                int rand = (int) Math.floor(Math.random() * tiles.length);
                imageView.setImageResource(tiles[rand]);
                imageView.setTag(tiles[rand]);
            } else {
                imageView.setImageResource(notTile);
                imageView.setTag(notTile);
            }

            tile.add(imageView);
            gridLayout.addView(imageView);
        }

        Log.d("Level1", "Board created with cross pattern");
    }

    private void candyInterchange() {
        int background = (int) tile.get(tileToBeReplaced).getTag();
        int background1 = (int) tile.get(tileToBeDragged).getTag();
        tile.get(tileToBeDragged).setImageResource(background);
        tile.get(tileToBeReplaced).setImageResource(background1);
        tile.get(tileToBeDragged).setTag(background);
        tile.get(tileToBeReplaced).setTag(background1);
    }

    private void startRepeat() {
        repeatChecker.run();
    }

    Runnable repeatChecker = new Runnable() {
        @Override
        public void run() {
            try {
                checkRowForFive();
                checkRowForFour();
                checkRowForThree();
                checkColumnForFive();
                checkColumnForFour();
                checkColumnForThree();
                moveDownCandies();
            } finally {
                mHandler.postDelayed(repeatChecker, interval);
            }
        }
    };

    private void checkRowForThree() { /* Your existing implementation */ }
    private void checkRowForFour() { /* Your existing implementation */ }
    private void checkRowForFive() { /* Your existing implementation */ }
    private void checkColumnForThree() { /* Your existing implementation */ }
    private void checkColumnForFour() { /* Your existing implementation */ }
    private void checkColumnForFive() { /* Your existing implementation */ }
    private void moveDownCandies() { /* Your existing implementation */ }
}
