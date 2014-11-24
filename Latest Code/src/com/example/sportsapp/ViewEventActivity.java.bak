package com.example.sportsapp;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class ViewEventActivity extends ActionBarActivity {
	
	private boolean EventDataPresent 	= false; 
	
	private String 		UserID			= null;
	private String 		EventID 		= null;
	private boolean 	Admin			= false;
	
	
	private String		HostID;		// should equal userID if admin..
	private String		EventName;
	private String		Sport;
	private String		DateTime;
	private short		PeopleRequired;
	private short		PeopleAttending;

	
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_view_event);
	
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        // If your minSdkVersion is 11 or higher, instead use:
        // getActionBar().setDisplayHomeAsUpEnabled(true);
		
		
		if (savedInstanceState != null) {
			UserID 			= savedInstanceState.getString("UserID");
			
			EventID 			= savedInstanceState.getString("EventID");
			Admin				= savedInstanceState.getBoolean("Admin");
			EventDataPresent 	= savedInstanceState.getBoolean("EventDataPresent");
		    
		    HostID				= savedInstanceState.getString("HostID");
		    EventName			= savedInstanceState.getString("EventName");
		    Sport				= savedInstanceState.getString("Sport");
		    DateTime			= savedInstanceState.getString("DateTime");
		    PeopleRequired 		= savedInstanceState.getShort("PeopleRequired");
		    PeopleAttending		= savedInstanceState.getShort("PeopleAttending");
		}else{
			// Get the message from the intent
			Intent intent = getIntent();
			UserID 		= intent.getStringExtra(R.string.EXTRA_PREFIX + "userID");
			EventID 	= intent.getStringExtra(R.string.EXTRA_PREFIX + "eventID");
			Log.d("ViewEventActivity","Event ID		: " + EventID);
			
		    // Request data from server
			EventDataPresent = getEventData();
			
			Log.d("ViewEventActivity","HostID: " + HostID);
			Log.d("ViewEventActivity","UserID: " + UserID);
			
			Admin 		= (UserID.equals(HostID));
			Log.d("ViewEventActivity","Admin status	: " + String.valueOf(Admin));
		}


    	TextView text_eventName 			= (TextView) findViewById(R.id.eventName);
    	TextView text_eventTime 			= (TextView) findViewById(R.id.eventTime);
    	TextView text_eventSport 			= (TextView) findViewById(R.id.eventSport);
    	TextView text_peopleRequired 		= (TextView) findViewById(R.id.peopleRequired);
    	TextView text_peopleAttending 		= (TextView) findViewById(R.id.peopleAttending);
    	
    	Log.d("ViewEventActivity","EventDataPresent: " + String.valueOf(EventDataPresent));
    	
    	if(EventDataPresent){
    		Log.d("ViewEventActivity","Displaying Event Data");
    		text_eventName.setText(EventName);
        	text_eventTime.setText(DateTime);
        	text_eventSport.setText(Sport);
        	text_peopleRequired.setText(String.valueOf(PeopleRequired));
        	text_peopleAttending.setText(String.valueOf(PeopleAttending));
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
    			Log.d("ViewEventActivity","Admin user");
    			edit_button.setVisibility(android.view.View.VISIBLE);
//    		}
    	}else{
    		edit_button.setVisibility(android.view.View.GONE);
    	}
		
		
	}
	
	public void viewAttendees(View view) {
    	// View own profile
    	Intent intent = new Intent(this, ListUsersActivity.class);
    	intent.putExtra(R.string.EXTRA_PREFIX + "listType", "Event");
    	intent.putExtra(R.string.EXTRA_PREFIX + "groupID", EventID);
    	intent.putExtra(R.string.EXTRA_PREFIX + "admin", Admin);
    	startActivity(intent);
    }
	
	
	private boolean getEventData() {
    	// TODO uncomment stuff when php script added    	
    	String message 		= null;
    	int success 		= 0;
    	
    	JSONObject request = new JSONObject();
    	 
    	try {
			request.put("eventID", EventID);
		} catch (JSONException e1) {
			e1.printStackTrace();
		}
    	Log.d("ViewEventActivity","Request: " + request.toString());
    	

    	JSONfunctions.setRequestObject(request);
    	String url = getString(R.string.getEvent);
    	Log.d("ViewEventActivity","Url: " + url);
    	new JSONfunctions().execute(url);

    	// TODO more with timeout error... (make global?)
    	long timeStart = System.currentTimeMillis();
    	boolean timeout = false;
    	int timeoutSeconds = 10;
    	JSONArray responseArray = null;
    	while (true){
    		if(timeStart + timeoutSeconds*1000 < System.currentTimeMillis()){	// Timeout (10seconds...)
    			Log.d("ViewEventActivity","No server response.");
    			Log.d("ViewEventActivity","Timeout triggered after " + timeoutSeconds + " seconds");
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
	    	
	    	Log.d("ViewEventActivity","Message: " + message);
	    	
	    	if(success == 1) // Event exists, get data
	    	{
	    		try {
				HostID 			= 			responseObject.getString("hostID");
				EventName 		= 			responseObject.getString("eventName");
				Sport 			= 			responseObject.getString("sport");
				DateTime 		= 			responseObject.getString("date");
				PeopleRequired 	= (short) 	responseObject.getInt("numReqd");
				PeopleAttending = (short) 	responseObject.getInt("numAttn");
//				attendingStatus (status of user wrt attending)
	    		} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
	    	}else{
	    		// TODO if event doesn't exist.
	    		return false;
	    	}
	    	return true;
    	}
    	Log.d("ViewEventActivity","Loading fake data");
		HostID 			= "1234";
		EventName 		= "Timeout Event";
		Sport 			= "Timeout Sport";
		DateTime 		= "2014-12-25 12:34:56";
		PeopleRequired 	= 10;
		PeopleAttending = 5;
		// TODO return false
		return true;
	}
	
	@Override
	public void onSaveInstanceState(Bundle savedInstanceState) {
		// Save stuff..
		savedInstanceState.putBoolean("EventDataPresent", EventDataPresent);
	    savedInstanceState.putString("EventID", EventID);
	    savedInstanceState.putBoolean("Admin", Admin);
		if(EventDataPresent){
		    savedInstanceState.putString("HostID", HostID);
		    savedInstanceState.putString("EventName", EventName);
		    savedInstanceState.putString("Sport", Sport);
		    savedInstanceState.putString("DateTime", DateTime);
		    savedInstanceState.putShort("PeopleRequired", PeopleRequired);
		    savedInstanceState.putShort("PeopleAttending", PeopleAttending);
		}
		
	    super.onSaveInstanceState(savedInstanceState);
	}

}
