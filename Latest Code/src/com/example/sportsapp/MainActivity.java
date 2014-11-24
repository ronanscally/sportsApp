package com.example.sportsapp;


import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.facebook.AppEventsLogger;
import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;
import com.facebook.model.GraphUser;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.gcm.GoogleCloudMessaging;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;


public class MainActivity extends FragmentActivity {
	private static final String TAG = "MainActivity";
	private static final String USER_SKIPPED_LOGIN_KEY = "user_skipped_login";
	private static final String USER_PROFILE_EXISTS = "user_profile_exists";
	

    private static final int SPLASH = 0;
    private static final int HOME = 1;
    private static final int NEW_USER = 2;
    private static final int SETTINGS = 3;
    private static final int FRAGMENT_COUNT = SETTINGS +1;

    private Fragment[] fragments = new Fragment[FRAGMENT_COUNT];
    private MenuItem settings;
    private boolean userSkippedLogin = false;
	private boolean isResumed = false;
	
	private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
	public static final String PROPERTY_REG_ID = "registration_id";
	private static final String PROPERTY_APP_VERSION = "appVersion";
	
	/**
     * Substitute you own sender ID here. This is the project number you got
     * from the API Console, as described in "Getting Started."
     */
	//TODO ask Mark about this value
    String SENDER_ID = "191229453439";
	
	
	TextView mDisplay;
    GoogleCloudMessaging gcm;
    AtomicInteger msgId = new AtomicInteger();
    Context context;

    String regid;
	
	private UiLifecycleHelper uiHelper;
	private Session.StatusCallback callback = new Session.StatusCallback() {
        @Override
        public void call(Session session, SessionState state, Exception exception) {
        	Log.d(TAG,"Session.StatusCallback");
        	Log.d(TAG,"call + onSessionStateChange");
            onSessionStateChange(session, state, exception);
        }
    };
	protected String UserID;
	private boolean UserProfileExists = true;
	protected boolean UserProfileCreated = false;
	
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        uiHelper = new UiLifecycleHelper(this, callback);
        uiHelper.onCreate(savedInstanceState);
        
        if(savedInstanceState != null)
        {
        	UserID 		= savedInstanceState.getString("UserID");
        	UserProfileExists = savedInstanceState.getBoolean(USER_PROFILE_EXISTS);
        }
        
        setContentView(R.layout.main);
        
        // Find fragments
        FragmentManager fm = getSupportFragmentManager();
        SplashFragment splashFragment = (SplashFragment) fm.findFragmentById(R.id.splashFragment);
        fragments[SPLASH] = splashFragment;
        fragments[HOME] = fm.findFragmentById(R.id.homeFragment);
        fragments[NEW_USER] = fm.findFragmentById(R.id.newUserFragment);
        fragments[SETTINGS] = fm.findFragmentById(R.id.userSettingsFragment);
        
        // Hide fragments
        FragmentTransaction transaction = fm.beginTransaction();
        for(int i = 0; i < fragments.length; i++) {
            transaction.hide(fragments[i]);
        }
        transaction.commit();
        
        splashFragment.setSkipLoginCallback(new SplashFragment.SkipLoginCallback() {
            @Override
            public void onSkipLoginPressed() {
//                userSkippedLogin = true;
                showFragment(HOME, false);
            }
        });
        
		
		GPSTracker gps = new GPSTracker(this);
		 
		double latitude =  gps.getLatitude();
		double longitude = gps.getLongitude();
		Toast.makeText(getApplicationContext(), "Your Location is - \nLat: " + latitude + "\nLong: " + longitude, Toast.LENGTH_LONG).show();
		//gps.showSettingsAlert();
		
		//setContentView(R.layout.gcm);
		//mDisplay = (TextView) findViewById(R.id.display);

		context = getApplicationContext();
		
		// Check device for Play Services APK. If check succeeds, proceed with GCM registration.
		if (checkPlayServices()) {
			gcm = GoogleCloudMessaging.getInstance(this);
			regid = getRegistrationId(context);
			System.out.println(regid);
			HomeFragment home = new HomeFragment(); 
			System.out.println("Marks User Id" + home.userIdent);
			
			//UserID = "10152504871783499";
			if (regid.isEmpty()) {
				registerInBackground();
				System.out.println("Finished register in bg");
			}
		} else {
			Log.i(TAG, "No valid Google Play Services APK found.");
		}
		
		Log.i(TAG, regid);
		
        Log.d(TAG,"Ending onCreateFunction");
    }
	
	//TODO ask Mark about this hard coded value..
	public String getRegId(){
    	return "1234567";
    } 
    
    @Override
    public void onResume() {
        super.onResume();
        uiHelper.onResume();
        isResumed = true;

        // Call the 'activateApp' method to log an app event for use in analytics and advertising reporting.  Do so in
        // the onResume methods of the primary Activities that an app may be launched into.
        AppEventsLogger.activateApp(this);
		
		// Check device for Play Services APK.
        checkPlayServices();
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

        outState.putBoolean(USER_SKIPPED_LOGIN_KEY, userSkippedLogin);
        outState.putBoolean(USER_PROFILE_EXISTS,UserProfileExists);
        outState.putString("UserID",UserID);
    }

    @Override
    protected void onResumeFragments() {
    	Log.d(TAG,"onResumeFragments");
        super.onResumeFragments();
        Session session = Session.getActiveSession();

        if (session != null && session.isOpened()) {
        	Log.d(TAG,"session.isOpened");
            // if the session is already open, try to show the selection fragment
        	if(UserProfileExists){
        		showFragment(HOME, false);
        	}else{
        		showFragment(NEW_USER,false);
        	}
            userSkippedLogin = false;
        } else if (userSkippedLogin) {
        	// TODO remove skip login button...
            showFragment(HOME, false);
        } else {
            // otherwise present the splash screen and ask the user to login, unless the user explicitly skipped.
            showFragment(SPLASH, false);
        }
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        // only add the menu when the selection fragment is showing
        if (fragments[HOME].isVisible() || fragments[NEW_USER].isVisible()) {
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
            // check for the OPENED state instead of session.isOpened() since for the
            // OPENED_TOKEN_UPDATED state, the selection fragment should already be showing.
            if (state.equals(SessionState.OPENED)) {
	            makeMeRequest(session);
            	if(UserProfileExists){
            		showFragment(HOME, false);
            	}else{
            		showFragment(NEW_USER,false);
            	}
            } else if (state.isClosed()) {
                showFragment(SPLASH, false);
            }
        }
    }

    private boolean checkUserProfile() {
    	String message 		= null;
    	int success 		= 0;
    	
    	JSONObject request = new JSONObject();
    	 
    	try {
			request.put("userID", UserID);
		} catch (JSONException e1) {
			e1.printStackTrace();
		}
    	Log.d("TAG","Request: " + request.toString());
    	JSONfunctions.setRequestObject(request);
    	String url = getString(R.string.getProfile);
    	Log.d("TAG","Url: " + url);
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
        		Log.e(TAG,"Unexpected response length...");
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
	    	
	    	if(success == 1)	// User Profile exists
	    	{
	    		return true;
	    	}else if(success == -1){ // User Profile doesn't Exist
	    		return false;
	    	}else if(success == 0){
	    		// TODO if poor request
	    		Log.e(TAG,"Poor Request..");
	    	}
	    	else{
	    		// TODO if poor response
	    		Log.e(TAG,"Poor Response..");
	    	}
    	}
		return false;
	}
    
    private void makeMeRequest(final Session session) {
        Request request = Request.newMeRequest(session, new Request.GraphUserCallback() {
            @Override
            public void onCompleted(GraphUser user, Response response) {
                if (session == Session.getActiveSession()) {
                    if (user != null) {
                    	UserID = user.getId();
                    	UserProfileExists = checkUserProfile();
                    	if(UserProfileExists){
                    		showFragment(HOME, false);
                    	}else{
                    		showFragment(NEW_USER,false);
                    		// Create entry for user in user table
//                    		if(!UserProfileCreated)
//                    			UserProfileCreated = createUser(user);
                    		UserProfileExists = createUser(user);
                    		Log.d(TAG,"UserProfileExists: " + UserProfileExists);
                    	}
                    }
                }
                if (response.getError() != null) {
//                    handleError(response.getError());
                }
            }
        });
        request.executeAsync();

    }

	protected boolean createUser(GraphUser user) {
		// TODO Auto-generated method stub
		String message 		= null;
    	int success 		= 0;
    	
    	String userID = user.getId();
    	String firstName = user.getFirstName();
    	String lastName = user.getLastName();
    	String dob = user.getBirthday();
    	Log.d(TAG,"DOB: " + dob);
    	if(dob == null){
    		dob = "1970-01-01";
    		Log.d(TAG,"Setting DOB to: " + dob);
    	}
    	
    	JSONObject request = new JSONObject();
    	 
    	try {
			request.put("userID", userID);
			request.put("firstName", firstName);
			request.put("lastName", lastName);
			request.put("dob", dob);
		} catch (JSONException e1) {
			e1.printStackTrace();
		}
    	Log.d(TAG,"Request: " + request.toString());
    	JSONfunctions.setRequestObject(request);
    	String url = getString(R.string.createProfile);
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
        		Log.e(TAG,"Unexpected response length...");
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
	    	
	    	if(success == 1)	// User Profile created
	    	{
	    		return true;
	    	}else if(success == -1){
	    		Log.e(TAG,"Unable to create entry for user");
	    		return false;
	    	}else if(success == 0){
	    		// TODO if poor request
	    		Log.e(TAG,"Poor Request..");
	    	}else{
	    		// TODO if poor response
	    		Log.e(TAG,"Poor Response..");
	    	}
    	}
		return false;
	}

	private void showFragment(int fragmentIndex, boolean addToBackStack) {
    	Log.d(TAG,"showFragment");
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
	
	
	/**
     * Check the device to make sure it has the Google Play Services APK. If
     * it doesn't, display a dialog that allows users to download the APK from
     * the Google Play Store or enable it in the device's system settings.
     */
    private boolean checkPlayServices() {
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                GooglePlayServicesUtil.getErrorDialog(resultCode, this,
                        PLAY_SERVICES_RESOLUTION_REQUEST).show();
            } else {
                Log.i(TAG, "This device is not supported.");
                finish();
            }
            return false;
        }
        return true;
    }
    
    
    /**
     * Gets the current registration ID for application on GCM service, if there is one.
     * <p>
     * If result is empty, the app needs to register.
     *
     * @return registration ID, or empty string if there is no existing
     *         registration ID.
     */
    private String getRegistrationId(Context context) {
        final SharedPreferences prefs = getGcmPreferences(context);
        String registrationId = prefs.getString(PROPERTY_REG_ID, "");
        if (registrationId.isEmpty()) {
            Log.i(TAG, "Registration not found.");
            return "";
        }
        // Check if app was updated; if so, it must clear the registration ID
        // since the existing regID is not guaranteed to work with the new
        // app version.
        int registeredVersion = prefs.getInt(PROPERTY_APP_VERSION, Integer.MIN_VALUE);
        int currentVersion = getAppVersion(context);
        if (registeredVersion != currentVersion) {
            Log.i(TAG, "App version changed.");
            return "";
        }
        return registrationId;
    }
    
    
    /**
     * Stores the registration ID and the app versionCode in the application's
     * {@code SharedPreferences}.
     *
     * @param context application's context.
     * @param regId registration ID
     */
    private void storeRegistrationId(Context context, String regId) {
        final SharedPreferences prefs = getGcmPreferences(context);
        int appVersion = getAppVersion(context);
        Log.i(TAG, "Saving regId on app version " + appVersion);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(PROPERTY_REG_ID, regId);
        editor.putInt(PROPERTY_APP_VERSION, appVersion);
        editor.commit();
    }
    
    
    /**
     * @return Application's version code from the {@code PackageManager}.
     */
    private static int getAppVersion(Context context) {
        try {
            PackageInfo packageInfo = context.getPackageManager()
                    .getPackageInfo(context.getPackageName(), 0);
            return packageInfo.versionCode;
        } catch (NameNotFoundException e) {
            // should never happen
            throw new RuntimeException("Could not get package name: " + e);
        }
    }
    
    
    /**
     * @return Application's {@code SharedPreferences}.
     */
    private SharedPreferences getGcmPreferences(Context context) {
        // This sample app persists the registration ID in shared preferences, but
        // how you store the regID in your app is up to you.
        return getSharedPreferences(MainActivity.class.getSimpleName(),
                Context.MODE_PRIVATE);
    }
    
    
    /**
     * Registers the application with GCM servers asynchronously.
     * <p>
     * Stores the registration ID and the app versionCode in the application's
     * shared preferences.
     */
    private void registerInBackground() {
        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... params) {
                String msg = "";
                try {
                    if (gcm == null) {
                        gcm = GoogleCloudMessaging.getInstance(context);
                    }
                    regid = gcm.register(SENDER_ID);
                    msg = "Device registered, registration ID=" + regid;

                    // You should send the registration ID to your server over HTTP, so it
                    // can use GCM/HTTP or CCS to send messages to your app.
                    sendRegistrationIdToBackend();

                    // For this demo: we don't need to send it because the device will send
                    // upstream messages to a server that echo back the message using the
                    // 'from' address in the message.

                    // Persist the regID - no need to register again.
                    storeRegistrationId(context, regid);
                } catch (IOException ex) {
                    msg = "Error :" + ex.getMessage();
                    // If there is an error, don't just keep trying to register.
                    // Require the user to click a button again, or perform
                    // exponential back-off.
                }
                return msg;
            }

            @Override
            protected void onPostExecute(String msg) {
                //mDisplay.append(msg + "\n");
            }
        }.execute(null, null, null);
    }
    
    
    
    
    /**
     * Sends the registration ID to your server over HTTP, so it can use GCM/HTTP or CCS to send
     * messages to your app. Not needed for this demo since the device sends upstream messages
     * to a server that echoes back the message using the 'from' address in the message.
     */
    private void sendRegistrationIdToBackend() {
      // Your implementation here.
    	try{
        	JSONObject request = new JSONObject();
        	request.put("userID", UserID);
    		request.put("deviceID", regid);
    		
    		System.out.println("I Got Here, Class!! semicolon");
    		// Send to server
        	JSONfunctions.setRequestObject(request);
        	String save_location = getString(R.string.addDevice);
        	new JSONfunctions().execute(save_location);
    	}catch(JSONException e){
    		System.out.println("Send Location to server");
    		e.printStackTrace();
    	}catch(Exception e){
    		System.out.println("General Error");
    		e.printStackTrace();
    		
    	}
    }
	
}
