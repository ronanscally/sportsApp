package com.example.sportsapp;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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
	static String EventID = null;
	

    private static final int LIST = 0;
    private static final int CREATE = 1;
    private static final int VIEW = 2;
    private static final int SETTINGS = 3;
    private static final int FRAGMENT_COUNT = SETTINGS +1;

    private Fragment[] fragments = new Fragment[FRAGMENT_COUNT];

	private ViewEventFragment viewEventFragment;
	
    private MenuItem settings;
	private boolean isResumed = false;
	
	private UiLifecycleHelper uiHelper;
	private Session.StatusCallback callback = new Session.StatusCallback() {
        @Override
        public void call(Session session, SessionState state, Exception exception) {
            onSessionStateChange(session, state, exception);
        }
    };

	private JSONObject EventJSONObject;

	
	
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
        viewEventFragment = (ViewEventFragment) fm.findFragmentById(R.id.viewFragment);
        fragments[LIST] = listEventsFragment;
        fragments[CREATE] = fm.findFragmentById(R.id.createFragment);
        fragments[VIEW] = viewEventFragment;
        fragments[SETTINGS] = fm.findFragmentById(R.id.userSettingsFragment);
        
        // Hide fragments
        FragmentTransaction transaction = fm.beginTransaction();
        for(int i = 0; i < fragments.length; i++) {
            transaction.hide(fragments[i]);
        }
        transaction.commit();
        
        listEventsFragment.setCreateCallback(new ListEventsFragment.ButtonPressedCallback() {
            @Override
            public void onButtonPressed(String id) {
                Log.d(TAG,"Create Button Pressed id: " + id);
                showFragment(CREATE, false);
            }
        });
        
        listEventsFragment.setViewEventCallback(new ListEventsFragment.ButtonPressedCallback() {
            @Override
            public void onButtonPressed(String id) {
                Log.d(TAG,"View Event Pressed id: " + id);
                setEventID(id);
                showFragment(VIEW, false);
            }
        });
        
        viewEventFragment.setBackCallback(new ViewEventFragment.ButtonPressedCallback() {
            @Override
            public void onButtonPressed(String id) {
                Log.d(TAG,"Back Button Pressed id: " + id);
                showFragment(LIST, false);
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
    	Log.d(TAG,"showFragment "+ String.valueOf(fragmentIndex));
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
        if(fragmentIndex == VIEW){	// Get data for view event
            getEventData(EventID);
//            Log.d(TAG,"Data Got!");
//            updateViewEvent();
        	viewEventFragment.setDisplayData(EventJSONObject);
        	
        }
    }

    private void updateViewEvent() {
		// TODO Auto-generated method stub
    	Log.d(TAG,"Updating view");
//    	viewEventFragment.setDisplayData();
	}

	public static String getEventID() {
		return EventID;
	}
	public static void setEventID(String eventID) {
		EventID = eventID;
	} 
	
	private boolean getEventData(String eventID) { 	
		JSONfunctions.clearResponseBuffer();
    	String message 		= null;
    	int success 		= 0;
    	
    	JSONObject request = new JSONObject();
    	 
    	try {
			request.put("eventID", eventID);
		} catch (JSONException e1) {
			e1.printStackTrace();
		}
    	Log.d(TAG,"Request: " + request.toString());
    	

    	JSONfunctions.setRequestObject(request);
    	String url = getString(R.string.getEvent);
    	Log.d(TAG,"Url: " + url);
    	new JSONfunctions().execute(url);

    	// TODO more with timeout error... (make global?)
    	long timeStart = System.currentTimeMillis();
    	boolean timeout = false;
    	int timeoutSeconds = 10;
    	JSONArray responseArray = null;
    	while (true){
    		if(timeStart + timeoutSeconds*1000 < System.currentTimeMillis()){	// Timeout (10seconds...)
    			Log.d(TAG,"No server response.");
    			Log.d(TAG,"Timeout triggered after " + timeoutSeconds + " seconds");
    			timeout = true;
    			break;
    		}
    		
    		if(JSONfunctions.checkNewResponse()){
    			responseArray = JSONfunctions.getResponseArray();
    			break;
    		}
    	}
    	if(!timeout){
    		if(responseArray.length() != 1)
        		Log.e("DisplayProfileActivity","Unexpected response length...");
        	JSONObject responseObject = null;
    		try {
    			responseObject = responseArray.getJSONObject(0);
    		} catch (JSONException e1) {
    			// TODO Auto-generated catch block
    			e1.printStackTrace();
    		}
	    	try {
	    		success 	= responseObject.getInt("success");
	    		message 	= responseObject.getString("message");
	    	} catch (JSONException e) {
	    		e.printStackTrace();
	    	}
	    	
	    	Log.d(TAG,"Message: " + message);
	    	
	    	if(success == 1) // Event exists, get data
	    	{
	    		EventJSONObject = responseObject;
	    	}else{
	    		// TODO if event doesn't exist.
	    		return false;
	    	}
	    	return true;
    	}
		return false;
	}
}
