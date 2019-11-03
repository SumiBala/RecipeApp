package com.example.recipeapp;

/*
 * Copyright (C) 2017 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  	http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import android.app.IntentService;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;

import com.example.recipeapp.database.RecipeContract;

import static com.example.recipeapp.database.RecipeProvider.BASE_CONTENT_URI;
import static com.example.recipeapp.database.RecipeProvider.PATH_RECIPE;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 */
public class RecipeService extends IntentService {
    public static final String ACTION_RECIPE = "com.example.recipeapp.action.ingredient";
    public static final String ACTION_UPDATE_RECIPE_WIDGETS = "com.example.recipeapp.action.update_recipe_widgets";
    public static final String EXTRA_RECIPE_ID = "com.example.android.recipeapp.extra.RECIPE_ID";

    public RecipeService() {
        super("RecipeService");
    }

    /**
     * Starts this service with the given parameters. If
     * the service is already performing a task this action will be queued.
     *
     * @see IntentService
     */
    public static void startAction(Context context, int recipeId) {
        Intent intent = new Intent(context, RecipeService.class);
        intent.setAction(ACTION_RECIPE);
        intent.putExtra(EXTRA_RECIPE_ID, recipeId);
        context.startService(intent);
    }

    public static void startActionUpdateRecipeWidgets(Context context) {
        Intent intent = new Intent(context, RecipeService.class);
        intent.setAction(ACTION_UPDATE_RECIPE_WIDGETS);
        context.startService(intent);
    }

    /**
     * @param intent
     */
    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            if (ACTION_RECIPE.equals(action)) {
                final int recipeId = intent.getIntExtra(EXTRA_RECIPE_ID,
                        RecipeContract.INVALID_RECIPEID);
                handleActionWidgets(recipeId);
            }
        }
    }

    private void handleActionWidgets(int recipeId) {
        Uri RECIPE_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_RECIPE).build();
        String[] projection = {RecipeContract.COLUMN_RECIPE, RecipeContract.COLUMN_INGREDIENT};
        String selection = RecipeContract.COLUMN_ID + "=" + recipeId;
        Cursor cursor = getContentResolver().query(
                RECIPE_URI,
                projection,
                selection,
                null,
                null
        );
        String rname = "";
        String ingr = "";

        if (cursor != null && cursor.getCount() > 0) {
            cursor.moveToFirst();
            int rnameIndex = cursor.getColumnIndex(RecipeContract.COLUMN_RECIPE);
            int ingrIndex = cursor.getColumnIndex(RecipeContract.COLUMN_INGREDIENT);
            rname = cursor.getString(rnameIndex);
            ingr = cursor.getString(ingrIndex);
            cursor.close();
        }
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this);
        int[] appWidgetIds = appWidgetManager.getAppWidgetIds(new ComponentName(this, RecipeAppWidget.class));
        //Now update all widgets
        RecipeAppWidget.updateRecipeWidgets(this, appWidgetManager, rname, ingr, appWidgetIds);
    }
}
