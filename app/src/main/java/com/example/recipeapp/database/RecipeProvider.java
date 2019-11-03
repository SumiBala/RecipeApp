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
package com.example.recipeapp.database;

import android.net.Uri;

import net.simonvt.schematic.annotation.ContentProvider;
import net.simonvt.schematic.annotation.ContentUri;
import net.simonvt.schematic.annotation.TableEndpoint;

import static com.example.recipeapp.database.RecipeDatabase.RECIPE_TABLE;


/**
 * Uses the Schematic (https://github.com/SimonVT/schematic) to create a content provider and
 * define
 * URIs for the provider
 */

@ContentProvider(
        authority = RecipeProvider.AUTHORITY,
        database = RecipeDatabase.class)
public final class RecipeProvider {
    // The authority, which is how your code knows which Content Provider to access
    public static final String AUTHORITY = "com.example.recipeapp.database.provider";
    public static final String CONTENT = "content://";
    public static final String PATH_RECIPE = "recipes";
    // The base content URI = "content://" + <authority>
    public static final Uri BASE_CONTENT_URI = Uri.parse(CONTENT + AUTHORITY);

    // Define the possible paths for accessing data in this contract
    // This is the path for the "recipes" directory
    @TableEndpoint(table = RECIPE_TABLE)
    public static class RecipeIngredients {
        @ContentUri(
                path = "recipes",
                type = "vnd.android.cursor.dir/recipes"
        )
        public static final Uri FULL_CONTENT_URI = Uri.parse(CONTENT + AUTHORITY + "/" + PATH_RECIPE);
    }
}