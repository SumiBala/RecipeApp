package com.example.recipeapp;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.loader.content.AsyncTaskLoader;

import com.example.recipeapp.database.Recipe;
import com.example.recipeapp.utils.QueryUtils;

import java.util.List;

public class RecipeLoader extends AsyncTaskLoader<List<Recipe>> {
    private String mUrl;

    public RecipeLoader(@NonNull Context context, String url) {
        super(context);
        mUrl = url;
    }

    @Nullable
    @Override
    public List<Recipe> loadInBackground() {
        if (mUrl == null) {
            return null;
        }
        //Perform the network request, parse the response and extract data.
        return QueryUtils.fetchRecipeData(mUrl);
    }

    @Override
    protected void onStartLoading() {
        forceLoad();
    }
}
