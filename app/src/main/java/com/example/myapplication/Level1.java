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

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class Level1 extends AppCompatActivity {
    TextView tvMoves;
    MediaPlayer music,pop;
    Button btnExit, btnContinue, btnExit2, btnLeaderboard, btnExitGame, reset;
    Dialog dialog, scoreDialog;
    FirebaseFirestore firestore;


    int[] tiles = {
            R.drawable.fries,
            R.drawable.cola,
            R.drawable.cocktail,
            R.drawable.hotdog,
            R.drawable.donut,
            R.drawable.lollipop
    };


    int maxNumOfMoves = 20;
    int widthOfBlock, noOfBlocks = 8, widthOfScreen, heightofScreen;
    ArrayList<ImageView> tile = new ArrayList<>();
    int tileToBeDraged, tileToBeReplaced;
    int notTile = R.drawable.ic_launcher_background;
    Handler mHandler;
    int interval = 300;
    TextView scoreRes, finalScore;
    TextView numOfMoves;
    int score = 0;
    boolean swiped = false;
    boolean hasWon = false;

    Thread repeatThread;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_level1);

        mHandler = new Handler();
        repeatThread = new Thread(new Level1.RepeatRunnable());
        repeatThread.start();

        tvMoves = (TextView)findViewById(R.id.moves);
        music = MediaPlayer.create(Level1.this,R.raw.lvl1);
        music.setLooping(true);
        music.start();

        pop = MediaPlayer.create(Level1.this,R.raw.matchpop);
        pop.setLooping(false);


        dialog = new Dialog(Level1.this);
        dialog.setContentView(R.layout.confirm_dialog);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.setCancelable(false);

        scoreDialog = new Dialog(Level1.this);
        scoreDialog.setContentView(R.layout.highscore_dialog);
        scoreDialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        scoreDialog.setCancelable(false);

        btnExit = dialog.findViewById(R.id.btnExit);
        btnContinue = dialog.findViewById(R.id.btnContinue);
        btnExit2 = scoreDialog.findViewById(R.id.btnExit2);
        btnLeaderboard = scoreDialog.findViewById(R.id.btnLeaderboard);
        finalScore = scoreDialog.findViewById(R.id.tvScore);

        scoreRes = findViewById(R.id.score);
        numOfMoves = findViewById(R.id.score);
        reset = findViewById(R.id.reset);
        btnExitGame = findViewById(R.id.btnExitGame);
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
        btnExitGame.setOnClickListener(view->{
            finalScore.setText(String.valueOf(score));
            scoreDialog.show();
            endCheckers();
        });
        reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Level1.this, Level1.class);
                music.stop();
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
                    moveDownCandies();
                    if (swiped) {
                        hasMatches();
                    }
                    mHandler.postDelayed(this, interval);
                }
            });
            Looper.loop();
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

                    if(hasWon){
                        if(maxNumOfMoves <= 0) {
                            finalScore.setText(String.valueOf(score));
                            scoreDialog.show();
                            endCheckers();
                        }
                    }
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

                    if(hasWon){
                        if(maxNumOfMoves <= 0) {
                            finalScore.setText(String.valueOf(score));
                            scoreDialog.show();
                            endCheckers();
                        }

                    }
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

                    if(hasWon){
                        if(maxNumOfMoves <= 0) {
                            finalScore.setText(String.valueOf(score));
                            scoreDialog.show();
                            endCheckers();
                        }
                    }
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

                if(hasWon){
                    if(maxNumOfMoves <= 0) {
                        finalScore.setText(String.valueOf(score));
                        scoreDialog.show();
                        endCheckers();
                    }
                }
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

                if(hasWon){
                    if(maxNumOfMoves <= 0) {
                        finalScore.setText(String.valueOf(score));
                        scoreDialog.show();
                        endCheckers();
                    }

                }
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

                if(hasWon){
                    if(maxNumOfMoves <= 0) {
                        finalScore.setText(String.valueOf(score));
                        scoreDialog.show();
                        endCheckers();
                    }
                }
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

    private void endCheckers(){

        btnExit2.setOnClickListener(view->{
            insertScore();
            finish();
            music.stop();
            startActivity(new Intent(Level1.this, SelectLvlActivity.class));
        });

        btnLeaderboard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                scoreDialog.dismiss();
                findViewById(R.id.background_overlay).setVisibility(View.VISIBLE);
                LeaderboardFragment leaderboardFragment = new LeaderboardFragment();
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                transaction.add(android.R.id.content, leaderboardFragment).addToBackStack(null).commit();
            }
        });

        btnExit.setOnClickListener(view->{
            insertScore();
            finish();
            music.stop();
            startActivity(new Intent(Level1.this, SelectLvlActivity.class));
        });
        btnContinue.setOnClickListener(view->{
            hasWon = true;
            dialog.dismiss();
        });

    }
    private void insertScore(){
        firestore = FirebaseFirestore.getInstance();
        String currUser = FirebaseAuth.getInstance().getUid();

        DocumentReference docref = firestore.collection("users").document(currUser).collection("highestScores").document("level1");
        docref.get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                int highestScore = documentSnapshot.getLong("highestScore").intValue();
                Log.d("TAG", "Highest score retrieved successfully: " + highestScore);

                if(score > highestScore) {
                    docref.update("highestScore", score).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            Log.d("TAG", "Highest score updated successfully");
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.e("TAG", "Error updating highest score", e);
                        }
                    });
                }
            } else {
                Log.d("TAG", "User document does not exist");
            }
        })
        .addOnFailureListener(e -> {
            Log.e("TAG", "Error retrieving highest score", e);
        });
    }
    private void checkWinCondition() {
        if (score >= 50 && !hasWon) {
            dialog.show();
            endCheckers();
        }
    }

}