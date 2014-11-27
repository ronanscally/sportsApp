package com.example.sportsapp;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.facebook.UiLifecycleHelper;
import com.google.android.gms.maps.model.LatLng;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class ViewEventFragment extends Fragment {

	private static final String TAG = "ViewEventsFragment";
	private String UserID;
	private String EventID;
	private String HostID;
	private String EventName;
	private String Sport;
	private String StartTime;
	private String EndTime;
	private LatLng Location_coords;
	private String Location_address;
	private short PeopleRequired;
	private short PeopleAttending;
	private Button backButton;
	
	private ButtonPressedCallback backPressedCallback;
	private TextView text_eventName;
	private TextView text_eventStartTime;
	private TextView text_eventEndTime;
	private TextView text_eventSport;
	private TextView text_peopleAttending;
	private TextView text_peopleRequired;
	private TextView text_location;
	private Button showMapButton;
	private Button inviteButton;
	
	private Button acceptButton;
	private Button declineButton;
	private Button joinButton;
	private RelativeLayout attendenceButtonTab;
	private int attendingStatus = 0;
	private Button deleteButton;
	private RelativeLayout deleteTab;
	
    public interface ButtonPressedCallback {
        void onButtonPressed(String id);
    }
    
    public void setBackCallback(ButtonPressedCallback callback) {
    	backPressedCallback = callback;
    }
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        activity = (MainActivity) getActivity();
//        uiHelper = new UiLifecycleHelper(getActivity(), sessionCallback);
//        uiHelper.onCreate(savedInstanceState);
        
        Log.d("TAG","onCreateCalled");
    }

    @Override
    public void onResume() {
        super.onResume();
//        uiHelper.onResume();
    }
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		Log.d(TAG,"OnCreateView called");
        View view = inflater.inflate(R.layout.view_event, container, false);
		
		// TODO saved instance state
		Intent intent = getActivity().getIntent();
	    UserID 		= intent.getStringExtra(R.string.EXTRA_PREFIX + "userID");
	    
	    text_eventName 				= (TextView) view.findViewById(R.id.eventName);
    	text_eventStartTime 		= (TextView) view.findViewById(R.id.eventStartTime);
    	text_eventEndTime			= (TextView) view.findViewById(R.id.eventEndTime);
    	text_eventSport 			= (TextView) view.findViewById(R.id.eventSport);
    	text_peopleAttending 		= (TextView) view.findViewById(R.id.eventNumAttending);
    	text_peopleRequired 		= (TextView) view.findViewById(R.id.eventNumRequired);
    	text_location				= (TextView) view.findViewById(R.id.eventLocation);
    	
    	attendenceButtonTab			= (RelativeLayout)  view.findViewById(R.id.attendenceTab);
    	deleteTab					= (RelativeLayout)  view.findViewById(R.id.deleteTab);
    	
    	
    	showMapButton = (Button) view.findViewById(R.id.show_map_button);
    	showMapButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            	Intent intent = new Intent(getActivity(), ShowMap.class);
            	intent.putExtra(R.string.EXTRA_PREFIX + "lat", Location_coords.latitude);
            	intent.putExtra(R.string.EXTRA_PREFIX + "lng", Location_coords.longitude);
            	intent.putExtra(R.string.EXTRA_PREFIX + "EventName", EventName);
            	startActivity(intent);
            }
        });
    	
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
    	
    	inviteButton	= (Button)	view.findViewById(R.id.inviteButton);
    	inviteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            	Intent intent = new Intent(getActivity(), InviteFriends.class);
            	intent.putExtra(R.string.EXTRA_PREFIX + "userID", UserID);
            	String eventID = Events.getEventID();
            	Log.d(TAG,"EventID: "+eventID);
            	intent.putExtra(R.string.EXTRA_PREFIX + "eventID", eventID);
            	startActivity(intent);
            }
        });
    	
    	
    	acceptButton = (Button) view.findViewById(R.id.acceptButton);
    	acceptButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            	// Set user to attending
            	setUserAttending(UserID, Events.getEventID(), true);
            	// Set button bar invisible
            	attendenceButtonTab.setVisibility(View.GONE); 
            	// Show attending status
            }
        });
    	
    	declineButton = (Button) view.findViewById(R.id.declineButton);
    	declineButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            	// Set user to not attending
            	setUserAttending(UserID, Events.getEventID(), false);
            	// Set button bar invisible
            	attendenceButtonTab.setVisibility(View.GONE); 
            	// Show attending status
            }
        });
    	
    	joinButton = (Button) view.findViewById(R.id.joinButton);
    	joinButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            	// Add user to events participants list
            	addUserToEvent(UserID,Events.getEventID());
            	// Set user to attending
            	setUserAttending(UserID, Events.getEventID(), true);
            	// Set button invisible
            	joinButton.setVisibility(View.GONE); 
            	// Show attending status
            }
        });
    	
    	deleteButton = (Button) view.findViewById(R.id.deleteButton);
    	deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            	String eventID = Events.getEventID();
            	deleteEvent(eventID);
            	backButton.callOnClick();
            }
        });
    	
		return view;
        
        
	}
	
	protected void deleteEvent(String eventID) {
		JSONfunctions.clearResponseBuffer();
//    	String message 		= null;
//    	int success 		= 0;
    	
    	JSONObject request = new JSONObject();
    	 
    	try {
    		request.put("eventID", eventID);
		} catch (JSONException e1) {
			e1.printStackTrace();
		}
    	Log.d(TAG,"Request: " + request.toString());
    	

    	JSONfunctions.setRequestObject(request);
    	String url = getString(R.string.deleteEvent);
    	Log.d(TAG,"Url: " + url);
    	new JSONfunctions().execute(url);
    	
    	long timeStart = System.currentTimeMillis();
//    	boolean timeout = false;
    	int timeoutSeconds = 10;
//    	JSONArray responseArray = null;
    	while (true){
    		if(timeStart + timeoutSeconds*1000 < System.currentTimeMillis()){	// Timeout (10seconds...)
    			Log.d(TAG,"No server response.");
    			Log.d(TAG,"Timeout triggered after " + timeoutSeconds + " seconds");
//    			timeout = true;
    			break;
    		}
    		
    		if(JSONfunctions.checkNewResponse()){
//    			responseArray = JSONfunctions.getResponseArray();
    			break;
    		}
    	}
	}

	protected void addUserToEvent(String userID, String eventID) {
		JSONfunctions.clearResponseBuffer();
//    	String message 		= null;
//    	int success 		= 0;
    	
    	JSONObject request = new JSONObject();
    	Log.d(TAG,"eventID: "+eventID);
    	try {
			request.put("eventID", eventID);
			request.put("userID", userID);
//			request.put("sendNotification", true); // Don't send notification
		} catch (JSONException e1) {
			e1.printStackTrace();
		}
    	Log.d(TAG ,"Request: " + request.toString());
    	

    	JSONfunctions.setRequestObject(request);
    	String url = getString(R.string.inviteUser);
    	Log.d(TAG,"Url: " + url);
    	new JSONfunctions().execute(url);

    	// TODO more with timeout error... (make global?)
    	long timeStart = System.currentTimeMillis();
//    	boolean timeout = false;
    	int timeoutSeconds = 10;
//    	JSONArray responseArray = null;
    	while (true){
    		if(timeStart + timeoutSeconds*1000 < System.currentTimeMillis()){	// Timeout (10seconds...)
    			Log.d(TAG,"No server response.");
    			Log.d(TAG,"Timeout triggered after " + timeoutSeconds + " seconds");
//    			timeout = true;
    			break;
    		}
    		if(JSONfunctions.checkNewResponse()){
//    			responseArray = JSONfunctions.getResponseArray();
    			break;
    		}
    	}
	}

	protected void setUserAttending(String userID, String eventID, boolean attending) {
		JSONfunctions.clearResponseBuffer();
//    	String message 		= null;
//    	int success 		= 0;
    	
    	JSONObject request = new JSONObject();
    	 
    	try {
    		request.put("userID", userID);
    		request.put("eventID", eventID);
    		if(attending)
    			request.put("attendingStatus", 2);
    		else
    			request.put("attendingStatus", 1);
		} catch (JSONException e1) {
			e1.printStackTrace();
		}
    	Log.d(TAG,"Request: " + request.toString());
    	

    	JSONfunctions.setRequestObject(request);
    	String url = getString(R.string.setAttending);
    	Log.d(TAG,"Url: " + url);
    	new JSONfunctions().execute(url);
    	
    	long timeStart = System.currentTimeMillis();
//    	boolean timeout = false;
    	int timeoutSeconds = 10;
//    	JSONArray responseArray = null;
    	while (true){
    		if(timeStart + timeoutSeconds*1000 < System.currentTimeMillis()){	// Timeout (10seconds...)
    			Log.d(TAG,"No server response.");
    			Log.d(TAG,"Timeout triggered after " + timeoutSeconds + " seconds");
//    			timeout = true;
    			break;
    		}
    		
    		if(JSONfunctions.checkNewResponse()){
//    			responseArray = JSONfunctions.getResponseArray();
    			break;
    		}
    	}
	}

	public void setDisplayData(JSONObject eventJSONObject){
		try{
			Log.d(TAG,"Setting display data");
			HostID 			= 			eventJSONObject.getString("hostID");
			EventName 		= 			eventJSONObject.getString("eventName");
			Sport 			= 			eventJSONObject.getString("sport");
			StartTime 		= 			eventJSONObject.getString("startTime");
			EndTime 		= 			eventJSONObject.getString("endTime");
			PeopleRequired 	= (short) 	eventJSONObject.getInt("numReqd");
			PeopleAttending = (short) 	eventJSONObject.getInt("numAttn");
			
			double location_lng	= 			eventJSONObject.getDouble("lng");
			double location_lat	= 			eventJSONObject.getDouble("lat");
			Location_coords = new LatLng(location_lng,location_lat);
			//TODO find address
			if(location_lat == 53.310376){
				Location_address = "UCD Bowl";
			}else if(location_lat == 53.308328){
				Location_address = "UCD Sports Hall B";
			}else if(location_lat == 53.308819){
				Location_address = "UCD Swimming Pool";
			}else if(location_lat == 53.327435){
				Location_address = "Herbert Park";
			}else if(location_lat ==54.307183){
				Location_address = "Rosses Point Golf Club (Sligo)";
			}else{
				Location_address = "Unable to find location";
			}
			
			String attn = eventJSONObject.getString("attnStatus");
			Log.d(TAG,attn);
			if(attn == null){
				attendingStatus = -1;
			}else if(attn.equals("1")){
				attendingStatus = 1;
			}
			else if(attn.equals("2")){
				attendingStatus = 2;
			}
			else if(attn.equals("0")){
				attendingStatus = 0;
			}else{
				attendingStatus = -1;
			}
		}catch(JSONException e){
			e.printStackTrace();
		}
		updateDisplayData();
	}
	
	private void updateDisplayData() {
		Log.d(TAG,"Updating display data");
		try{
			Log.d(TAG,"Updating display data");
			text_eventName.setText(EventName);
			text_eventStartTime.setText(StartTime);
			text_eventEndTime.setText(EndTime);
			text_eventSport.setText(Sport);
			text_peopleAttending.setText(String.valueOf(PeopleAttending));
			text_peopleRequired.setText(String.valueOf(PeopleRequired));
			text_location.setText(Location_address);
			
			attendenceButtonTab.setVisibility(View.GONE);
			joinButton.setVisibility(View.GONE);
			deleteTab.setVisibility(View.GONE);
			try{
			if(HostID.equals(UserID)){
				deleteTab.setVisibility(View.VISIBLE);
			}
			}catch(Exception e){
				Log.e(TAG,e.toString());
			}
			
			// TODO more stuff here...
			Log.d(TAG,"Attending: " + attendingStatus);
			switch(attendingStatus){
			case -1:
				joinButton.setVisibility(View.VISIBLE);
				break;
			case 0:
				attendenceButtonTab.setVisibility(View.VISIBLE);
				break;
			case 1:
			case 2:
			default:
			}
		}catch(Exception e){
			Log.d(TAG,"Could not update display data");
		}
	}
}
