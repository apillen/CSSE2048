package com.example.alexei.csse2048;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.provider.SyncStateContract;
import android.support.v4.app.Fragment;
import android.support.v4.view.GestureDetectorCompat;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by alexei on 4/16/17.
 */

public class BoardController extends Fragment{

   private BoardView boardView;
   private BoardModel boardModel;

   private EventInterface eventInterface;

   public static final String GRID = "pref_grid";
   public static final String SCORE = "pref_score";
   public static final String HI_SCORE = "pref_hi_score";

   @Override
   public View onCreateView(LayoutInflater inflater, ViewGroup container,
                            Bundle savedInstanceState) {
      super.onCreateView(inflater, container, savedInstanceState);


      // inflate the fragment_main.xml layout
      View view =
              inflater.inflate(R.layout.fragment_main, container, false);


      // get a reference to the CannonView
      boardView = (BoardView) view.findViewById(R.id.boardView);

      boardModel = new BoardModel();
      boardView.post(new Runnable() {
         @Override
         public void run() {
            boardView.viewCreated();
            //boardView.drawBoard(boardModel.getMatrix());
            boardView.setGrid(boardModel.getMatrix());
         }
      });

      final GestureDetector gesture = new GestureDetector(getActivity(),
              new GestureDetector.SimpleOnGestureListener() {

                 @Override
                 public boolean onDown(MotionEvent e) {
                    return true;
                 }

                 @Override
                 public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
                                        float velocityY) {
                    Log.d("OnFling", "onFling has been called!");
                    final int SWIPE_MIN_DISTANCE = 50;
                    try {

                       float diffY = e2.getY() - e1.getY();
                       float diffX = e2.getX() - e1.getX();
                       if (Math.abs(diffX) > Math.abs(diffY)) {
                          if (e1.getX() - e2.getX() > SWIPE_MIN_DISTANCE ) {
                             Log.d("Swipe", "Right to Left");
                             btnLeft();
                          } else if (e2.getX() - e1.getX() > SWIPE_MIN_DISTANCE ) {
                             Log.d("Swipe", "Left to Right");
                             btnRight();
                          }
                       } else {
                          if (e1.getY() - e2.getY() > SWIPE_MIN_DISTANCE) {
                             Log.d("Swipe", "Down to Up");
                             btnUp();
                          } else if (e2.getY() - e1.getY() > SWIPE_MIN_DISTANCE) {
                             Log.d("Swipe", "Up to Down");
                             btnDown();
                          }
                       }
                    } catch (Exception e) {
                    }
                    return super.onFling(e1, e2, velocityX, velocityY);
                 }
              });

      view.setOnTouchListener(new View.OnTouchListener() {
         @Override
         public boolean onTouch(View v, MotionEvent event) {
            return gesture.onTouchEvent(event);
         }
      });

      return view;
   }



   public void saveState(SharedPreferences sharedPreferences) {
      SharedPreferences.Editor editor = sharedPreferences.edit();

      String gridString = "";
      int[][] gridArray = boardModel.getMatrix();

      for (int i = 0; i < 4; i ++) {
         for (int j = 0; j < 4; j ++) {
            gridString += Integer.toString(gridArray[i][j]);
            if (i != 3 || j != 3) {
               gridString += ", ";
            }
         }
      }

      editor.clear();
      editor.putString(GRID, gridString);
      editor.putInt(SCORE, boardModel.getScore());
      editor.putInt(HI_SCORE, boardModel.getHighScore());
      editor.apply();

   }

   public void resumeState(SharedPreferences sharedPreferences) {
      String gridString = sharedPreferences.getString(GRID, "0");
      boardModel.setScore(sharedPreferences.getInt(SCORE, 0));
      boardModel.setHighScore(sharedPreferences.getInt(HI_SCORE, 0));

      int[][] gridArray = new int[4][4];

      boolean reset = true;
      if (gridString != "0") {
         List<String> vals = Arrays.asList(gridString.split("\\s*,\\s*"));
         int index = 0;

         for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
               if (Integer.parseInt(vals.get(index)) != 0) reset = false;
               gridArray[i][j] = Integer.parseInt(vals.get(index));
               index ++;
            }
         }
         boardModel.setMatrix(gridArray);
         boardView.setGrid(boardModel.getMatrix());
      }
      if (reset) {
         boardModel = new BoardModel();
         boardView.setGrid(boardModel.getMatrix());
      }

   }

   public int getBoardScore() {
      return boardModel.getScore();
   }

   public int getBoardHiScore() {
      return boardModel.getHighScore();
   }

   public void setEventInterface(EventInterface eventInterface) {
      this.eventInterface = eventInterface;
   }

   public void btnUp() {
      if (boardView.isAnimating()) return;
      boardModel.shiftUp();
      boardView.setNextGrid(boardModel.getMatrix());
      boardView.startAnimating(0);
      if (boardModel.gameOver()) {
         boardView.setGameOver(true);
      }
      this.eventInterface.showScore();
   }

   public void btnDown() {
      if (boardView.isAnimating()) return;
      boardModel.shiftDown();
      boardView.setNextGrid(boardModel.getMatrix());
      boardView.startAnimating(2);
      if (boardModel.gameOver()) {
         boardView.setGameOver(true);
      }
      this.eventInterface.showScore();
   }

   public void btnLeft() {
      if (boardView.isAnimating()) return;
      Log.d("Test 0, 0", Integer.toString(boardView.getGrid()[0][0]));
      boardModel.shiftLeft();
      Log.d("Test 0, 0", Integer.toString(boardView.getGrid()[0][0]));
      boardView.setNextGrid(boardModel.getMatrix());
      boardView.startAnimating(1);
      if (boardModel.gameOver()) {
         boardView.setGameOver(true);
      }
      this.eventInterface.showScore();
   }

   public void btnRight() {
      if (boardView.isAnimating()) return;
      boardModel.shiftRight();
      boardView.setNextGrid(boardModel.getMatrix());
      boardView.startAnimating(3);
      if (boardModel.gameOver()) {
         boardView.setGameOver(true);
      }
      this.eventInterface.showScore();
   }

   public void reset() {
      if (boardView.isAnimating()) return;
      boardModel = new BoardModel();
      boardView.setGrid(boardModel.getMatrix());
      boardView.setGameOver(false);
   }


}
