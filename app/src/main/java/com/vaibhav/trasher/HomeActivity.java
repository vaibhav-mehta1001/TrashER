package com.vaibhav.trasher;

import android.os.Bundle;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.GestureDetector;
import android.view.View;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.support.v4.view.GestureDetectorCompat;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.widget.TextView;
import android.widget.Toast;

import com.vaibhav.trasher.R;

public class HomeActivity extends AppCompatActivity {
    private GestureDetectorCompat gestureDetectorCompat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        gestureDetectorCompat = new GestureDetectorCompat(this, new MyGestureListener());

        Button btnRestaurant = (Button) findViewById(R.id.button2);
        btnRestaurant.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeActivity.this, Disposal.class);
                startActivity(intent);
             //   Toast.makeText(MapsActivity.this,"Nearby Recyclers", Toast.LENGTH_LONG).show();
            }
        });

        Button btnRest = (Button) findViewById(R.id.button3);
        btnRest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeActivity.this, About.class);
                startActivity(intent);
                //   Toast.makeText(MapsActivity.this,"Nearby Recyclers", Toast.LENGTH_LONG).show();
            }
        });


    }
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        this.gestureDetectorCompat.onTouchEvent(event);
        return super.onTouchEvent(event);
    }



class MyGestureListener extends GestureDetector.SimpleOnGestureListener {
    //handle 'swipe left' action only

    @Override
    public boolean onFling(MotionEvent event1, MotionEvent event2,
                           float velocityX, float velocityY) {

         /*
         Toast.makeText(getBaseContext(),
          event1.toString() + "\n\n" +event2.toString(),
          Toast.LENGTH_SHORT).show();
         */

        if (event2.getX() < event1.getX()) {
            Toast.makeText(getApplicationContext(),
                    "Swipe left - startActivity()",
                    Toast.LENGTH_SHORT).show();

            //switch another activity
            Intent intent = new Intent(HomeActivity.this, MainActivity.class);
            startActivity(intent);
        }
        if (event2.getX() > event1.getX()) {
            Toast.makeText(getApplicationContext(),
                    "Swipe Right - startActivity()",
                    Toast.LENGTH_SHORT).show();

            //switch another activity
          Intent intent = new Intent(HomeActivity.this, MapsActivity.class);
          startActivity(intent);
        }

        return true;
    }
}
}

