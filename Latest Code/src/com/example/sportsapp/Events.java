package com.example.sportsapp;


import com.facebook.AppEventsLogger;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;


public class Events extends FragmentActivity {
	private static final String TAG = "Events";
	
	static String UserID = null;
	

    private static final int LIST = 0;
    private static final int CREATE = 1;
    private static final int VIEW = 2;
    private static final int SETTINGS = 3;
    private static final int FRAGMENT_COUNT = SETTINGS +1;

    private Fragment[] fragments = new Fragment[FRAGMENT_COUNT];
    private MenuItem settings;
	private boolean isResumed = false;
	
	private UiLifecycleHelper uiHelper;
	private Session.StatusCallback callback = new Session.StatusCallback() {
        @Override
        public void call(Session session, SessionState state, Exception exception) {
            onSessionStateChange(session, state, exception);
        }
    };
	
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(savedInstanceState != null)
        {
        	UserID 		= savedInstanceState.getString("UserID");
        }else{
        }
        
        uiHelper = new UiLifecycleHelper(this, callback);
        uiHelper.onCreate(savedInstanceState);
        
        setContentView(R.layout.events);
        
        // Find fragments
        FragmentManager fm = getSupportFragmentManager();
        ListEventsFragment listEventsFragment = (ListEventsFragment) fm.findFragmentById(R.id.listFragment);
        fragments[LIST] = listEventsFragment;
        fragments[CREATE] = fm.findFragmentById(R.id.createFragment);
        fragments[VIEW] = fm.findFragmentById(R.id.viewFragment);
        fragments[SETTINGS] = fm.findFragmentById(R.id.userSettingsFragment);
        
        // Hide fragments
        FragmentTransaction transaction = fm.beginTransaction();
        for(int i = 0; i < fragments.length; i++) {
            transaction.hide(fragments[i]);
        }
        transaction.commit();
        
        
        listEventsFragment.setCreateCallback(new ListEventsFragment.ButtonPressedCallback() {
            @Override
            public void onButtonPressed() {
                showFragment(CREATE, false);
                Log.d(TAG,"Create Button Pressed");
            }
        });
        
        listEventsFragment.setViewEventCallback(new ListEventsFragment.ButtonPressedCallback() {
            @Override
            public void onButtonPressed() {
                showFragment(VIEW, false);
                Log.d(TAG,"Create Button Pressed");
            }
        });
        
        
    }
    
    @Override
    public void onResume() {
        super.onResume();
        uiHelper.onResume();
        isResumed = true;

        // Call the 'activateApp' method to log an app event for use in analytics and advertising reporting.  Do so in
        // the onResume methods of the primary Activities that an app may be launched into.
        AppEventsLogger.activateApp(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        uiHelper.onPause();
        isResumed = false;

        // Call the 'deactivateApp' method to log an app event for use in analytics and advertising
        // reporting.  Do so in the onPause methods of the primary Activities that an app may be launched into.
        AppEventsLogger.deactivateApp(this);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        uiHelper.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        uiHelper.onDestroy();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        uiHelper.onSaveInstanceState(outState);
    }

    @Override
    protected void onResumeFragments() {
    	Log.d(TAG,"onResumeFragments");
        super.onResumeFragments();
        Session session = Session.getActiveSession();
        showFragment(LIST, false);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        if (!fragments[SETTINGS].isVisible()) {
            if (menu.size() == 0) {
                settings = menu.add(R.string.settings);
            }
            return true;
        } else {
            menu.clear();
            settings = null;
        }
        return false;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.equals(settings)) {
            showSettingsFragment();
            return true;
        }
        return false;
    }

    public void showSettingsFragment() {
        showFragment(SETTINGS, true);
    }

    private void onSessionStateChange(Session session, SessionState state, Exception exception) {
    	Log.d(TAG,"onSessionStateChange");
        if (isResumed) {
            FragmentManager manager = getSupportFragmentManager();
            int backStackSize = manager.getBackStackEntryCount();
            for (int i = 0; i < backStackSize; i++) {
                manager.popBackStack();
            }
            if (state.equals(SessionState.OPENED)) {
                showFragment(LIST, false);
            } else if (state.isClosed()) {
                finish();
            }
        }
    }

    private void showFragment(int fragmentIndex, boolean addToBackStack) {
    	Log.d(TAG,"showFragment for "+ String.valueOf(fragmentIndex));
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction transaction = fm.beginTransaction();
        for (int i = 0; i < fragments.length; i++) {
            if (i == fragmentIndex) {
                transaction.show(fragments[i]);
            } else {
                transaction.hide(fragments[i]);
            }
        }
        if (addToBackStack) {
            transaction.addToBackStack(null);
        }
        transaction.commit();
    }  
}
