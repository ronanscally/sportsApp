package com.example.sportsapp;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

public class NewUserFragment extends Fragment {

	private static final String TAG = "NewUserFragment";
	private ProceedButtonCallback proceedButtonCallback;
	private Button proceedButton;

    public interface ProceedButtonCallback {
        void onProceedButtonPressed();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.new_user, container, false);
        
        proceedButton = (Button) view.findViewById(R.id.proceedButton);
        proceedButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (proceedButtonCallback != null) {
                	Log.d(TAG,"onClick");
                	proceedButtonCallback.onProceedButtonPressed();
                }
            }
        });
        
        return view;
    }
    
    public void setProceedButtonCallback(ProceedButtonCallback callback) {
    	Log.d(TAG,"setProceedButtonCallback");
    	proceedButtonCallback = callback;
    }
    
    
}

