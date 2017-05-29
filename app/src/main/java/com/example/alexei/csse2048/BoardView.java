package com.example.alexei.csse2048;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;

import java.util.Arrays;

/**
 * Created by alexei on 4/16/17.
 */
public class BoardView extends SurfaceView {

   private Bitmap bitmap;
   private Canvas canvas;
   private Paint paint;
   private Paint paintNumber;
   private boolean created;

   private LoopThread gameThread;
   private SurfaceHolder holder;

   private boolean animating;
   private int frame;
   private int[][] grid;
   private int[][] nextGrid;
   private int[][] animationCodes;
   private float [][] offset;
   private boolean gameOver;
   private int direction;

   public BoardView(final Context context, final AttributeSet attrs) {
      super(context, attrs); // pass context to View's constructor
      created = false;
      frame = 0;

      holder = getHolder();
      holder.addCallback(new SurfaceHolder.Callback() {

         @Override
         public void surfaceDestroyed(SurfaceHolder holder) {
            boolean retry = true;
            created = false;
            gameThread.setRunning(false);
            while (retry) {
               try {
                  gameThread.join();
                  retry = false;
               } catch (InterruptedException e) {
               }
            }
         }

         @Override
         public void surfaceCreated(SurfaceHolder holder) {
            viewCreated();
            gameThread = new LoopThread(holder);
            gameThread.start();
            gameThread.setRunning(true);
         }
         @Override
         public void surfaceChanged(SurfaceHolder holder, int format,
                                    int width, int height) {
         }
      });
   }

   // Thread subclass to control the game loop
   private class LoopThread extends Thread {
      private SurfaceHolder surfaceHolder; // for manipulating canvas
      private boolean threadIsRunning = true; // running by default

      // initializes the surface holder
      public LoopThread(SurfaceHolder holder) {
         surfaceHolder = holder;
         setName("GameThread");
      }

      // changes running state
      public void setRunning(boolean running) {
         threadIsRunning = running;
      }

      // controls the game loop
      @Override
      public void run() {
         Canvas canvas = null; // used for drawing

         while (threadIsRunning) {
            try {
               // get Canvas for exclusive drawing from this thread
               canvas = surfaceHolder.lockCanvas(null);

               // lock the surfaceHolder for drawing
               synchronized(surfaceHolder) {
                  drawCanvas(canvas);
               }
            }
            finally {
               // display canvas's contents on the CannonView
               // and enable other threads to use the Canvas
               if (canvas != null)
                  surfaceHolder.unlockCanvasAndPost(canvas);
            }
         }
      }
   }


   public void viewCreated() {

      bitmap = Bitmap.createBitmap(getWidth(), getHeight(),
              Bitmap.Config.ARGB_8888);
      canvas = new Canvas(bitmap);
      bitmap.eraseColor(Color.WHITE);
      paint = new Paint();
      paintNumber = new Paint();
      paintNumber.setTextSize(100);
      paintNumber.setColor(Color.WHITE);
      gameOver = false;
      created = true;

  }

   public void setGameOver(boolean gameOver) {
      this.gameOver = gameOver;
   }

   public void setAnimating(boolean animating) {
      this.animating = animating;
   }

   public boolean isAnimating() {
      return animating;
   }

   public void setGrid(int[][] grid) {
      //this.grid = grid;
      this.grid = new int[4][];
      for (int i = 0; i < 4; i ++) {
         this.grid[i] = grid[i].clone();
      }
      //this.grid = Arrays.copyOf(grid, 4);
   }

   public int[][] getGrid() {
      return grid;
   }

   public void setNextGrid(int[][] nextGrid) {
      //this.nextGrid = Arrays.copyOf(nextGrid, 4);
      this.nextGrid = new int[4][];
      for (int i = 0; i < 4; i ++) {
         this.nextGrid[i] = nextGrid[i].clone();
      }
   }

   protected void setColor(Paint paint, int cellNumber) {
      switch (cellNumber) {
         case 0:
            paint.setColor(Color.GRAY);
            break;
         case 2:
            paint.setColor(Color.BLUE);
            break;
         case 4:
            paint.setColor(Color.RED);
            break;
         case 8:
            paint.setColor(Color.YELLOW);
            break;
         case 16:
            paint.setColor(Color.GREEN);
            break;
         case 32:
            paint.setColor(Color.CYAN);
            break;
         case 64:
            paint.setColor(Color.MAGENTA);
            break;
         case 128:
            paint.setARGB(255, 128, 0, 128);
            break;
         case 256:
            paint.setARGB(255, 255, 182, 193);
            break;
         case 512:
            paint.setARGB(255, 255, 165, 0);
            break;
         case 1024:
            paint.setARGB(255, 255, 215, 0);
            break;
         default:
            paint.setColor(Color.BLACK);
      }
   }


   protected void drawBoard(int[][] grid) {
      int margin = 10;
      int corner = 15;

      paint.setColor(Color.GRAY);
      RectF rect = new RectF(0, 0, getWidth(), getHeight());
      canvas.drawRoundRect(rect, 20, 20, paint);


      int squareW = getWidth() / 4;
      int squareH = getHeight() / 4;
      for (int i = 0; i < 4; i++) {
         for (int j = 0; j < 4; j++) {
            if (grid[i][j] == 0) continue;
            rect = new RectF(squareW * i + margin, squareH * j + margin,
                    squareW * (i + 1) - margin, squareH * (j + 1) - margin);
            setColor(paint, grid[i][j]);
            canvas.drawRoundRect(rect, corner, corner, paint);
            if (grid[i][j] == 0) continue;
            canvas.drawText(Integer.toString(grid[i][j]), rect.centerX(), rect.centerY(), paintNumber);
         }
      }
   }

   protected void drawBoardOffsetX(int[][] grid) {
      int margin = 10;
      int corner = 15;

      //Log.d("anim frame", Integer.toString(grid[0][0]));

      paint.setColor(Color.GRAY);
      RectF rect = new RectF(0, 0, getWidth(), getHeight());
      canvas.drawRoundRect(rect, 20, 20, paint);


      int squareW = getWidth() / 4;
      int squareH = getHeight() / 4;
      for (int i = 0; i < 4; i++) {
         for (int j = 0; j < 4; j++) {
            if (grid[i][j] == 0) continue;
            rect = new RectF(squareW * (i + offset[i][j]) + margin, squareH * j + margin,
                    squareW * ((i + offset[i][j]) + 1) - margin, squareH * (j + 1) - margin);
            setColor(paint, grid[i][j]);
            canvas.drawRoundRect(rect, corner, corner, paint);
            if (grid[i][j] == 0) continue;
            canvas.drawText(Integer.toString(grid[i][j]), rect.centerX(), rect.centerY(), paintNumber);
         }
      }
   }

   protected void drawBoardOffsetY(int[][] grid) {
      int margin = 10;
      int corner = 15;

      //Log.d("anim frame", Integer.toString(grid[0][0]));

      paint.setColor(Color.GRAY);
      RectF rect = new RectF(0, 0, getWidth(), getHeight());
      canvas.drawRoundRect(rect, 20, 20, paint);


      int squareW = getWidth() / 4;
      int squareH = getHeight() / 4;
      for (int i = 0; i < 4; i++) {
         for (int j = 0; j < 4; j++) {
            if (grid[i][j] == 0) continue;
            rect = new RectF(squareW * (i) + margin, squareH * (j + offset[i][j]) + margin,
                    squareW * ((i) + 1) - margin, squareH * ((j + offset[i][j]) + 1) - margin);
            setColor(paint, grid[i][j]);
            canvas.drawRoundRect(rect, corner, corner, paint);
            if (grid[i][j] == 0) continue;
            canvas.drawText(Integer.toString(grid[i][j]), rect.centerX(), rect.centerY(), paintNumber);
         }
      }
   }


   protected void drawGameOver(){
      paintNumber.setColor(Color.BLACK);
      paintNumber.setTextSize(200);
      canvas.drawText("GAME OVER",0 , getHeight()/2, paintNumber);
      paintNumber.setTextSize(150);
      canvas.drawText("PRESS RESET",0 ,3 * getHeight()/4, paintNumber);
      paintNumber.setColor(Color.WHITE);
      paintNumber.setTextSize(100);
   }

   public void startAnimating(int direction) {
      //set direction
      this.direction = direction;
      this.offset = new float[4][4];
      this.animationCodes = new int[4][4];

      updateAnimationCodes();

      this.animating = true;
   }

   private void updateAnimationCodes() {

      animationCodes = new int[4][4];
      if (direction == 0) {
         for (int i = 0; i < 4; i ++) {
            int lastNum = 0;
            for (int j = 0; j < 4; j ++) {
               if (grid[i][j] == 0) {
                  for (int k = j + 1; k < 4; k ++) {
                     if (grid[i][k] != 0) animationCodes[i][k] -= 1;
                  }
               } else {
                  if (grid[i][j] == lastNum) {
                     lastNum = 0;
                     for (int k = j; k < 4; k ++) {
                        if (grid[i][k] != 0) animationCodes[i][k] -= 1;
                     }
                  } else {
                     lastNum = grid[i][j];
                  }
               }
            }
         }
      } else if (direction == 2) {
         for (int i = 0; i < 4; i ++) {
            int lastNum = 0;
            for (int j = 3; j >= 0; j --) {
               if (grid[i][j] == 0) {
                  for (int k = j - 1; k >= 0; k --) {
                     if (grid[i][k] != 0) animationCodes[i][k] += 1;
                  }
               } else {
                  if (grid[i][j] == lastNum) {
                     lastNum = 0;
                     for (int k = j; k >= 0; k --) {
                        if (grid[i][k] != 0) animationCodes[i][k] += 1;
                     }
                  } else {
                     lastNum = grid[i][j];
                  }
               }
            }
         }
      } else if (direction == 1) {
         for (int i = 0; i < 4; i ++) {
            int lastNum = 0;
            for (int j = 0; j < 4; j ++) {
               if (grid[j][i] == 0) {
                  for (int k = j + 1; k < 4; k ++) {
                     if (grid[k][i] != 0) animationCodes[k][i] -= 1;
                  }
               } else {
                  if (grid[j][i] == lastNum) {
                     lastNum = 0;
                     for (int k = j; k < 4; k ++) {
                        if (grid[k][i] != 0) animationCodes[k][i] -= 1;
                     }
                  } else {
                     lastNum = grid[j][i];
                  }
               }
            }
         }
      } else if (direction == 3) {
         for (int i = 0; i < 4; i ++) {
            int lastNum = 0;
            for (int j = 3; j >= 0; j --) {
               if (grid[j][i] == 0) {
                  for (int k = j - 1; k >= 0; k --) {
                     if (grid[k][i] != 0) animationCodes[k][i] += 1;
                  }
               } else {
                  if (grid[j][i] == lastNum) {
                     lastNum = 0;
                     for (int k = j; k >= 0; k --) {
                        if (grid[k][i] != 0) animationCodes[k][i] += 1;
                     }
                  } else {
                     lastNum = grid[j][i];
                  }
               }
            }
         }
      }
   }

   private void updateOffset() {
      for (int i = 0; i < 4; i ++) {
         for (int j = 0; j < 4; j ++) {
            if (animationCodes[i][j] != 0) {

               offset[i][j] = animationCodes[i][j]*((float)frame/10);
            }
         }
      }
   }

   private void animate(Canvas canvas) {
      updateOffset();

      //check direction first
      if (direction == 0 || direction == 2) {
         drawBoardOffsetY(grid);
      } else {
         drawBoardOffsetX(grid);
      }

      frame ++;
      if (frame == 10) {
         frame = 0;
         this.grid = this.nextGrid;
         animating = false;
      }
   }

   @Override
   protected void onDraw(Canvas canvas) {
      super.onDraw(canvas);

   }

   private void drawCanvas(Canvas canvas) {
      if (created) {
         canvas.drawBitmap(bitmap, 1, 1, paint);
         if (animating) {
            animate(canvas);
         } else {
            drawBoard(grid);
            if (gameOver) {
               drawGameOver();
            }
         }
      }
   }

   @Override
   public void onSizeChanged(int w, int h, int oldW, int oldH) {
      bitmap = Bitmap.createBitmap(getWidth(), getHeight(),
              Bitmap.Config.ARGB_8888);
      canvas = new Canvas(bitmap);
      bitmap.eraseColor(Color.WHITE); // erase the Bitmap with white
   }
}
