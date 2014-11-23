package com.example.sportsapp;


import android.os.Bundle;
import android.support.v4.app.FragmentActivity;


public class LoginSplashActivity extends FragmentActivity {
	
	private MainFragment mainFragment;
	
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(savedInstanceState != null)
        {
        	// Or set the fragment from restored state info
	        mainFragment = (MainFragment) getSupportFragmentManager()
	        .findFragmentById(android.R.id.content);
        }else{
			// Add the fragment on initial activity setup
	        mainFragment = new MainFragment();
	        getSupportFragmentManager()
	        .beginTransaction()
	        .add(android.R.id.content, mainFragment)
	        .commit();
        }
    }

    
	
    private void openSettings() {
		// TODO Auto-generated method stub
		
	}


	private void openSearch() {
		// TODO Auto-generated method stub
		
	}


    
	@Override
	public void onSaveInstanceState(Bundle savedInstanceState) {
		// Save stuff..
	    super.onSaveInstanceState(savedInstanceState);
	}
	
    
    
}
