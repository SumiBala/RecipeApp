package com.example.recipeapp;

import android.content.res.Configuration;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import com.example.recipeapp.database.Steps;

import java.util.ArrayList;
import java.util.List;

public class VideoActivity extends AppCompatActivity {
    List<Steps> mStepList;
    String imgSrc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video);
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            showFullScreen();
        }
        if (savedInstanceState == null) {
            mStepList = getIntent().getParcelableArrayListExtra("steps");
            imgSrc = getIntent().getStringExtra("imageSrc");
            int position = getIntent().getIntExtra("index", 0);
            FragmentManager fragmentManager = getSupportFragmentManager();
            if (mStepList != null) {
                VideoFragment videoFragment = VideoFragment.newInstance(imgSrc, (ArrayList<Steps>) mStepList, position);
                fragmentManager.beginTransaction().add(R.id.detailSteps_container, videoFragment).commit();
            }
        }
    }

    private void showFullScreen() {
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_IMMERSIVE
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                // Hide the nav bar and status bar
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN);
    }
}