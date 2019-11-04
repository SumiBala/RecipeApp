package com.example.recipeapp;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.recipeapp.database.Recipe;

public class RecipeActivity extends AppCompatActivity {
    private boolean mTwoPane;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe);
        if (findViewById(R.id.recipeTabMode) != null) {
            //Run on Tablet.
            mTwoPane = true;
        } else {
            // We're in single-pane mode and displaying fragments on a phone in separate activities
            mTwoPane = false;
        }
        if (savedInstanceState == null) {
            Recipe recipe = getIntent().getParcelableExtra("recipe");
            RecipeFragment recipeFragment = RecipeFragment.newInstance(recipe,mTwoPane);
            getSupportFragmentManager().beginTransaction().add(R.id.rdetail_list_container, recipeFragment)
                    .commit();
        }
    }
}

