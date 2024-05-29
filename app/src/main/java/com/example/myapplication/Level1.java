package com.example.myapplication;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.ViewGroup;
import android.view.animation.GridLayoutAnimationController;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class Level1 extends AppCompatActivity {
    TextView tvMoves;
    MediaPlayer music,pop;

    int[] tiles = {
            R.drawable.diamond,
            R.drawable.gold,
            R.drawable.iron,
            R.drawable.lapis,
            R.drawable.netherite,
            R.drawable.redstone,
    };

    int maxNumOfMoves = 20;
    int widthOfBlock, noOfBlocks = 8, widthOfScreen, heightofScreen;
    ArrayList<ImageView> tile = new ArrayList<>();
    int tileToBeDraged, tileToBeReplaced;
    int notTile = R.drawable.ic_launcher_background;
    Handler mHandler;
    int interval = 300;
    TextView scoreRes;
    TextView numOfMoves;
    int score = 0;
    boolean swiped = false;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_level1);
        tvMoves = (TextView)findViewById(R.id.moves);
        music = MediaPlayer.create(Level1.this,R.raw.lvl1);
        music.setLooping(true);
        music.start();

        pop = MediaPlayer.create(Level1.this,R.raw.matchpop);
        pop.setLooping(false);

        scoreRes = findViewById(R.id.score);
        numOfMoves = findViewById(R.id.score);
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        widthOfScreen = dm.widthPixels;
        heightofScreen = dm.heightPixels;
        widthOfBlock = widthOfScreen/noOfBlocks;
        createboard();
        mHandler = new Handler();
        startRepeat();

        for(ImageView imageView: tile){
            imageView.setOnTouchListener(new OnSwipeListener(this){
                @Override
                void onSwipeLeft() {
                    super.onSwipeLeft();
                    tileToBeDraged = imageView.getId();
                    tileToBeReplaced = tileToBeDraged - 1;
                    if (tileToBeReplaced >= 0) {
                        swiped = true;
                        candyInterchange();
                    }
                }

                @Override
                void onSwipeRight() {
                    super.onSwipeRight();
                    tileToBeDraged = imageView.getId();
                    tileToBeReplaced = tileToBeDraged + 1;
                    if (tileToBeReplaced < noOfBlocks * noOfBlocks) {
                        swiped = true;
                        candyInterchange();
                    }
                }

                @Override
                void onSwipeTop() {
                    super.onSwipeTop();
                    tileToBeDraged = imageView.getId();
                    tileToBeReplaced = tileToBeDraged - noOfBlocks;
                    if (tileToBeReplaced >= 0) {
                        swiped = true;
                        candyInterchange();
                    }
                }

                @Override
                void onSwipeBottom() {
                    super.onSwipeBottom();
                    tileToBeDraged = imageView.getId();
                    tileToBeReplaced = tileToBeDraged + noOfBlocks;
                    if (tileToBeReplaced < noOfBlocks * noOfBlocks) {
                        swiped = true;
                        candyInterchange();
                    }
                }

            });
        }
    }

    private boolean hasMatches() {
        int originalScore = score;

        checkRowForFive();
        checkRowForFour();
        checkRowForThree();
        checkColumnForFive();
        checkColumnForFour();
        checkColumnForThree();

        return score > originalScore;
    }


    private void checkRowForThree(){
        OnSwipeListener swipeListener = new OnSwipeListener(this);
        for(int i = 0; i < 61;i++){
            int chosenTile = (int)tile.get(i).getTag();
            boolean isBlank = (int)tile.get(i).getTag() == notTile;
            Integer[] notValid = {6,7,14,15,22,23,30,31,38,39,46,47,54,55};
            List<Integer> list = Arrays.asList(notValid);
            if(!list.contains(i)){
                int x = i;
                if((int)tile.get(x++).getTag()==chosenTile&&!isBlank&&(int)tile.get(x++).getTag()==chosenTile
                        &&(int) tile.get(x).getTag()==chosenTile){
                    score += 3;
                    scoreRes.setText(String.valueOf(score));
                    tile.get(x).setImageResource(notTile);
                    tile.get(x).setTag(notTile);
                    x--;
                    tile.get(x).setImageResource(notTile);
                    tile.get(x).setTag(notTile);
                    x--;
                    tile.get(x).setImageResource(notTile);
                    tile.get(x).setTag(notTile);
                    pop.start();
                    if(swiped == true){
                        tvMoves.setText("" + maxNumOfMoves--);
                    }
                    checkWinCondition();
                }
            }
        }
        swiped = false;
        moveDownCandies();
    }

    private void checkRowForFour() {
        OnSwipeListener swipeListener = new OnSwipeListener(this);
        for (int i = 0; i < 61; i++) {
            int chosenTile = (int) tile.get(i).getTag();
            boolean isBlank = (int) tile.get(i).getTag() == notTile;
            Integer[] notValid = {6, 7, 14, 15, 22, 23, 30, 31, 38, 39, 46, 47, 54, 55};
            List<Integer> list = Arrays.asList(notValid);
            if (!list.contains(i)) {
                int x = i;
                if ((int) tile.get(x++).getTag() == chosenTile && !isBlank &&
                        (int) tile.get(x++).getTag() == chosenTile &&
                        (int) tile.get(x++).getTag() == chosenTile) {
                    score += 4;
                    scoreRes.setText(String.valueOf(score));
                    tile.get(x).setImageResource(notTile);
                    tile.get(x).setTag(notTile);
                    x--;
                    tile.get(x).setImageResource(notTile);
                    tile.get(x).setTag(notTile);
                    x--;
                    tile.get(x).setImageResource(notTile);
                    tile.get(x).setTag(notTile);
                    x--;
                    tile.get(x).setImageResource(notTile);
                    tile.get(x).setTag(notTile);
                    pop.start();
                    if (swiped) {
                        tvMoves.setText("" + maxNumOfMoves--);
                    }
                    checkWinCondition();
                }
            }
        }
        swiped = false;
        moveDownCandies();
    }

    private void checkRowForFive() {
        OnSwipeListener swipeListener = new OnSwipeListener(this);
        for (int i = 0; i < 61; i++) {
            int chosenTile = (int) tile.get(i).getTag();
            boolean isBlank = (int) tile.get(i).getTag() == notTile;
            Integer[] notValid = {6, 7, 14, 15, 22, 23, 30, 31, 38, 39, 46, 47, 54, 55};
            List<Integer> list = Arrays.asList(notValid);
            if (!list.contains(i)) {
                int x = i;
                if ((int) tile.get(x++).getTag() == chosenTile && !isBlank &&
                        (int) tile.get(x++).getTag() == chosenTile &&
                        (int) tile.get(x++).getTag() == chosenTile &&
                        (int) tile.get(x).getTag() == chosenTile) {
                    score += 5;
                    scoreRes.setText(String.valueOf(score));
                    tile.get(x).setImageResource(notTile);
                    tile.get(x).setTag(notTile);
                    x--;
                    tile.get(x).setImageResource(notTile);
                    tile.get(x).setTag(notTile);
                    x--;
                    tile.get(x).setImageResource(notTile);
                    tile.get(x).setTag(notTile);
                    x--;
                    tile.get(x).setImageResource(notTile);
                    tile.get(x).setTag(notTile);
                    x--;
                    tile.get(x).setImageResource(notTile);
                    tile.get(x).setTag(notTile);
                    pop.start();
                    if (swiped) {
                        tvMoves.setText("" + maxNumOfMoves--);
                    }
                    checkWinCondition();
                }
            }
        }
        swiped = false;
        moveDownCandies();
    }

    private void checkColumnForThree(){
        for(int i = 0; i < 47 ;i++){
            int chosenTile = (int)tile.get(i).getTag();
            boolean isBlank = (int)tile.get(i).getTag() == notTile;
            int x = i;
            if((int)tile.get(x).getTag()==chosenTile&&!isBlank&&
                    (int)tile.get(x+noOfBlocks).getTag()==chosenTile
                    &&(int) tile.get(x+2*noOfBlocks).getTag()==chosenTile){

                score += 3;
                scoreRes.setText(String.valueOf(score));
                tile.get(x).setImageResource(notTile);
                tile.get(x).setTag(notTile);
                x+= +noOfBlocks;
                tile.get(x).setImageResource(notTile);
                tile.get(x).setTag(notTile);
                x+= +noOfBlocks;
                tile.get(x).setImageResource(notTile);
                tile.get(x).setTag(notTile);
                pop.start();
                if(swiped == true){
                    tvMoves.setText("" + maxNumOfMoves--);
                }
                checkWinCondition();
            }
            swiped = false;
        }
        moveDownCandies();
    }

    private void checkColumnForFour() {
        for (int i = 0; i < 64 - 4 * noOfBlocks; i++) {
            int chosenTile = (int) tile.get(i).getTag();
            boolean isBlank = (int) tile.get(i).getTag() == notTile;
            int x = i;
            if ((int) tile.get(x).getTag() == chosenTile && !isBlank &&
                    (int) tile.get(x + noOfBlocks).getTag() == chosenTile &&
                    (int) tile.get(x + 2 * noOfBlocks).getTag() == chosenTile &&
                    (int) tile.get(x + 3 * noOfBlocks).getTag() == chosenTile) {
                score += 4;
                scoreRes.setText(String.valueOf(score));
                tile.get(x).setImageResource(notTile);
                tile.get(x).setTag(notTile);
                x += noOfBlocks;
                tile.get(x).setImageResource(notTile);
                tile.get(x).setTag(notTile);
                x += noOfBlocks;
                tile.get(x).setImageResource(notTile);
                tile.get(x).setTag(notTile);
                x += noOfBlocks;
                tile.get(x).setImageResource(notTile);
                tile.get(x).setTag(notTile);
                pop.start();
                if (swiped) {
                    tvMoves.setText("" + maxNumOfMoves--);
                }
                checkWinCondition();
            }
            swiped = false;
        }
        moveDownCandies();
    }

    private void checkColumnForFive() {
        for (int i = 0; i < 64 - 4 * noOfBlocks; i++) {
            int chosenTile = (int) tile.get(i).getTag();
            boolean isBlank = (int) tile.get(i).getTag() == notTile;
            int x = i;
            if ((int) tile.get(x).getTag() == chosenTile && !isBlank &&
                    (int) tile.get(x + noOfBlocks).getTag() == chosenTile &&
                    (int) tile.get(x + 2 * noOfBlocks).getTag() == chosenTile &&
                    (int) tile.get(x + 3 * noOfBlocks).getTag() == chosenTile &&
                    (int) tile.get(x + 4 * noOfBlocks).getTag() == chosenTile) {
                score += 5;
                scoreRes.setText(String.valueOf(score));
                tile.get(x).setImageResource(notTile);
                tile.get(x).setTag(notTile);
                x += noOfBlocks;
                tile.get(x).setImageResource(notTile);
                tile.get(x).setTag(notTile);
                x += noOfBlocks;
                tile.get(x).setImageResource(notTile);
                tile.get(x).setTag(notTile);
                x += noOfBlocks;
                tile.get(x).setImageResource(notTile);
                tile.get(x).setTag(notTile);
                x += noOfBlocks;
                tile.get(x).setImageResource(notTile);
                tile.get(x).setTag(notTile);
                pop.start();
                if (swiped) {
                    tvMoves.setText("" + maxNumOfMoves--);
                }
                checkWinCondition();
            }
            swiped = false;
        }
        moveDownCandies();
    }


    private void moveDownCandies() {
        for (int col = 0; col < noOfBlocks; col++) {
            int emptySpaces = 0;
            for (int row = noOfBlocks - 1; row >= 0; row--) {
                int index = row * noOfBlocks + col;
                if ((int) tile.get(index).getTag() == notTile) {
                    emptySpaces++;
                } else if (emptySpaces > 0) {
                    int newIndex = (row + emptySpaces) * noOfBlocks + col;
                    tile.get(newIndex).setImageResource((int) tile.get(index).getTag());
                    tile.get(newIndex).setTag(tile.get(index).getTag());
                    tile.get(index).setImageResource(notTile);
                    tile.get(index).setTag(notTile);
                }
            }
            // Fill in empty spaces at the top with random candies
            for (int i = 0; i < emptySpaces; i++) {
                int randomColor = (int) Math.floor(Math.random() * tiles.length);
                int index = i * noOfBlocks + col;
                tile.get(index).setImageResource(tiles[randomColor]);
                tile.get(index).setTag(tiles[randomColor]);
            }
        }
    }

    Runnable repeatChecker = new Runnable() {
        @Override
        public void run() {
            try{
                checkRowForFive();
                checkRowForFour();
                checkRowForThree();
                checkColumnForFive();
                checkColumnForFour();
                checkColumnForThree();
                moveDownCandies();

            }finally {
                mHandler.postDelayed(repeatChecker,interval);
            }
        }
    };


    void startRepeat(){
        repeatChecker.run();
    }

    private void candyInterchange() {
        int background = (int) tile.get(tileToBeReplaced).getTag();
        int background1 = (int) tile.get(tileToBeDraged).getTag();

        tile.get(tileToBeDraged).setImageResource(background);
        tile.get(tileToBeReplaced).setImageResource(background1);
        tile.get(tileToBeDraged).setTag(background);
        tile.get(tileToBeReplaced).setTag(background1);

        if (!hasMatches()) {
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    tile.get(tileToBeDraged).setImageResource(background1);
                    tile.get(tileToBeReplaced).setImageResource(background);
                    tile.get(tileToBeDraged).setTag(background1);
                    tile.get(tileToBeReplaced).setTag(background);
                }
            }, 500);

            swiped = false;

            Toast.makeText(this, "Invalid move! Please make a valid move.", Toast.LENGTH_SHORT).show();
        } else {
            // If the move is valid, update the moves count and check for win condition
            tvMoves.setText("" + maxNumOfMoves--);
            if (maxNumOfMoves <= 0 && score < 50) {
                Toast.makeText(this, "No more moves left!", Toast.LENGTH_SHORT).show();
            }
        }
    }



    private void createboard() {
        GridLayout gridLayout = findViewById(R.id.board);
        gridLayout.setRowCount(noOfBlocks);
        gridLayout.setColumnCount(noOfBlocks);
        gridLayout.getLayoutParams().width = widthOfScreen;
        gridLayout.getLayoutParams().height = widthOfScreen;

        Random random = new Random();

        for (int i = 0; i < noOfBlocks * noOfBlocks; i++) {
            ImageView imageView = new ImageView(this);
            imageView.setId(i);
            imageView.setLayoutParams(new ViewGroup.LayoutParams(widthOfBlock, widthOfBlock));
            imageView.setMaxHeight(widthOfBlock);
            imageView.setMaxWidth(widthOfBlock);

            int rand;
            do {
                rand = random.nextInt(tiles.length);
                imageView.setImageResource(tiles[rand]);
                imageView.setTag(tiles[rand]);
            } while (hasInitialMatches(i, tiles[rand]));

            tile.add(imageView);
            gridLayout.addView(imageView);
        }

        Log.d("Level3", "Board created without pre-matching tiles");
    }

    private boolean hasInitialMatches(int index, int drawable) {
        int row = index / noOfBlocks;
        int col = index % noOfBlocks;

        if (col >= 2) {
            if (tile.get(index - 1).getTag().equals(drawable) && tile.get(index - 2).getTag().equals(drawable)) {
                return true;
            }
        }

        if (row >= 2) {
            if (tile.get(index - noOfBlocks).getTag().equals(drawable) && tile.get(index - 2 * noOfBlocks).getTag().equals(drawable)) {
                return true;
            }
        }
        return false;
    }


    private void checkWinCondition() {
        if (score >= 50) {
            Toast.makeText(this, "You win!", Toast.LENGTH_SHORT).show();
            finish();
            music.stop();
            startActivity(new Intent(Level1.this, SelectLvlActivity.class));

        }
    }

}