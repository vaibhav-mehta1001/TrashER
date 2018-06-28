package com.vaibhav.trasher;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.widget.TextView;

public class Disposal extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_disposal);
        TextView tv = (TextView) findViewById(R.id.textView9);
        tv.setMovementMethod(new ScrollingMovementMethod());
        TextView tv1 = (TextView) findViewById(R.id.textView12);
        tv1.setMovementMethod(new ScrollingMovementMethod());

    }
}
