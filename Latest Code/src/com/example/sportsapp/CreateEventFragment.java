package com.example.sportsapp;


import org.json.JSONObject;

import com.example.sportsapp.ViewEventFragment.ButtonPressedCallback;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

public class CreateEventFragment extends Fragment {

	static final String TAG = "CreateEventFragment";
	private TextView text_eventName;
	private TextView text_sport;
	private TextView text_startDate;
	private TextView text_startTime;
	private TextView text_endDate;
	private TextView text_endTime;
	private TextView text_playersRequired;
	private TextView text_locationAddress;
	
	private JSONObject EventObject = new JSONObject();
	private Button backButton;
	private ButtonPressedCallback backPressedCallback;
	private Button saveButton;
	
	public interface ButtonPressedCallback {
        void onButtonPressed(String id);
    }
    
    public void setBackCallback(ButtonPressedCallback callback) {
    	backPressedCallback = callback;
    }

	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		View view = inflater.inflate(R.layout.create_event, container, false);
		
		
		text_eventName			= (TextView) 	view.findViewById(R.id.eventName);
		text_sport				= (TextView) 	view.findViewById(R.id.sport);
		text_startDate			= (TextView) 	view.findViewById(R.id.startDate);
		text_startTime			= (TextView) 	view.findViewById(R.id.startTime);
		text_endDate			= (TextView) 	view.findViewById(R.id.endDate);
		text_endTime			= (TextView) 	view.findViewById(R.id.endTime);
		text_playersRequired	= (TextView) 	view.findViewById(R.id.playersRequired);
		text_locationAddress	= (TextView) 	view.findViewById(R.id.locationAddress);
		
		backButton 			= (Button) 			view.findViewById(R.id.backButton);
    	backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (backPressedCallback != null) {
                	Log.d(TAG,"onClick");
                	backPressedCallback.onButtonPressed("Back");
                }
            }
        });
    	
    	saveButton 			= (Button) 			view.findViewById(R.id.saveButton);
    	saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            	saveEvent();
            }
        });
		
    	
		return view;

	}
	
	private void saveEvent(){
		if(getUserData()){
			// Clear buffer
			JSONfunctions.clearResponseBuffer();
			JSONObject request = EventObject;
			Log.d(TAG,"Request: " + request.toString());
	    	

	    	JSONfunctions.setRequestObject(request);
	    	String url = getString(R.string.createEvent);
	    	Log.d(TAG,"Url: " + url);
	    	new JSONfunctions().execute(url);

	    	// TODO more with timeout error... (make global?)
	    	long timeStart = System.currentTimeMillis();
//	    	boolean timeout = false;
	    	int timeoutSeconds = 10;
//	    	JSONArray responseArray = null;
	    	while (true){
	    		if(timeStart + timeoutSeconds*1000 < System.currentTimeMillis()){	// Timeout (10seconds...)
	    			Log.d(TAG,"No server response.");
	    			Log.d(TAG,"Timeout triggered after " + timeoutSeconds + " seconds");
//	    			timeout = true;
	    			break;
	    		}
	    		
	    		if(JSONfunctions.checkNewResponse()){
//	    			responseArray = JSONfunctions.getResponseArray();
	    			break;
	    		}
	    	}
			return;
		}else{
			// Display alert message
		}
	}

	private boolean getUserData() {
		try{
			// Create object from text data...
			Log.d(TAG,"Creating object");
			String UserID = "908862745805631";
//			String UserID = Events.UserID;
			EventObject.put("userID", UserID);
			Log.d(TAG,"HostID set");
			EventObject.put("title", text_eventName.getText().toString());
			EventObject.put("sport", text_sport.getText().toString());
			EventObject.put("numReqd", text_playersRequired.getText().toString());
			Log.d(TAG,"User entries in");
			// Date and time, start and end
			String startTime = "2014-12-21 15:00:00";
			String endTime = "2014-12-21 18:00:00";
			EventObject.put("startTime", startTime);
			EventObject.put("endTime", endTime);
			Log.d(TAG,"Times added");
			// Location
			EventObject.put("lat", -6.21683350000001);
			EventObject.put("lng", 53.3052276);
			Log.d(TAG,"Location set");
			return true;
		}catch(Exception e){
			Log.d(TAG,"Failed to get inputed data and set to object");
		}
		return false;
	}
}