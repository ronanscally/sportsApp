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
import android.widget.TextView;

public class ViewEventFragment extends Fragment {

	private static final String TAG = "ViewEventsFragment";
	private String UserID;
//	private String EventID;
//	private String HostID;
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
    	
    	
		return view;
        
        
	}
	
	public void setDisplayData(JSONObject eventJSONObject){
		try{
			Log.d(TAG,"Setting display data");
//			HostID 			= 			eventJSONObject.getString("hostID");
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
			Location_address = "Need to find the address...";
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
		}catch(Exception e){
			Log.d(TAG,"Could not update display data");
		}
	}
}
