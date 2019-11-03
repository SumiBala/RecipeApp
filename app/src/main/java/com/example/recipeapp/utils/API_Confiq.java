package com.example.recipeapp.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.example.recipeapp.R;

public class API_Confiq {
    private Context mContext;

    public API_Confiq(Context context) {
        mContext = context;
    }

    public String getRecipeUrl() {
        return mContext.getString(R.string.recipeUrl);
    }

    public boolean netIsConnected() {
        // Get a reference to the ConnectivityManager to check state of network connectivity.
        ConnectivityManager connectivityManager = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        // Get details on the currently active default data network.
        NetworkInfo networkInfo = null;
        if (connectivityManager != null) {
            networkInfo = connectivityManager.getActiveNetworkInfo();
        }
        // If there is a network connection, fetch data
        return networkInfo != null && networkInfo.isConnected();
    }
}
