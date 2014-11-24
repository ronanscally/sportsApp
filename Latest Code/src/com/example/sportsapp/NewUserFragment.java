package com.example.sportsapp;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class NewUserFragment extends Fragment {

	private static final String TAG = "NewUserFragment";

    public interface SkipLoginCallback {
        void onSkipLoginPressed();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.new_user, container, false);

        Log.d(TAG,"Ending onCreateView");
        return view;
    }
    
    
}

