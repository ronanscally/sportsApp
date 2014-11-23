package com.example.sportsapp;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;

import org.json.JSONException;
import org.json.JSONObject;

import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;
import com.facebook.model.GraphUser;
import com.facebook.widget.ProfilePictureView;
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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

public class HomeFragment extends Fragment {
//	private static final String TAG = "HomeFragment";
	private ProfilePictureView profilePictureView;
	private Button viewProfileButton;
	private Button viewEventsButton;
	private Button viewGroupsButton;
	private Button viewInvitationsButton;
//	private Button viewFriendsButton;
	
//	private MainActivity activity = null;
	
	private String UserID = null;
//	private boolean pendingAnnounce;
	private static final String TAG = "HomeFragment";
	
	private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
	public static final String PROPERTY_REG_ID = "registration_id";
	private static final String PROPERTY_APP_VERSION = "appVersion";
	public String userIdent = "12345";
	String SENDER_ID = "191229453439";
	
	TextView mDisplay;
    GoogleCloudMessaging gcm;
    AtomicInteger msgId = new AtomicInteger();
    Context context;

    private String regid;
	
	
	
	private UiLifecycleHelper uiHelper;
    private Session.StatusCallback sessionCallback = new Session.StatusCallback() {
        @Override
        public void call(final Session session, final SessionState state, final Exception exception) {
            onSessionStateChange(session, state, exception);
        }
    };
	
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.home, container, false);
        System.out.println("Entered Home Fragment");
        profilePictureView 		= (ProfilePictureView) view.findViewById(R.id.selection_profile_pic);
        viewProfileButton 		= (Button) view.findViewById(R.id.view_profile);
        viewEventsButton 		= (Button) view.findViewById(R.id.view_events);
        viewGroupsButton 		= (Button) view.findViewById(R.id.view_groups);
        viewInvitationsButton 	= (Button) view.findViewById(R.id.view_invitations);
//        viewFriendsButton 		= (Button) view.findViewById(R.id.view_friends);
        profilePictureView.setCropped(true);
        Session session = Session.getActiveSession();
        if (session != null && session.isOpened()) {
            // Get the user's data
            makeMeRequest(session);
        }
    	
        
    
    Log.d(TAG,"Ending onCreateFunction");
        
     
        
        
        viewProfileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            	// View own profile
            	Intent intent = new Intent(getActivity(), DisplayProfileActivity.class);
            	intent.putExtra(R.string.EXTRA_PREFIX + "userID", UserID);
            	intent.putExtra(R.string.EXTRA_PREFIX + "admin", true);
            	startActivity(intent);
            }
        });
        
        viewEventsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            	Intent intent = new Intent(getActivity(), ListEventsActivity.class);
            	intent.putExtra(R.string.EXTRA_PREFIX + "userID", UserID);
            	startActivity(intent);
            }
        });
        
        viewGroupsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            	Intent intent = new Intent(getActivity(), ListGroupsActivity.class);
            	intent.putExtra(R.string.EXTRA_PREFIX + "userID", UserID);
            	startActivity(intent);
            }
        });
        
        viewInvitationsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            	Intent intent = new Intent(getActivity(), Events.class);
            	intent.putExtra(R.string.EXTRA_PREFIX + "userID", UserID);
            	startActivity(intent);
            }
        });
        
        return view;
    }
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(savedInstanceState != null)
        {
        	UserID 		= savedInstanceState.getString("UserID");
        }

        context = this.getActivity();
        
        
     // Check device for Play Services APK. If check succeeds, proceed with GCM registration.
        if (checkPlayServices()) {
            gcm = GoogleCloudMessaging.getInstance(this.getActivity());
            regid = getRegistrationId(context);
            System.out.println(regid);
            System.out.println("Marks reg FRAG Id" + regid);
            
            //UserID = "10152504871783499";
            if (regid.isEmpty()) {
                registerInBackground();
                System.out.println("Finished register in bg");
            }
        } else {
            Log.i(TAG, "No valid Google Play Services APK found.");
        }
        
        
//        activity = (MainActivity) getActivity();
        uiHelper = new UiLifecycleHelper(getActivity(), sessionCallback);
        uiHelper.onCreate(savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();
        uiHelper.onResume();
    }
	
    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putString("UserID", UserID);
        uiHelper.onSaveInstanceState(savedInstanceState);
    }
    
    @Override
    public void onPause() {
        super.onPause();
        uiHelper.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        uiHelper.onDestroy();
    }
	
//	private void tokenUpdated() {
//        if (pendingAnnounce) {
////            handleAnnounce(false);
//        }
//    }
	
	private void onSessionStateChange(final Session session, SessionState state, Exception exception) {
		UserID = null;
        if (session != null && session.isOpened()) {
            if (state.equals(SessionState.OPENED_TOKEN_UPDATED)) {
//                tokenUpdated();
            } else {
                makeMeRequest(session);
            }
        } else {
            profilePictureView.setProfileId(null);
        }
    }
	
	private void makeMeRequest(final Session session) {
        Request request = Request.newMeRequest(session, new Request.GraphUserCallback() {
            @Override
            public void onCompleted(GraphUser user, Response response) {
                if (session == Session.getActiveSession()) {
                	System.out.println("ACTIVE SESSION");
                    if (user != null) {
                    	System.out.println("ACTIVE SESSION 1");
                    	UserID = user.getId();
                    	userIdent = UserID;
                		sendRegistrationIdToBackend();
                		
                		
                		if (regid.isEmpty()) {
                            registerInBackground();
                            System.out.println("Registered in Background: " + regid);
                        }
                		
                    	
                    	System.out.println("ACTIVE SESSION 2: " + UserID);
                    	profilePictureView.setProfileId(UserID);
                    }
                }
                if (response.getError() != null) {
//                    handleError(response.getError());
                }
            }
        });
        request.executeAsync();

    }
	
	
	/**
     * Check the device to make sure it has the Google Play Services APK. If
     * it doesn't, display a dialog that allows users to download the APK from
     * the Google Play Store or enable it in the device's system settings.
     */
    private boolean checkPlayServices() {
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this.getActivity());
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                GooglePlayServicesUtil.getErrorDialog(resultCode, this.getActivity(),
                        PLAY_SERVICES_RESOLUTION_REQUEST).show();
            } else {
                Log.i(TAG, "This device is not supported.");
                //finish();
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
        return this.getActivity().getSharedPreferences(MainActivity.class.getSimpleName(),
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
                    //sendRegistrationIdToBackend();

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
