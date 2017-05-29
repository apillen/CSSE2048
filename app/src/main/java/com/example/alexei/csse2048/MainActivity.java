package com.example.alexei.csse2048;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity implements EventInterface {

   public static final String SAVE_STATE = "pref_save";



   @Override
   protected void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      setContentView(R.layout.activity_main);
   }

   @Override
   protected void onStop() {
      super.onStop();
      BoardController controller = (BoardController)
              getSupportFragmentManager().findFragmentById(
                      R.id.mainFragment);
      controller.saveState(getSharedPreferences(SAVE_STATE, Context.MODE_PRIVATE));
   }

   @Override
   protected void onStart() {
      super.onStart();
      BoardController controller = (BoardController) getSupportFragmentManager().findFragmentById(R.id.mainFragment);
      controller.setEventInterface(this);
      try {
         controller.resumeState(getSharedPreferences(SAVE_STATE, Context.MODE_PRIVATE));
         displayScore(controller.getBoardScore(), controller.getBoardHiScore());
      } catch (Exception e) {
         Log.d("CANNOT RESUME", e.toString());
      }
   }

   @Override
   public void showScore() {
      BoardController controller = (BoardController) getSupportFragmentManager().findFragmentById(R.id.mainFragment);
      displayScore(controller.getBoardScore(), controller.getBoardHiScore());
   }

   protected void displayScore(int score, int hiScore) {
      TextView text = (TextView) findViewById(R.id.score);
      text.setText("Score: " + score + "   Hi Score: " + hiScore);
   }

   public void aboutBtn(View view) {
      Intent intent = new Intent(this, AboutActivity.class);
      startActivity(intent);
   }

   public void buttonPressed(View view) {
      BoardController controller = (BoardController) getSupportFragmentManager().findFragmentById(R.id.mainFragment);
      switch (view.getId()) {
         case R.id.btn_up:
            controller.btnUp();
            break;
         case R.id.btn_down:
            controller.btnDown();
            break;
         case R.id.btn_left:
            controller.btnLeft();
            break;
         case R.id.btn_right:
            controller.btnRight();
            break;
         case R.id.btn_reset:
            controller.reset();
            break;
         default:
      }
      displayScore(controller.getBoardScore(), controller.getBoardHiScore());
   }

}
