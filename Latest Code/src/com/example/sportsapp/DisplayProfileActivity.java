package com.example.sportsapp;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.support.v7.app.ActionBarActivity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class DisplayProfileActivity extends ActionBarActivity {
	
	boolean ProfileDataPresent 	= false; 
	
	static String UserID 		= null;
	static String FirstName		= null;
	static String LastName 		= null;
	static String Dob 			= null;
	boolean Admin				= false;
	
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_display_profile);
		
		if (savedInstanceState != null) {
			UserID 		= savedInstanceState.getString("UserID");
			FirstName 	= savedInstanceState.getString("FirstName");
			LastName 	= savedInstanceState.getString("LastName");
			Dob 		= savedInstanceState.getString("Dob");
			Admin 		= savedInstanceState.getBoolean("Admin");
		}else{
			// Get the message from the intent
		    Intent intent = getIntent();
		    UserID 		= intent.getStringExtra(R.string.EXTRA_PREFIX + "userID");
		    Admin		= intent.getBooleanExtra(R.string.EXTRA_PREFIX + "admin", false);
		    
		    // Request data from server
			ProfileDataPresent = getProfileData();
		}


    	TextView text_userID 	= (TextView) findViewById(R.id.userID);
    	TextView text_firstName = (TextView) findViewById(R.id.firstName);
    	TextView text_lastName 	= (TextView) findViewById(R.id.lastName);
    	TextView text_dob 		= (TextView) findViewById(R.id.dob);
    	
    	
    	text_userID.setText(UserID);
    	if(ProfileDataPresent){
	    	text_firstName.setText(FirstName);
	    	text_lastName.setText(LastName);
	    	text_dob.setText(Dob);
    	}else {
    		// TODO user profile fail...
    		// TODO timeout reached...
    	}
    	
    	Button edit_button		= (Button) findViewById(R.id.editButton);
    	if(Admin){
    		// Double check admin status..
    		// TODO this check doesn't work...
//    		if(UserID != MainActivity.UserID){
    			// TODO throw error...
//    		}else{
    			// TODO make edit profile button visible
    			Log.d("DisplayProfile","Admin user");
    			edit_button.setVisibility(android.view.View.VISIBLE);
//    		}
    	}else{
    		// TODO make edit profile button invisible
    		edit_button.setVisibility(android.view.View.GONE);
    	}
	    

//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        // If your minSdkVersion is 11 or higher, instead use:
        // getActionBar().setDisplayHomeAsUpEnabled(true);

		
	}


	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
	    // Inflate the menu items for use in the action bar
	    MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.main_activity_actions, menu);
	    return super.onCreateOptionsMenu(menu);
	}
	
	public boolean getProfileData() {
		
		JSONfunctions.clearResponseBuffer();
    	
    	String message 		= null;
    	int success 		= 0;
    	
    	JSONObject request = new JSONObject();
    	 
    	try {
			request.put("userID", UserID);
		} catch (JSONException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
    	Log.d("log_tag","Request: " + request.toString());
    	
    	JSONfunctions.setRequestObject(request);
    	String url = getString(R.string.getProfile);
    	Log.d("DisplayEventActivity","Url: " + url);
    	new JSONfunctions().execute(getString(R.string.getProfile));

    	// TODO more with timeout error... (make global?)
    	long timeStart = System.currentTimeMillis();
    	boolean timeout = false;
    	int timeoutSeconds = 10;
    	JSONArray responseArray = null;
    	while (true){
    		if(timeStart + timeoutSeconds*1000 < System.currentTimeMillis()){	// Timeout (10seconds...)
    			Log.d("DisplayProfileActivity","No server response.");
    			Log.d("DisplayProfileActivity","Timeout triggered after " + timeoutSeconds + " seconds");
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
	    	
	    	Log.d("DisplayProfileActivity","Message: " + message);
	    	
	    	if(success == 1) // User profile exists, get data
	    	{
	    		try {
				FirstName 	= responseObject.getString("firstName");
				LastName 	= responseObject.getString("lastName");
				Dob		 	= responseObject.getString("dob");
	    		} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
	    	}else{
	    		// TODO if user profile doesn't exist.
	    		return false;
	    	}
	    	return true;
    	}
		return false;
	}
	
	public void editProfile(View view) {
        // Do something in response to button
    	Intent intent = new Intent(this, EditProfileActivity.class);
    	
    	startActivity(intent);
    }
	
	@Override
	protected void onRestart (){
		Log.d("Debug","onRestart Function called for Display activity..");
		super.onRestart();
		TextView text_userID 	= (TextView) findViewById(R.id.userID);
    	TextView text_firstName = (TextView) findViewById(R.id.firstName);
    	TextView text_lastName 	= (TextView) findViewById(R.id.lastName);
    	TextView text_dob 		= (TextView) findViewById(R.id.dob);
    	
    	
    	text_userID.setText(UserID);
    	if(ProfileDataPresent){
	    	text_firstName.setText(FirstName);
	    	text_lastName.setText(LastName);
	    	text_dob.setText(Dob);
    	}
	}
	
	@Override
	public void onSaveInstanceState(Bundle savedInstanceState) {
		// Save stuff..
		savedInstanceState.putBoolean("ProfileDataPresent", ProfileDataPresent);
	    savedInstanceState.putString("UserID", UserID);
	    savedInstanceState.putBoolean("Admin", Admin);
		if(ProfileDataPresent){
		    savedInstanceState.putString("FirstName", FirstName);
		    savedInstanceState.putString("LastName", LastName);
		    savedInstanceState.putString("Dob", Dob);
		}
		
	    super.onSaveInstanceState(savedInstanceState);
	}
	
	    
	    
}
