package com.example.alexei.csse2048;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.Log;

import java.util.Arrays;
import java.util.Random;

/**
 * Created by alexei on 4/16/17.
 */

public class BoardModel {

   public static final int UP = 0;
   public static final int DOWN = 1;
   public static final int LEFT = 2;
   public static final int RIGHT = 3;

   private int[][] matrix;
   private static int score;
   private static int highScore;


   public BoardModel() {
      matrix = new int[4][4];
      for (int i = 0; i < 4; i++) {
         for (int j = 0; j < 4; j ++) {
            matrix[i][j] = 0;
         }
      }
      score = 0;
      addRandomBlock();
   }

   public int[][] getMatrix() {
      return matrix;
   }

   public void setMatrix(int[][] matrix) {
      this.matrix = matrix;
   }

   public int getScore() {
      return score;
   }

   public void setScore(int score) {
      BoardModel.score = score;
   }

   public int getHighScore() {
      return highScore;
   }

   public void setHighScore(int highScore) {
      BoardModel.highScore = highScore;
   }

   private void addRandomBlock() {
      Random rn = new Random();
      int nextNum = 2;
      if (rn.nextDouble() < 0.25) {
         nextNum = 4;
      }
      score += nextNum;
      if (score > highScore) {
         highScore = score;
      }
      int r, c;
      do {
         r = rn.nextInt(Integer.MAX_VALUE) % 4;
         c = rn.nextInt(Integer.MAX_VALUE) % 4;
      } while (matrix[r][c] != 0);
      matrix[r][c] = nextNum;
   }

   private boolean compare(int[][] grid1, int[][] grid2) {
      for (int i = 0; i < 4; i++) {
         for (int j = 0; j < 4; j ++) {
            if (grid1[i][j] != grid2[i][j]) return false;
         }
      }
      return true;
   }

   public boolean gameOver() {
      if (full()) {
         for (int i = 0; i < 4; i ++){
            for (int j = 0; j < 4; j ++) {
               if (i-1 >=0) {
                  if (matrix[i-1][j] == matrix[i][j]) return false;
               }
               if (i+1 < 4) {
                  if (matrix[i+1][j] == matrix[i][j]) return false;
               }
               if (j-1 >=0) {
                  if (matrix[i][j-1] == matrix[i][j]) return false;
               }
               if (j+1 < 4) {
                  if (matrix[i][j+1] == matrix[i][j]) return false;
               }
            }
         }
         return true;
      }
      return false;
   }

   public void shiftUp() {
      for (int i = 0; i < 4; i ++) {
         matrix[i] = collapse(matrix[i]);
      }
      if (!full()) {
         addRandomBlock();
      }
   }


   public void shiftDown() {
      for (int i = 0; i < 4; i ++) {
         matrix[i] = reverse(collapse(reverse(matrix[i])));
      }
      if (!full()) {
         addRandomBlock();
      }
   }

   public void shiftLeft() {
      for (int i = 0; i < 4; i ++) {
         setRow(i, collapse(getRow(i)));
      }
      if (!full()) {
         addRandomBlock();
      }
   }

   public void shiftRight() {
      for (int i = 0; i < 4; i ++) {
         setRow(i, reverse(collapse(reverse(getRow(i)))));
      }
      if (!full()) {
         addRandomBlock();
      }
   }

   private int[] reverse(int[] arr) {
      int[] out = new int[4];
      for (int i = 0; i < 4; i ++) {
         out[i] = arr[3 - i];
      }
      return out;
   }

   private int[] getRow(int r) {
      int[] out = new int[4];
      for (int i = 0; i < 4; i ++) {
         out[i] = matrix[i][r];
      }
      return out;
   }

   private void setRow(int r, int[] arr) {
      for (int i = 0; i < 4; i ++) {
         matrix[i][r] = arr[i];
      }
   }

   private int[] collapse(int[] c) {

      int[] out = new int[4];
      int j = 0;
      for (int i = 0; i < 4; i ++) {
         if (c[i] == 0) {
            continue;
         }
         out[j] = c[i];
         j ++;
      }
      for ( ; j < 4; j ++) {
         out[j] = 0;
      }

      for (int i = 0; i < 3; i ++) {
         if (out[i] == out[i + 1]) {
            out[i] *= 2;
            for (j = i + 1; j < 3; j++) {
               out[j] = out[j + 1];
            }
            out[3] = 0;
         }
      }
      return out;
   }

   private boolean full() {
      for (int i = 0; i < 4; i ++) {
         for (int j = 0; j < 4; j ++) {
            if (matrix[i][j] == 0) return false;
         }
      }
      return true;
   }

}
