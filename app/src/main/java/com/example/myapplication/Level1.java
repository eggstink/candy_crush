package com.example.myapplication;

import android.annotation.SuppressLint;
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

public class Level1 extends AppCompatActivity {
    TextView tvMoves;
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
                    score += 1;
                    scoreRes.setText(String.valueOf(score));
                    tile.get(x).setImageResource(notTile);
                    tile.get(x).setTag(notTile);
                    x--;
                    tile.get(x).setImageResource(notTile);
                    tile.get(x).setTag(notTile);
                    x--;
                    tile.get(x).setImageResource(notTile);
                    tile.get(x).setTag(notTile);
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
                    score += 2;
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
                    x--;
                    tile.get(x).setImageResource(notTile);
                    tile.get(x).setTag(notTile);
                    x--;
                    tile.get(x).setImageResource(notTile);
                    tile.get(x).setTag(notTile);
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

                score += 1;
                scoreRes.setText(String.valueOf(score));
                tile.get(x).setImageResource(notTile);
                tile.get(x).setTag(notTile);
                x+= +noOfBlocks;
                tile.get(x).setImageResource(notTile);
                tile.get(x).setTag(notTile);
                x+= +noOfBlocks;
                tile.get(x).setImageResource(notTile);
                tile.get(x).setTag(notTile);
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
                score += 1;
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
                score += 1;
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
                if (swiped) {
                    tvMoves.setText("" + maxNumOfMoves--);
                }
                checkWinCondition();
            }
            swiped = false;
        }
        moveDownCandies();
    }


    private void moveDownCandies(){
        Integer[] firstRow = {0,1,2,3,4,5,6,7};
        List<Integer> list = Arrays.asList(firstRow);
        for(int i = 55; i >=0;i--){
            if((int)tile.get(i+noOfBlocks).getTag()==notTile){
                tile.get(i+noOfBlocks).setImageResource((int)tile.get(i).getTag());
                tile.get(i+noOfBlocks).setTag(tile.get(i).getTag());
                tile.get(i).setImageResource(notTile);
                tile.get(i).setTag(notTile);

                if(list.contains(i)&&(int)tile.get(i).getTag()==notTile){
                    int randomColor = (int) Math.floor(Math.random()*tiles.length);
                    tile.get(i).setImageResource(tiles[randomColor]);
                    tile.get(i).setTag(tiles[randomColor]);
                }
            }
        }

        for(int i = 0; i < 5; i++){
            if((int) tile.get(i).getTag()==notTile){
                int randomColor = (int) Math.floor(Math.random()*tiles.length);
                tile.get(i).setImageResource(tiles[randomColor]);
                tile.get(i).setTag(tiles[randomColor]);
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
            if (maxNumOfMoves <= 0) {
                Toast.makeText(this, "No more moves left!", Toast.LENGTH_SHORT).show();
            }
        }
    }



    private void createboard() {
        GridLayout gridLayout = findViewById(R.id.board);
        gridLayout.setRowCount(noOfBlocks);
        gridLayout.setColumnCount(noOfBlocks);
        gridLayout.getLayoutParams().width=widthOfScreen;
        gridLayout.getLayoutParams().height=widthOfScreen;

        for(int i = 0; i < noOfBlocks*noOfBlocks;i++){
            ImageView imageView = new ImageView(this);
            imageView.setId(i);
            imageView.setLayoutParams(new ViewGroup.LayoutParams(widthOfBlock,widthOfBlock));
            imageView.setMaxHeight(widthOfBlock);
            imageView.setMaxWidth(widthOfBlock);
            int rand = (int) Math.floor(Math.random() * tiles.length);
            imageView.setImageResource(tiles[rand]);
            imageView.setTag(tiles[rand]);
            tile.add(imageView);
            gridLayout.addView(imageView);

        }

    }

    private void checkWinCondition() {
        if (score >= 50) {
            Toast.makeText(this, "You win!", Toast.LENGTH_SHORT).show();
        }
    }

}