package com.example.recipeapp;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // This LinearLayout will only initially exist in the two-pane tablet case
        boolean mTwoPane = findViewById(R.id.tabMode) != null;
    }
}
