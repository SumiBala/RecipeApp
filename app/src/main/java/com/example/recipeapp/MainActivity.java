package com.example.recipeapp;

import android.content.ContentValues;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.Loader;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.recipeapp.database.Ingredient;
import com.example.recipeapp.database.Recipe;
import com.example.recipeapp.database.RecipeContract;
import com.example.recipeapp.database.RecipeProvider;
import com.example.recipeapp.utils.API_Confiq;
import com.example.recipeapp.utils.QueryUtils;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;


public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<List<Recipe>>, RecipeAdapter.RecipeAdapterOnClickHandler {

    private RecipeAdapter mAdapter;
    @BindView(R.id.recipeRv)
    RecyclerView recipeList;
    @BindView(R.id.emptyTextView)
    TextView emptyTxtVw;
    @BindView(R.id.loadingIndicator)
    ProgressBar loadingBar;
    API_Confiq config;
    String RPATH;
    public static final String RECIPEID = "recipeid";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        config = new API_Confiq(this);
        RPATH = config.getRecipeUrl();
        //For Testing
        QueryUtils.setIdlingResource(false);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        mAdapter = new RecipeAdapter(this, this);
        recipeList.setLayoutManager(linearLayoutManager);
        recipeList.setHasFixedSize(true);
        recipeList.setAdapter(mAdapter);
        loadRecipesFromNet();
    }

    @NonNull
    @Override
    public Loader<List<Recipe>> onCreateLoader(int id, @Nullable Bundle args) {
        return new RecipeLoader(this, RPATH);
    }

    @Override
    public void onLoadFinished(@NonNull Loader<List<Recipe>> loader, List<Recipe> data) {
        if (data != null && data.size() != 0) {
            mAdapter.loadRecipes(data);
            //Load into dbase for Widget
            insertRecipe(data);
            mAdapter.notifyDataSetChanged();
            //For Testing
            QueryUtils.setIdlingResource(true);
            showControls();
        } else {
            showError(getString(R.string.no_recipe));
        }
    }

    @Override
    public void onLoaderReset(@NonNull Loader<List<Recipe>> loader) {
    }

    private void insertRecipe(List<Recipe> recipeList) {
        AsyncTask<Void, Void, Void> insertTask = new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {


                for (Recipe temp : recipeList) {
                    int id = temp.getRid();
                    String rname = temp.getRname();
                    StringBuilder ingredients = new StringBuilder();
                    for (Ingredient tempIngr : temp.getIngredientList()) {
                        ingredients.append(tempIngr.getIngredient()).append(" ").append(tempIngr.getQty()).append(" ").append(tempIngr.getMeasurement()).append("\n");
                    }
                    ContentValues newRecipe = new ContentValues();
                    newRecipe.put(RecipeContract.COLUMN_ID, id);
                    newRecipe.put(RecipeContract.COLUMN_RECIPE, rname);
                    newRecipe.put(RecipeContract.COLUMN_INGREDIENT, ingredients.toString());
                    Uri uri = getContentResolver().insert(RecipeProvider.RecipeIngredients.FULL_CONTENT_URI, newRecipe);
                }

                return null;
            }
        };
        insertTask.execute();
    }

    private void loadRecipesFromNet() {
        if (config.netIsConnected()) {
            LoaderManager loaderManager = getSupportLoaderManager();
            loaderManager.initLoader(121, null, this);
        } else {
            showError(getString(R.string.no_internet_connection));
        }
    }

    public void showError(String msg) {
        recipeList.setVisibility(View.GONE);
        emptyTxtVw.setVisibility(View.VISIBLE);
        loadingBar.setVisibility(View.GONE);
        emptyTxtVw.setText(msg);
    }

    private void showControls() {
        loadingBar.setVisibility(View.GONE);
        recipeList.setVisibility(View.VISIBLE);
        emptyTxtVw.setVisibility(View.INVISIBLE);
    }

    @Override
    public void onClick(Recipe recipe) {
        Intent intent = new Intent(this, RecipeActivity.class);
        Bundle bundle = new Bundle();
        intent.putExtra("recipe", recipe);
        RecipeService.startAction(this, recipe.getRid());
        startActivity(intent);
    }
}
