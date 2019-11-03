package com.example.recipeapp.utils;

import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.VisibleForTesting;
import androidx.test.espresso.IdlingResource;

import com.example.recipeapp.IdlingResource.SimpleIdlingResource;
import com.example.recipeapp.database.Ingredient;
import com.example.recipeapp.database.Recipe;
import com.example.recipeapp.database.Steps;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;

public class QueryUtils {
    private static final String LOG = QueryUtils.class.getSimpleName();
    private static final String RID = "id";
    private static final String RECIPENAME = "name";
    private static final String IMAGE = "image";
    private static final String RINGREDIENTS = "ingredients";
    private static final String INGREDIENT = "ingredient";
    private static final String QTY = "quantity";
    private static final String MSR = "measure";
    private static final String STEPS = "steps";
    private static final String SERVINGS = "servings";
    private static final String SHORTDESC = "shortDescription";
    private static final String DESCRIPTION = "description";
    private static final String VIDEOURL = "videoURL";

    // The Idling Resource which will be null in production.
    @Nullable
    private static SimpleIdlingResource mIdlingResource;

    public static ArrayList<Recipe> fetchRecipeData(String requestUrl) {
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        URL url = createUrl(requestUrl);
        String jsonResponse = "";
        try {
            jsonResponse = makeHttpRequest(url);
        } catch (IOException e) {
            Log.e(LOG, "Error closing InputStream", e);
        }
        return extractDataFromJson(jsonResponse);
    }

    //Creating URL.
    private static URL createUrl(String stringUrl) {
        URL url = null;
        try {
            url = new URL(stringUrl);
        } catch (MalformedURLException e) {
            Log.e(LOG, "Error with creating Url.", e);
        }
        return url;
    }

    private static String makeHttpRequest(URL url) throws IOException {
        String jsonResponse = "";
        if (url == null) {
            return jsonResponse;
        }
        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;
        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setReadTimeout(10000);
            urlConnection.setConnectTimeout(15000);
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();
            if (urlConnection.getResponseCode() == 200) {
                inputStream = urlConnection.getInputStream();
                jsonResponse = readFromStream(inputStream);
            } else {
                Log.e(LOG, "Error response Code : " + urlConnection.getResponseCode());
            }
        } catch (IOException e) {
            Log.e(LOG, "Problem in retrieving data ", e);
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (inputStream != null) {
                inputStream.close();
            }
        }
        return jsonResponse;
    }

    private static String readFromStream(InputStream inputStream) throws IOException {
        StringBuilder output = new StringBuilder();
        if (inputStream != null) {
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
            BufferedReader reader = new BufferedReader(inputStreamReader);
            String line = reader.readLine();
            while (line != null) {
                output.append(line);
                line = reader.readLine();
            }
        }
        return output.toString();
    }

    private static ArrayList<Recipe> extractDataFromJson(String RecipeJson) {
        //If RecipeJson is null and empty ,return null.
        if (TextUtils.isEmpty(RecipeJson)) {
            return null;
        }
        ArrayList<Recipe> RecipeList = new ArrayList<>();
        try {
            JSONArray root = new JSONArray(RecipeJson);
            for (int i = 0; i < root.length(); i++) {
                JSONObject currentRecipe = root.getJSONObject(i);
                int recipeId = currentRecipe.getInt(RID);
                String recipeTitle = currentRecipe.getString(RECIPENAME);
                String recipeimage = currentRecipe.getString(IMAGE);
                int servings = Integer.parseInt(currentRecipe.getString(SERVINGS));
                JSONArray ingredients = currentRecipe.getJSONArray(RINGREDIENTS);
                ArrayList<Ingredient> ingredientArrayList = new ArrayList<>();
                ArrayList<Steps> stepsArrayList = new ArrayList<>();
                for (int j = 0; j < ingredients.length(); j++) {
                    JSONObject currentIng = ingredients.getJSONObject(j);
                    Float qty = Float.parseFloat(currentIng.getString(QTY));
                    String measure = currentIng.getString(MSR);
                    String ingredient = currentIng.getString(INGREDIENT);
                    String formatedIngr = ingredient.substring(0, 1).toUpperCase() + ingredient.substring(1);
                    ingredientArrayList.add(new Ingredient(qty, measure, formatedIngr));
                }
                JSONArray steps = currentRecipe.getJSONArray(STEPS);
                for (int k = 0; k < steps.length(); k++) {
                    JSONObject currentSteps = steps.getJSONObject(k);
                    String sDesc = currentSteps.getString(SHORTDESC);
                    String description = currentSteps.getString(DESCRIPTION);
                    String video = currentSteps.getString(VIDEOURL);
                    stepsArrayList.add(new Steps(sDesc, description, video));
                }
                RecipeList.add(new Recipe(recipeId, recipeTitle, recipeimage, servings, ingredientArrayList, stepsArrayList));
            }
        } catch (JSONException e) {
            Log.e(LOG, "Problem in parsing JSON response results ", e);
        }
        return RecipeList;
    }

    /**
     * Only called from test, creates and returns a new {@link SimpleIdlingResource}.
     */
    @VisibleForTesting
    @NonNull
    public static IdlingResource getIdlingResource() {
        if (mIdlingResource == null) {
            mIdlingResource = new SimpleIdlingResource();
        }
        return mIdlingResource;
    }

    public static void setIdlingResource(boolean isIdleNow) {
        if (mIdlingResource == null) {
            mIdlingResource = new SimpleIdlingResource();
        }
        mIdlingResource.setIdleState(isIdleNow);
    }
}

