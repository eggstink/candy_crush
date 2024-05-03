package com.example.myapplication;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
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

public class MainActivity extends AppCompatActivity {

    int[] tiles = {
            R.drawable.blue,
            R.drawable.red,
            R.drawable.green,
            R.drawable.yellow,
    };

    int widthOfBlock, noOfBlocks = 5, widthOfScreen, heightofScreen;
    ArrayList<ImageView> tile = new ArrayList<>();
    int tileToBeDraged, tileToBeReplaced;
    int notTile = R.drawable.ic_launcher_background;
    Handler mHandler;
    int interval = 20;
    TextView scoreRes;
    int score = 0;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        scoreRes = findViewById(R.id.score);
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        widthOfScreen = dm.widthPixels;
        heightofScreen = dm.heightPixels;
        widthOfBlock = widthOfScreen/noOfBlocks;
        createboard();
        for(ImageView imageView: tile){
            imageView.setOnTouchListener(new OnSwipeListener(this){
                @Override
                void onSipwLeft() {
                    super.onSipwLeft();
                    tileToBeDraged = imageView.getId();
                    tileToBeReplaced = tileToBeDraged -1;
                    candyInterchange();
                }

                @Override
                void onSipwRight() {
                    super.onSipwRight();
                    tileToBeDraged = imageView.getId();
                    tileToBeReplaced = tileToBeDraged +1;
                    candyInterchange();
                }

                @Override
                void onSipwTop() {
                    super.onSipwTop();
                    tileToBeDraged = imageView.getId();
                    tileToBeReplaced = tileToBeDraged - noOfBlocks;
                    candyInterchange();
                }

                @SuppressLint("ClickableViewAccessibility")
                @Override
                void onSipwBottom() {
                    super.onSipwBottom();
                    tileToBeDraged = imageView.getId();
                    tileToBeReplaced = tileToBeDraged + noOfBlocks;
                    candyInterchange();

                }
            });
        }
        mHandler = new Handler();
        startRepeat();
    }
    private void checkRowForThree(){
        for(int i = 0; i < 23;i++){
            int chosenTile = (int)tile.get(i).getTag();
            boolean isBlank = (int)tile.get(i).getTag() == notTile;
            Integer[] notValid = {3,4,8,9,13,14,18,19,23,24};
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

                }
            }
        }
        moveDownCandies();
    }
    private void checkColumnForThree(){
        for(int i = 0; i < 13;i++){
            int chosenTile = (int)tile.get(i).getTag();
            boolean isBlank = (int)tile.get(i).getTag() == notTile;


                int x = i;
                if((int)tile.get(x).getTag()==chosenTile&&!isBlank&&(int)tile.get(x+noOfBlocks).getTag()==chosenTile
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

                }

        }
        moveDownCandies();
    }

    private void moveDownCandies(){
        Integer[] firstRow = {0,1,2,3,4};
        List<Integer> list = Arrays.asList(firstRow);
        for(int i = 19; i >=0;i--){
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
                checkRowForThree();
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
    private void candyInterchange(){
        int background = (int)tile.get(tileToBeReplaced).getTag();
        int background1 = (int)tile.get(tileToBeDraged).getTag();
        tile.get(tileToBeDraged).setImageResource(background);
        tile.get(tileToBeReplaced).setImageResource(background1);
        tile.get(tileToBeDraged).setTag(background);
        tile.get(tileToBeReplaced).setTag(background1);
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
}