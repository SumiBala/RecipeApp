package com.example.recipeapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.recipeapp.database.Recipe;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class RecipeAdapter extends RecyclerView.Adapter<RecipeAdapter.RecipeViewHolder> {

    private Context mContext;
    List<Recipe> mRecipe = new ArrayList<>();
    private final RecipeAdapterOnClickHandler mClickHandler;

    public interface RecipeAdapterOnClickHandler {
        void onClick(Recipe recipe);
    }

    public RecipeAdapter(Context context, RecipeAdapterOnClickHandler handler) {
        mContext = context;
        mClickHandler = handler;
    }

    @NonNull
    @Override
    public RecipeAdapter.RecipeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.card_layout, parent, false);
        return new RecipeAdapter.RecipeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecipeAdapter.RecipeViewHolder holder, int position) {
        holder.BindView(position);
    }

    @Override
    public int getItemCount() {
        return (mRecipe != null) ? mRecipe.size(): 0;
    }

    public void loadRecipes(List<Recipe> Recipes) {
        mRecipe = Recipes;
    }

    public class RecipeViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        @BindView(R.id.listItem)
        TextView contentTxt;
        @BindView(R.id.servings)
        TextView servingTxt;

        public RecipeViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(this);
        }

        public void BindView(final int position) {
            Recipe recipe = mRecipe.get(position);
            contentTxt.setText(recipe.getRname());
            servingTxt.setText(new StringBuilder().append(mContext.getString(R.string.servings)).append(recipe.getServings()).toString());
        }

        @Override
        public void onClick(View v) {
            int adapterPosition = getAdapterPosition();
            Recipe loadRecipe = mRecipe.get(adapterPosition);
            mClickHandler.onClick(loadRecipe);
        }
    }
}
