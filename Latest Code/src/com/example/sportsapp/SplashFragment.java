package com.example.sportsapp;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.facebook.widget.LoginButton;

public class SplashFragment extends Fragment {

	private static final String TAG = "SplashFragment";
    private LoginButton loginButton;
    private TextView skipLoginButton;
    private SkipLoginCallback skipLoginCallback;

    public interface SkipLoginCallback {
        void onSkipLoginPressed();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.splash, container, false);

        loginButton = (LoginButton) view.findViewById(R.id.login_button);
        loginButton.setReadPermissions("user_friends","user_birthday");

        skipLoginButton = (TextView) view.findViewById(R.id.skip_login_button);
        skipLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (skipLoginCallback != null) {
                	Log.d(TAG,"onClick");
                    skipLoginCallback.onSkipLoginPressed();
                }
            }
        });
        Log.d(TAG,"Ending onCreateView");
        return view;
    }

    public void setSkipLoginCallback(SkipLoginCallback callback) {
    	Log.d(TAG,"setSkipLoginCallback");
        skipLoginCallback = callback;
    }
}

