package com.example.recipeapp;

import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import com.example.recipeapp.database.Ingredient;
import com.example.recipeapp.database.Recipe;
import com.example.recipeapp.database.Steps;

import java.util.ArrayList;

import butterknife.ButterKnife;

public class RecipeActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe);
        if (savedInstanceState == null) {
            ButterKnife.bind(this);
            Recipe recipe = getIntent().getParcelableExtra("recipe");
            FragmentManager fragmentManager = getSupportFragmentManager();
            if (recipe != null) {
                setTitle(recipe.getRname());
                if (recipe.getIngredientList().size() > 0) {
                    IngredientFragment ingredientFragment = IngredientFragment.newInstance((ArrayList<Ingredient>) recipe.getIngredientList());
                    fragmentManager.beginTransaction().add(R.id.ingredient_container, ingredientFragment)
                            .commit();
                    String image = recipe.getImage();
                    StepsFragment stepsFragment = StepsFragment.newInstance(image, (ArrayList<Steps>) recipe.getSteps());
                    fragmentManager.beginTransaction().add(R.id.steps_container, stepsFragment)
                            .commit();
                } else {
                    Toast.makeText(this, "Recipe not available", Toast.LENGTH_LONG).show();
                }
            }
        }
    }
}

