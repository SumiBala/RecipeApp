package com.example.recipeapp;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.example.recipeapp.database.Ingredient;
import com.example.recipeapp.database.Recipe;
import com.example.recipeapp.database.Steps;

import java.util.ArrayList;

import butterknife.ButterKnife;

public class RecipeFragment extends Fragment {

    private static final String RECIPE = "selectedRecipe";
    private static final String RUNONTAB = "twoPane";
    private Recipe mRecipe;
    private boolean mTwoPane;

    public RecipeFragment() {
    }

    public static RecipeFragment newInstance(Recipe recipes, boolean mTwoPane) {
        RecipeFragment fragment = new RecipeFragment();
        Bundle args = new Bundle();
        args.putParcelable(RECIPE, recipes);
        args.putBoolean(RUNONTAB, mTwoPane);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mRecipe = getArguments().getParcelable(RECIPE);
            mTwoPane=getArguments().getBoolean(RUNONTAB);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_recipe, container, false);
        if (savedInstanceState == null) {
            ButterKnife.bind(this, view);
            Recipe recipe = mRecipe;
            FragmentManager fragmentManager = getFragmentManager();
            if (recipe != null) {
                getActivity().setTitle(recipe.getRname());
                if (recipe.getIngredientList().size() > 0) {
                    IngredientFragment ingredientFragment = IngredientFragment.newInstance((ArrayList<Ingredient>) recipe.getIngredientList());
                    fragmentManager.beginTransaction().add(R.id.ingredient_container, ingredientFragment)
                            .commit();
                    String image = recipe.getImage();
                    StepsFragment stepsFragment = StepsFragment.newInstance(image, (ArrayList<Steps>) recipe.getSteps(),mTwoPane);
                    fragmentManager.beginTransaction().add(R.id.steps_container, stepsFragment)
                            .commit();
                } else {
                    Toast.makeText(getContext(), "Recipe not available", Toast.LENGTH_LONG).show();
                }
            }
        }
        return view;
    }
}
