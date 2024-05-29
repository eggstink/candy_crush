package com.example.myapplication;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class Level3 extends AppCompatActivity {
    TextView tvMoves;
    int[] tiles = {
            R.drawable.diamond,
            R.drawable.gold,
            R.drawable.iron,
            R.drawable.lapis,
            R.drawable.netherite,
            R.drawable.redstone,
    };

    int maxNumOfMoves = 10;
    int widthOfBlock, noOfBlocks = 11, widthOfScreen, heightofScreen;
    ArrayList<ImageView> tile = new ArrayList<>();
    private boolean[][] pattern = {
            {false, false, false, false, false, false, false, false, false, false, false},
            {false, false, false, false, false, false, false, false, false, false, false},
            {true,  true,  false,  false, false, true, false, false, false, true,  false},
            {true,  false, true,  false, true,  false, true,  false, true,  false, true },
            {true,  false, true,  false, true,  true,  true,  false, true,  true,  true },
            {true,  false, true,  false, true,  false, true,  false, true,  false, true },
            {true,  true,  false,  false, true,  false, true,  false, true,  false, true},
            {false, false, false, false, false, false, false, false, false, false, false},
            {false, false, false, false, false, false, false, false, false, false, false}
    };
    int tileToBeDragged, tileToBeReplaced;
    int notTile = R.drawable.ic_launcher_background;
    MediaPlayer music3,pop;
    Handler mHandler;
    int interval = 300;
    Button btnReset3;
    TextView scoreRes;
    int score = 0;
    boolean swiped = false;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_level3);
        tvMoves = findViewById(R.id.moves3);
        scoreRes = findViewById(R.id.score3);
        btnReset3 = findViewById(R.id.reset3);
        music3 = MediaPlayer.create(Level3.this,R.raw.daa);
        music3.setLooping(true);
        music3.start();
        pop = MediaPlayer.create(Level3.this,R.raw.matchpop);

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
                    if (tileToBeReplaced >= 0 && tileToBeReplaced / noOfBlocks == tileToBeDragged / noOfBlocks) {
                        swiped = true;
                        candyInterchange();
                    }
                }

                @Override
                void onSwipeRight() {
                    super.onSwipeRight();
                    tileToBeDragged = imageView.getId();
                    tileToBeReplaced = tileToBeDragged + 1;
                    if (tileToBeReplaced < noOfBlocks * noOfBlocks && tileToBeReplaced / noOfBlocks == tileToBeDragged / noOfBlocks) {
                        swiped = true;
                        candyInterchange();
                    }
                }

                @Override
                void onSwipeTop() {
                    super.onSwipeTop();
                    tileToBeDragged = imageView.getId();
                    tileToBeReplaced = tileToBeDragged - noOfBlocks;
                    if (tileToBeReplaced >= 0) {
                        swiped = true;
                        candyInterchange();
                    }
                }

                @Override
                void onSwipeBottom() {
                    super.onSwipeBottom();
                    tileToBeDragged = imageView.getId();
                    tileToBeReplaced = tileToBeDragged + noOfBlocks;
                    if (tileToBeReplaced < noOfBlocks * noOfBlocks) {
                        swiped = true;
                        candyInterchange();
                    }
                }
            });
        }
        btnReset3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                music3.stop();
                Intent intent = new Intent(Level3.this, Level3.class);
                finish();
                startActivity(intent);
            }
        });
    }

    private void createboard() {
        GridLayout gridLayout = findViewById(R.id.board3);
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

            int row = i / noOfBlocks;
            int col = i % noOfBlocks;

            if (row < pattern.length && col < pattern[row].length && pattern[row][col]) {
                int rand;
                do {
                    rand = random.nextInt(tiles.length);
                    imageView.setImageResource(tiles[rand]);
                    imageView.setTag(tiles[rand]);
                } while (hasInitialMatches(i, tiles[rand]));
            } else {
                imageView.setImageResource(notTile);
                imageView.setTag(notTile);
            }

            tile.add(imageView);
            gridLayout.addView(imageView);
        }

        Log.d("Level3", "Board created with specified pattern");
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

    private boolean isValidSwap(int tileDragged, int tileReplaced) {
        int rowDragged = tileDragged / noOfBlocks;
        int colDragged = tileDragged % noOfBlocks;
        int rowReplaced = tileReplaced / noOfBlocks;
        int colReplaced = tileReplaced % noOfBlocks;

        return pattern[rowDragged][colDragged] && pattern[rowReplaced][colReplaced];
    }

    private void candyInterchange() {
        if (isValidSwap(tileToBeDragged, tileToBeReplaced)) {
            int background = (int) tile.get(tileToBeReplaced).getTag();
            int background1 = (int) tile.get(tileToBeDragged).getTag();
            tile.get(tileToBeDragged).setImageResource(background);
            tile.get(tileToBeReplaced).setImageResource(background1);
            tile.get(tileToBeDragged).setTag(background);
            tile.get(tileToBeReplaced).setTag(background1);

            if (!hasMatches()) {
                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        tile.get(tileToBeDragged).setImageResource(background1);
                        tile.get(tileToBeReplaced).setImageResource(background);
                        tile.get(tileToBeDragged).setTag(background1);
                        tile.get(tileToBeReplaced).setTag(background);
                    }
                }, 500);

                swiped = false;

                Toast.makeText(this, "Invalid move! Please make a valid move.", Toast.LENGTH_SHORT).show();
            } else {
                tvMoves.setText("" + maxNumOfMoves--);
                if (maxNumOfMoves <= 0 && score < 100) {
                    Toast.makeText(this, "No more moves left!", Toast.LENGTH_SHORT).show();
                }
            }
        } else {
            Toast.makeText(this, "Invalid move! You can only swap adjacent tiles that are part of the pattern.", Toast.LENGTH_SHORT).show();
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

    private void checkRowForThree() {
        for (int i = 0; i < 118; i++) {
            int chosenTile = (int) tile.get(i).getTag();
            boolean isBlank = (int) tile.get(i).getTag() == notTile;
            Integer[] notValid = {8, 9, 10, 19, 20, 21, 30, 31, 32, 41, 42, 43, 52, 53, 54, 63, 64, 65, 74, 75, 76, 85, 86, 87, 96, 97, 98, 107, 108, 109};
            List<Integer> list = Arrays.asList(notValid);
            if (!list.contains(i)) {
                int x = i;
                if ((int) tile.get(x++).getTag() == chosenTile && !isBlank &&
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
                    pop.start();
                }
            }
        }
        checkWinCondition();
    }

    private void checkRowForFour() {
        for (int i = 0; i < 117; i++) {
            int chosenTile = (int) tile.get(i).getTag();
            boolean isBlank = (int) tile.get(i).getTag() == notTile;
            Integer[] notValid = {8, 9, 10, 19, 20, 21, 30, 31, 32, 41, 42, 43, 52, 53, 54, 63, 64, 65, 74, 75, 76, 85, 86, 87, 96, 97, 98, 107, 108, 109};
            List<Integer> list = Arrays.asList(notValid);
            if (!list.contains(i)) {
                int x = i;
                if ((int) tile.get(x++).getTag() == chosenTile && !isBlank &&
                        (int) tile.get(x++).getTag() == chosenTile &&
                        (int) tile.get(x++).getTag() == chosenTile &&
                        (int) tile.get(x).getTag() == chosenTile) {
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
                }
            }
        }
        checkWinCondition();
    }

    private void checkRowForFive() {
        for (int i = 0; i < 116; i++) {
            int chosenTile = (int) tile.get(i).getTag();
            boolean isBlank = (int) tile.get(i).getTag() == notTile;
            Integer[] notValid = {7, 8, 9, 10, 18, 19, 20, 21, 29, 30, 31, 32, 40, 41, 42, 43, 51, 52, 53, 54, 62, 63, 64, 65, 73, 74, 75, 76, 84, 85, 86, 87, 95, 96, 97, 98, 106, 107, 108, 109};
            List<Integer> list = Arrays.asList(notValid);
            if (!list.contains(i)) {
                int x = i;
                if ((int) tile.get(x++).getTag() == chosenTile && !isBlank &&
                        (int) tile.get(x++).getTag() == chosenTile &&
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
                }
            }
        }
        checkWinCondition();
    }

    private void checkColumnForThree() {
        for (int i = 0; i < 77; i++) {
            int chosenTile = (int) tile.get(i).getTag();
            boolean isBlank = (int) tile.get(i).getTag() == notTile;
            int x = i;
            if ((int) tile.get(x).getTag() == chosenTile && !isBlank &&
                    (int) tile.get(x + noOfBlocks).getTag() == chosenTile &&
                    (int) tile.get(x + 2 * noOfBlocks).getTag() == chosenTile) {
                score += 3;
                scoreRes.setText(String.valueOf(score));
                tile.get(x).setImageResource(notTile);
                tile.get(x).setTag(notTile);
                x = x + noOfBlocks;
                tile.get(x).setImageResource(notTile);
                tile.get(x).setTag(notTile);
                x = x + noOfBlocks;
                tile.get(x).setImageResource(notTile);
                tile.get(x).setTag(notTile);
                pop.start();
            }
        }
        checkWinCondition();
    }

    private void checkColumnForFour() {
        for (int i = 0; i < 66; i++) {
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
                x = x + noOfBlocks;
                tile.get(x).setImageResource(notTile);
                tile.get(x).setTag(notTile);
                x = x + noOfBlocks;
                tile.get(x).setImageResource(notTile);
                tile.get(x).setTag(notTile);
                x = x + noOfBlocks;
                tile.get(x).setImageResource(notTile);
                tile.get(x).setTag(notTile);
                pop.start();
            }
        }
        checkWinCondition();
    }

    private void checkColumnForFive() {
        for (int i = 0; i < 55; i++) {
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
                x = x + noOfBlocks;
                tile.get(x).setImageResource(notTile);
                tile.get(x).setTag(notTile);
                x = x + noOfBlocks;
                tile.get(x).setImageResource(notTile);
                tile.get(x).setTag(notTile);
                x = x + noOfBlocks;
                tile.get(x).setImageResource(notTile);
                tile.get(x).setTag(notTile);
                x = x + noOfBlocks;
                tile.get(x).setImageResource(notTile);
                tile.get(x).setTag(notTile);
                pop.start();
            }
        }
        checkWinCondition();
    }

    private void moveDownTiles() {
        int numRows = pattern.length;
        int numCols = pattern[0].length;

        for (int row = numRows - 2; row >= 0; row--) {
            for (int col = 0; col < numCols; col++) {
                if (pattern[row][col] && pattern[row + 1][col]) {
                    int currentIndex = row * noOfBlocks + col;
                    int belowIndex = (row + 1) * noOfBlocks + col;

                    if ((int) tile.get(belowIndex).getTag() == notTile) {
                        tile.get(belowIndex).setImageResource((int) tile.get(currentIndex).getTag());
                        tile.get(belowIndex).setTag(tile.get(currentIndex).getTag());
                        tile.get(currentIndex).setImageResource(notTile);
                        tile.get(currentIndex).setTag(notTile);
                    }
                }
            }
        }

        for (int row = 0; row < numRows; row++) {
            for (int col = 0; col < numCols; col++) {
                if (pattern[row][col]) {
                    int currentIndex = row * noOfBlocks + col;
                    if ((int) tile.get(currentIndex).getTag() == notTile) {
                        int rand = (int) Math.floor(Math.random() * tiles.length);
                        tile.get(currentIndex).setImageResource(tiles[rand]);
                        tile.get(currentIndex).setTag(tiles[rand]);
                    }
                }
            }
        }
    }


    private void startRepeat() {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                moveDownTiles();
                if (swiped) {
                    hasMatches();
                }
                mHandler.postDelayed(this, interval);
            }
        });
    }

    private void checkWinCondition() {
        if (score >= 10) {
            Toast.makeText(this, "You win!", Toast.LENGTH_SHORT).show();
            finish();
            music3.stop();
            startActivity(new Intent(Level3.this, SelectLvlActivity.class));
        }
    }
}
