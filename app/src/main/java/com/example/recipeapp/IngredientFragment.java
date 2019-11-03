package com.example.recipeapp;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.recipeapp.database.Ingredient;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class IngredientFragment extends Fragment {
    private static final String INGREDIENTLIST = "ingredients";
    @BindView(R.id.recipeRv)
    RecyclerView rListView;
    @BindView(R.id.emptyTextView)
    TextView emptyTxtVw;
    @BindView(R.id.loadingIndicator)
    ProgressBar loadingBar;
    private IngredientAdapter mAdapter;
    private List<Ingredient> mIngredientList;

    public IngredientFragment() {
    }

    public static IngredientFragment newInstance(ArrayList<Ingredient> param1) {
        IngredientFragment fragment = new IngredientFragment();
        Bundle args = new Bundle();
        args.putParcelableArrayList(INGREDIENTLIST, param1);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mIngredientList = getArguments().getParcelableArrayList(INGREDIENTLIST);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_ingredient, container, false);
        ButterKnife.bind(this, rootView);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        mAdapter = new IngredientAdapter(getContext());
        rListView.setLayoutManager(linearLayoutManager);
        rListView.setHasFixedSize(true);
        rListView.setHorizontalScrollBarEnabled(true);
        rListView.setAdapter(mAdapter);
        if (mIngredientList != null && mIngredientList.size() > 0) {
            mAdapter.loadIngredient(mIngredientList);
            loadingBar.setVisibility(View.GONE);
            mAdapter.notifyDataSetChanged();
        } else {
            showError(getString(R.string.no_ingredients));
        }
        return rootView;
    }

    public void showError(String msg) {
        rListView.setVisibility(View.GONE);
        emptyTxtVw.setVisibility(View.VISIBLE);
        loadingBar.setVisibility(View.GONE);
        emptyTxtVw.setText(msg);
    }
}
