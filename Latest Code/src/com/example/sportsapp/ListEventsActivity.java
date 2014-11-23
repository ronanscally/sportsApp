package com.example.sportsapp;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class ListEventsActivity extends ActionBarActivity {

	String UserID = null;
	List<String> EventNames = new ArrayList<String>();
	List<String> EventIDs = new ArrayList<String>();
	
	private ListView lv;
	List<String> Event_string_array_list = new ArrayList<String>();
	private boolean EventDataPresent;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_list_events);
	Log.d("Oncreate","Function called...");
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        // If your minSdkVersion is 11 or higher, instead use:
        // getActionBar().setDisplayHomeAsUpEnabled(true);
		
		if (savedInstanceState != null) {
			UserID 				= savedInstanceState.getString("EventID");
			EventDataPresent 	= savedInstanceState.getBoolean("EventDataPresent");
		    EventNames 			= savedInstanceState.getStringArrayList("EventNames");
		    EventIDs 			= savedInstanceState.getStringArrayList("EventIDs");
		}else{
			Intent intent = getIntent();
		    UserID 		= intent.getStringExtra(R.string.EXTRA_PREFIX + "userID");
		    Log.d("ListEventsActivity","User ID: " + UserID);
		    EventDataPresent = getEventsList();
		}
	    
		lv = (ListView) findViewById(R.id.listView);
        

        // This is the array adapter, it takes the context of the activity as a 
        // first parameter, the type of list view as a second parameter and your 
        // array as a third parameter.
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(
                this, 
//                android.R.layout.simple_list_item_1,
                R.layout.event_list_item,
                EventNames );

        lv.setAdapter(arrayAdapter); 
        
     // Bind onclick event handler
        lv.setOnItemClickListener(new OnItemClickListener() {
					@Override
					public void onItemClick(AdapterView<?> parent, View view,
							int position, long id) {
						
						Log.d("List","position = " + String.valueOf(position));
						Log.d("List","id 	   = " + String.valueOf(id));
						
						viewEvent(view, position);
						
					}
        });
	}
	
	public void createEvent(View view) {
    	Intent intent = new Intent(this, CreateEventActivity.class);
    	startActivity(intent);
    }
	
	public void viewEvent(View view, int position) {
    	Intent intent = new Intent(this, ViewEventActivity.class);
    	String eventID = EventIDs.get(position);
    	intent.putExtra(R.string.EXTRA_PREFIX + "eventID", eventID);
    	intent.putExtra(R.string.EXTRA_PREFIX + "userID", UserID);
    	startActivity(intent);
    }
	
	
	public boolean getEventsList() {
    	
    	String message 		= null;
    	int success 		= 0;
    	
    	JSONObject request = new JSONObject();
    	 
    	try {
			request.put("userID", UserID);
		} catch (JSONException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
    	Log.d("ListEventsActivity","Request: " + request.toString());
    	
    	JSONfunctions.setRequestObject(request);
    	new JSONfunctions().execute(getString(R.string.listEvents));

    	// TODO more with timeout error... (make global?)
    	long timeStart = System.currentTimeMillis();
    	boolean timeout = false;
    	int timeoutSeconds = 10;
    	JSONArray responseArray = null;
    	while (true){
    		if(timeStart + timeoutSeconds*1000 < System.currentTimeMillis()){	// Timeout (10seconds...)
    			Log.d("Server","No server response.");
    			Log.d("Server","Timeout triggered after " + timeoutSeconds + " seconds");
    			timeout = true;
    			break;
    		}
    		if(JSONfunctions.checkNewResponse()){
    			responseArray = JSONfunctions.getResponseArray();
    			break;
    		}
    	}

    	if(!timeout){
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
	    	
	    	Log.d("ListEventsActivity","Message: " + message);
	    	
	    	if(success == 1) // Events exists, get data
	    	{
	    		for (int i = 0; i < responseArray.length(); i++) {
	    			try {
						responseObject = responseArray.getJSONObject(i);
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
		    	    Log.d("mJsonObject",responseObject.toString());
		    	    try {
			    		EventIDs.add(responseObject.getString("eventID"));
						EventNames.add(responseObject.getString("eventName"));
		    		} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
		    	}
	    	}else{
	    		// TODO if user profile doesn't exist.
	    		return false;
	    	}
	    	return true;
    	}
		return false;
	}
	
	
	@Override
	public void onSaveInstanceState(Bundle savedInstanceState) {
		// Save stuff..
		savedInstanceState.putString("UserID", UserID);
		savedInstanceState.putBoolean("EventDataPresent", EventDataPresent);
		if(EventDataPresent){
		    savedInstanceState.putStringArrayList("EventNames", (ArrayList<String>) EventNames);
		    savedInstanceState.putStringArrayList("EventIDs", (ArrayList<String>) EventIDs);
		}
		
	    super.onSaveInstanceState(savedInstanceState);
	}
}
