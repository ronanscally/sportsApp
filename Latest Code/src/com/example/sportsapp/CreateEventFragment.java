package com.example.sportsapp;


import org.json.JSONObject;

import com.example.sportsapp.ViewEventFragment.ButtonPressedCallback;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

public class CreateEventFragment extends Fragment {

	static final String TAG = "CreateEventFragment";
	private TextView text_eventName;
	private Spinner spinner_sport;
	private TextView text_startDate;
	private TextView text_startTime;
	private TextView text_endDate;
	private TextView text_endTime;
	private TextView text_playersRequired;
	private Spinner spinner_locationAddress;
	
	private JSONObject EventObject = new JSONObject();
	private Button backButton;
	private ButtonPressedCallback backPressedCallback;
	private Button saveButton;
//	private ArrayAdapter<CharSequence> adapter;
	private ArrayAdapter<CharSequence> adapter_location;
	private ArrayAdapter<CharSequence> adapter_sport;
	
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
		spinner_sport			= (Spinner) 	view.findViewById(R.id.sport);
		text_startDate			= (TextView) 	view.findViewById(R.id.startDate);
		text_startTime			= (TextView) 	view.findViewById(R.id.startTime);
		text_endDate			= (TextView) 	view.findViewById(R.id.endDate);
		text_endTime			= (TextView) 	view.findViewById(R.id.endTime);
		text_playersRequired	= (TextView) 	view.findViewById(R.id.playersRequired);
		spinner_locationAddress	= (Spinner) 	view.findViewById(R.id.locationAddress);
		
		adapter_location = ArrayAdapter.createFromResource(getActivity(),
		        R.array.event_locations, android.R.layout.simple_spinner_item);
		// Specify the layout to use when the list of choices appears
		adapter_location.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		// Apply the adapter to the spinner
		spinner_locationAddress.setAdapter(adapter_location);
		
		adapter_sport = ArrayAdapter.createFromResource(getActivity(),
		        R.array.sport_names, android.R.layout.simple_spinner_item);
		// Specify the layout to use when the list of choices appears
		adapter_sport.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		// Apply the adapter to the spinner
		spinner_sport.setAdapter(adapter_sport);
		
		
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
	
	private void Log(String tag2, String string) {
		// TODO Auto-generated method stub
		
	}

	private void saveEvent(){
		if(getUserInputData()){
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

	private boolean getUserInputData() {
		try{
			// Create object from text data...
			Log.d(TAG,"Creating object");
			String UserID = Events.UserID;
			EventObject.put("userID", UserID);
			Log.d(TAG,"HostID set");
			EventObject.put("title", text_eventName.getText().toString());
			
			String sport = spinner_sport.getSelectedItem().toString();  
			
			EventObject.put("sport", sport);
			EventObject.put("numReqd", text_playersRequired.getText().toString());
			Log.d(TAG,"User entries in");
			// Date and time, start and end
			String startTime = "2014-12-21 15:00:00";
			String endTime = "2014-12-21 18:00:00";
			EventObject.put("startTime", startTime);
			EventObject.put("endTime", endTime);
			Log.d(TAG,"Times added");
			// Location
			String address = spinner_locationAddress.getSelectedItem().toString();
			if(address.equals("UCD Bowl")){
				EventObject.put("lng", -6.228134);
				EventObject.put("lat", 53.310376);
			}else if(address.equals("UCD Sports Hall B")){
				EventObject.put("lng", -6.229142);
				EventObject.put("lat", 53.308328);
			}else if(address.equals("UCD Swimming Pool")){
				EventObject.put("lng", -6.228074);
				EventObject.put("lat", 53.308819);
			}else if(address.equals("Herbert Park")){
				EventObject.put("lng", -6.235490);
				EventObject.put("lat", 53.327435);
			}else if(address.equals("Rosses Point Golf Club (Sligo)")){
				EventObject.put("lng", -8.565783);
				EventObject.put("lat", 54.307183);
			}else{
				EventObject.put("lng", 0.0);
				EventObject.put("lat", 0.0);
			}
			Log.d(TAG,"Location set");
			return true;
		}catch(Exception e){
			Log.d(TAG,"Failed to get inputed data and set to object");
		}
		return false;
	}
}