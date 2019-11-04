package com.example.recipeapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.recipeapp.database.Ingredient;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class IngredientAdapter extends RecyclerView.Adapter<IngredientAdapter.IngredientViewHolder> {

    private Context mContext;
    private List<Ingredient> mIngredient = new ArrayList<>();

    public IngredientAdapter(Context context) {
        mContext = context;
    }

    @NonNull
    @Override
    public IngredientAdapter.IngredientViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.ing_controls, parent, false);
        return new IngredientAdapter.IngredientViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull IngredientAdapter.IngredientViewHolder holder, int position) {
        holder.BindView(position);
    }

    @Override
    public int getItemCount() {
        return (mIngredient != null) ? mIngredient.size(): 0;
    }

    public void loadIngredient(List<Ingredient> ingredients) {
        mIngredient = ingredients;
    }

    public class IngredientViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.ingredientTv)
        TextView ingrTxt;
        @BindView(R.id.qtyTv)
        TextView qtyTxt;
        @BindView(R.id.msrTv)
        TextView msrText;

        public IngredientViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        public void BindView(final int position) {
            Ingredient tempIngredient = mIngredient.get(position);
            ingrTxt.setText(tempIngredient.getIngredient());
            qtyTxt.setText(String.valueOf(tempIngredient.getQty()));
            msrText.setText(tempIngredient.getMeasurement());
        }
    }
}
