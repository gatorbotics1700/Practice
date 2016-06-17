package com.example.katiemishra.myfirstapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class DisplayMessageActivity extends AppCompatActivity {

    Intent intent = getIntent();
    String message = intent.getStringExtra(MyActivity.EXTRA_MESSAGE);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_message);

        TextView textView = new TextView(this);
        textView.setTextSize(40);
        textView.setText(message);

        RelativeLayout layout = (RelativeLayout) findViewById(R.id.content);
        layout.addView(textView);
    }
}

