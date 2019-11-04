package com.example.recipeapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.recipeapp.database.Steps;

import java.util.ArrayList;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;

public class StepsFragment extends Fragment implements StepsAdapter.StepsAdapterOnClickHandler {

    private static final String STEPS = "steps";
    private static final String IMGSRC = "image";
    private static final String RUNONTAB = "twoPane";
    private ArrayList<Steps> mSteps;
    @BindView(R.id.recipeRv)
    RecyclerView rListView;
    @BindView(R.id.emptyTextView)
    TextView emptyTxtVw;
    @BindView(R.id.loadingIndicator)
    ProgressBar loadingBar;
    private String mImgsrc;
    private StepsAdapter stepsAdapter;
    private boolean mTwoPane;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public StepsFragment() {
    }

    public static StepsFragment newInstance(String image, ArrayList<Steps> stepsArrayList, boolean mTwoPane) {
        StepsFragment fragment = new StepsFragment();
        Bundle args = new Bundle();
        args.putParcelableArrayList(STEPS, stepsArrayList);
        args.putString(IMGSRC, image);
        args.putBoolean(RUNONTAB, mTwoPane);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mSteps = getArguments().getParcelableArrayList(STEPS);
            mImgsrc = getArguments().getString(IMGSRC);
            mTwoPane = getArguments().getBoolean(RUNONTAB);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_steps, container, false);
        ButterKnife.bind(this, view);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        stepsAdapter = new StepsAdapter(getContext(), this);
        rListView.setLayoutManager(linearLayoutManager);
        rListView.setHasFixedSize(true);
        rListView.setHorizontalScrollBarEnabled(true);
        rListView.setAdapter(stepsAdapter);
        if (mSteps != null && mSteps.size() > 0) {
            stepsAdapter.loadSteps(mSteps);
            loadingBar.setVisibility(View.GONE);
            stepsAdapter.notifyDataSetChanged();
        } else {
            showError(getString(R.string.no_steps));
        }
        return view;
    }

    public void showError(String msg) {
        rListView.setVisibility(View.GONE);
        emptyTxtVw.setVisibility(View.VISIBLE);
        loadingBar.setVisibility(View.GONE);
        emptyTxtVw.setText(msg);
    }

    @Override
    public void onClick(Steps steps, int position) {
        if (mTwoPane) {
            //Run on Tablet.
            VideoFragment videoFragment = VideoFragment.newInstance(mImgsrc, mSteps, position);
            Objects.requireNonNull(getActivity()).getSupportFragmentManager().beginTransaction().replace(R.id.rdetail_video_container, videoFragment).commit();
        } else {
            //Run on Phone.
            Intent intent = new Intent(getContext(), VideoActivity.class);
            intent.putParcelableArrayListExtra("steps", mSteps);
            intent.putExtra("imageSrc", mImgsrc);
            intent.putExtra("index", position);
            startActivity(intent);
        }
    }
}
