package com.example.sportsapp;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.example.sportsapp.ListEventsFragment.ButtonPressedCallback;
import com.facebook.UiLifecycleHelper;

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
	private String EventID;
	private String HostID;
	private String EventName;
	private String Sport;
	private String DateTime;
	private short PeopleRequired;
	private short PeopleAttending;
	private TextView text_eventName;
	private TextView text_eventTime;
	private TextView text_eventSport;
	private TextView text_peopleRequired;
	private TextView text_peopleAttending;
	private Button backButton;
	
	private ButtonPressedCallback backPressedCallback;

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
	    EventID 	= Events.getEventID();
	    
	    text_eventName 			= (TextView) view.findViewById(R.id.eventName);
    	text_eventTime 			= (TextView) view.findViewById(R.id.eventTime);
    	text_eventSport 			= (TextView) view.findViewById(R.id.eventSport);
    	text_peopleRequired 		= (TextView) view.findViewById(R.id.peopleRequired);
    	text_peopleAttending 		= (TextView) view.findViewById(R.id.peopleAttending);
    	
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
		
		return view;
        
        
	}
	
	public void setDisplayData(JSONObject eventJSONObject){
		// TODO seperate this into two methods
		try{
		HostID 			= 			eventJSONObject.getString("hostID");
		EventName 		= 			eventJSONObject.getString("eventName");
		Sport 			= 			eventJSONObject.getString("sport");
		DateTime 			= 			eventJSONObject.getString("date");
		PeopleRequired 	= (short) 	eventJSONObject.getInt("numReqd");
		PeopleAttending 	= (short) 	eventJSONObject.getInt("numAttn");
		}catch(JSONException e){
			e.printStackTrace();
		}
		Log.d(TAG,"Setting Display Data");
		text_eventName.setText(HostID);
    	text_eventTime.setText(EventName);
    	text_eventSport.setText(Sport);
    	text_peopleRequired.setText(String.valueOf(PeopleRequired));
    	text_peopleAttending.setText(String.valueOf(PeopleAttending));
	}
	
	
}
