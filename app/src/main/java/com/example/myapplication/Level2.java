package com.example.myapplication;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
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
import androidx.fragment.app.FragmentTransaction;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class Level2 extends AppCompatActivity {
    TextView tvMoves;
    MediaPlayer music2,pop;
    Button btnExit, btnContinue, btnExit2, btnLeaderboard, btnExitGame2;
    Dialog dialog, scoreDialog;
    FirebaseFirestore firestore;
    FirebaseAuth auth;
    String currUser;
    String currLevel;

    int[] tiles = {
            R.drawable.planet1,
            R.drawable.planet2,
            R.drawable.planet3,
            R.drawable.planet4,
            R.drawable.planet5,
            R.drawable.planet6
    };


    int maxNumOfMoves = 50, widthOfBlock, noOfBlocks = 8, widthOfScreen, heightofScreen;
    ArrayList<ImageView> tile = new ArrayList<>();
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
    int tileToBeDragged, tileToBeReplaced, notTile = R.drawable.ic_launcher_background, interval = 300, score = 0;
    Handler mHandler;
    Button btnReset;
    TextView scoreRes, finalScore;
    Thread repeatThread;
    boolean swiped = false, hasWon = false;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_level2);

        mHandler = new Handler();
        repeatThread = new Thread(new Level2.RepeatRunnable());
        repeatThread.start();

        music2 = MediaPlayer.create(Level2.this,R.raw.lvl2music);
        music2.setLooping(true);
        music2.start();

        pop = MediaPlayer.create(Level2.this,R.raw.matchpop);

        firestore = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
        currUser = auth.getCurrentUser().getUid();
        currLevel = "level2";

        tvMoves = findViewById(R.id.moves2);
        scoreRes = findViewById(R.id.score2);
        btnReset = findViewById(R.id.reset2);
        btnExitGame2 = findViewById(R.id.btnExitGame2);

        dialog = new Dialog(Level2.this);
        dialog.setContentView(R.layout.confirm_dialog);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.setCancelable(false);

        scoreDialog = new Dialog(Level2.this);
        scoreDialog.setContentView(R.layout.highscore_dialog);
        scoreDialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        scoreDialog.setCancelable(false);

        btnExit = dialog.findViewById(R.id.btnExit);
        btnContinue = dialog.findViewById(R.id.btnContinue);
        btnExit2 = scoreDialog.findViewById(R.id.btnExit2);
        btnLeaderboard = scoreDialog.findViewById(R.id.btnLeaderboard);
        finalScore = scoreDialog.findViewById(R.id.tvScore);

        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        widthOfScreen = dm.widthPixels;
        heightofScreen = dm.heightPixels;
        widthOfBlock = widthOfScreen / noOfBlocks;

        createBoard();
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

        btnExitGame2.setOnClickListener(view->{
            finalScore.setText(String.valueOf(score));
            scoreDialog.show();
            endCheckers();
        });

        btnReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                insertScore();
                music2.stop();
                Intent intent = new Intent(Level2.this, Level2.class);
                finish();
                startActivity(intent);
            }
        });
    }

    private class RepeatRunnable implements Runnable {
        @Override
        public void run() {
            Looper.prepare();
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
            Looper.loop();
        }
    }

    private void handleSwipe(ImageView imageView, int targetTileId) {
        tileToBeDragged = imageView.getId();
        tileToBeReplaced = targetTileId;
        if (isValidSwap(tileToBeDragged, tileToBeReplaced)) {
            swiped = true;
            candyInterchange();
        }
    }

    private void createBoard() {
        GridLayout gridLayout = findViewById(R.id.board2);
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

            if (row < crossPattern.length && col < crossPattern[row].length && crossPattern[row][col]) {
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

        Log.d("Level2", "Board created with specified pattern");
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
        int rowDragged = tileDragged / noOfBlocks, colDragged = tileDragged % noOfBlocks;
        int rowReplaced = tileReplaced / noOfBlocks, colReplaced = tileReplaced % noOfBlocks;
        return crossPattern[rowDragged][colDragged] && crossPattern[rowReplaced][colReplaced];
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
                }, 300);

                swiped = false;

                Toast.makeText(this, "Invalid move! Please make a valid move.", Toast.LENGTH_SHORT).show();
            } else {
                tvMoves.setText(" " + maxNumOfMoves--);
                if (maxNumOfMoves <= 0 && score < 80) {
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
        for (int i = 0; i < 61; i++) {
            int chosenTile = (int) tile.get(i).getTag();
            boolean isBlank = (int) tile.get(i).getTag() == notTile;
            Integer[] notValid = {6,7,14,15,22,23,30,31,38,39,46,47,54,55};List<Integer> list = Arrays.asList(notValid);
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
                    if(swiped == true) {
                        tvMoves.setText("" + maxNumOfMoves--);
                    }
                    checkWinCondition();

                    if (hasWon) {
                        if (maxNumOfMoves <= 0) {
                            finalScore.setText(String.valueOf(score));
                            scoreDialog.show();
                            endCheckers();
                        }
                    }
                }
            }
        }
        swiped = false;
        checkWinCondition();
    }


    private void checkRowForFour() {
        for (int i = 0; i < 60; i++) {
            int chosenTile = (int) tile.get(i).getTag();
            boolean isBlank = (int) tile.get(i).getTag() == notTile;
            Integer[] notValid = {6,7,14,15,22,23,30,31,38,39,46,47,54,55};
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
                    if(swiped == true) {
                        tvMoves.setText("" + maxNumOfMoves--);
                    }
                    checkWinCondition();

                    if (hasWon) {
                        if (maxNumOfMoves <= 0) {
                            finalScore.setText(String.valueOf(score));
                            scoreDialog.show();
                            endCheckers();
                        }
                    }
                }
            }
        }
        swiped = false;
        checkWinCondition();
    }

    private void checkRowForFive() {
        for (int i = 0; i < 59; i++) {
            int chosenTile = (int) tile.get(i).getTag();
            boolean isBlank = (int) tile.get(i).getTag() == notTile;
            Integer[] notValid = {6,7,14,15,22,23,30,31,38,39,46,47,54,55};
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
                    if(swiped == true) {
                        tvMoves.setText("" + maxNumOfMoves--);
                    }
                    checkWinCondition();

                    if (hasWon) {
                        if (maxNumOfMoves <= 0) {
                            finalScore.setText(String.valueOf(score));
                            scoreDialog.show();
                            endCheckers();
                        }
                    }
                }
            }
        }
        swiped = false;
        checkWinCondition();
    }

    private void checkColumnForThree() {
        for (int i = 0; i < 48; i++) {
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
                if(swiped == true) {
                    tvMoves.setText("" + maxNumOfMoves--);
                }
                checkWinCondition();

                if (hasWon) {
                    if (maxNumOfMoves <= 0) {
                        finalScore.setText(String.valueOf(score));
                        scoreDialog.show();
                        endCheckers();
                    }
                }
            }
            swiped = false;
        }
        checkWinCondition();
    }

    private void checkColumnForFour() {
        for (int i = 0; i < 40; i++) {
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
                if(swiped == true) {
                    tvMoves.setText("" + maxNumOfMoves--);
                }
                checkWinCondition();

                if (hasWon) {
                    if (maxNumOfMoves <= 0) {
                        finalScore.setText(String.valueOf(score));
                        scoreDialog.show();
                        endCheckers();
                    }
                }
            }
            swiped = false;
        }
        checkWinCondition();
    }

    private void checkColumnForFive() {
        for (int i = 0; i < 32; i++) {
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
                if(swiped == true) {
                    tvMoves.setText("" + maxNumOfMoves--);
                }
                checkWinCondition();

                if (hasWon) {
                    if (maxNumOfMoves <= 0) {
                        finalScore.setText(String.valueOf(score));
                        scoreDialog.show();
                        endCheckers();
                    }
                }
            }
            swiped = false;
        }
        checkWinCondition();
    }

    private void moveDownTiles() {
        int numRows = crossPattern.length;
        int numCols = crossPattern[0].length;

        for (int row = numRows - 2; row >= 0; row--) {
            for (int col = 0; col < numCols; col++) {
                if (crossPattern[row][col] && crossPattern[row + 1][col]) {
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
                if (crossPattern[row][col]) {
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

                    hasMatches();

                mHandler.postDelayed(this, interval);
            }
        });
    }
    private void endCheckers(){
        btnExit2.setOnClickListener(view->{
            insertScore();
            finish();
            music2.stop();
            startActivity(new Intent(Level2.this, SelectLvlActivity.class));
        });

        btnLeaderboard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                insertScore();
                scoreDialog.dismiss();
                String currentLevel = "level2";
                LeaderboardFragment leaderboardFragment = LeaderboardFragment.newInstance(currentLevel);
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                transaction.add(android.R.id.content, leaderboardFragment).addToBackStack(null).commit();
            }
        });

        btnExit.setOnClickListener(view-> {
            insertScore();
            finish();
            music2.stop();
            startActivity(new Intent(Level2.this, SelectLvlActivity.class));
        });

        btnContinue.setOnClickListener(view->{
            insertScore();
            hasWon = true;
            dialog.dismiss();
        });
    }

    private void insertScore() {
        DocumentReference docRef = firestore.collection("leaderboard").document(currUser);
        docRef.get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                int currentScore = score;
                int highestScore = documentSnapshot.getLong(currLevel) != null ? documentSnapshot.getLong(currLevel).intValue() : 0;

                if (currentScore > highestScore) {
                    docRef.update(currLevel, currentScore)
                            .addOnSuccessListener(aVoid -> Log.d("TAG", "Highest score updated successfully"))
                            .addOnFailureListener(e -> Log.e("TAG", "Error updating highest score", e));
                }
            } else {
                Map<String, Object> userData = new HashMap<>();
                userData.put(currLevel, score);

                docRef.set(userData)
                        .addOnSuccessListener(aVoid -> Log.d("TAG", "New user document created with score"))
                        .addOnFailureListener(e -> Log.e("TAG", "Error creating new user document", e));
            }
        }).addOnFailureListener(e -> Log.e("TAG", "Error retrieving user document", e));
    }

    private void checkWinCondition() {
        if (score >= 80 && !hasWon) {
            insertScore();
            dialog.show();
            endCheckers();
        }
    }

}
