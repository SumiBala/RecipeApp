package com.example.recipeapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.recipeapp.database.Steps;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class StepsAdapter extends RecyclerView.Adapter<StepsAdapter.StepsViewHolder> {

    private Context mContext;
    List<Steps> mSteps = new ArrayList<>();
    private final StepsAdapterOnClickHandler mClickHandler;

    public interface StepsAdapterOnClickHandler {
        void onClick(Steps steps, int position);
    }

    public StepsAdapter(Context context, StepsAdapterOnClickHandler handler) {
        mContext = context;
        mClickHandler = handler;
    }

    @NonNull
    @Override
    public StepsAdapter.StepsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.steps_control, parent, false);
        return new StepsAdapter.StepsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull StepsAdapter.StepsViewHolder holder, int position) {
        holder.BindView(position);
    }

    @Override
    public int getItemCount() {
        return (mSteps != null) ? mSteps.size(): 0;
    }

    public void loadSteps(List<Steps> steps) {
        mSteps = steps;
    }

    public class StepsViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        @BindView(R.id.listItem)
        TextView contentTxt;

        public StepsViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(this);
        }

        public void BindView(final int position) {
            Steps steps = mSteps.get(position);
            contentTxt.setText(steps.getShortDescription());
        }

        @Override
        public void onClick(View v) {
            int adapterPosition = getAdapterPosition();
            Steps loadStep = mSteps.get(adapterPosition);
            mClickHandler.onClick(loadStep, adapterPosition);
        }
    }
}
