package com.example.sportsapp;


import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.facebook.model.OpenGraphAction;


import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

public class ListEventsFragment extends Fragment {

	private static final String TAG = "ListEventsFragment";
	
	private Spinner eventUserStatusSpinner;
	private ArrayAdapter<CharSequence> adapter;
	private Button nearbyButton;
	private Button yourEventsButton;
	private Button createButton;
	private RelativeLayout buttonBar;
	private ListView listView;
	private TextView noEventsText;
	private List<BaseListElement> listElements;
	private String UserID;
	GPSTracker gps;
	private double latitude = 0;
	private double longitude = 0;

	private JSONArray UserEventsJSONArray;
	private boolean UserEventsPresent = false;
	private JSONArray NearbyEventsJSONArray;
	private boolean NearbyEventsPresent = false;
	
	private ButtonPressedCallback createPressedCallback;
	private ButtonPressedCallback viewEventPressedCallback;
	
	private static final String INVITED = "0";
	private static final String DECLINED = "1";
	private static final String ATTENDING = "2";
	private String ATTEND_STATUS = ATTENDING;

    public interface ButtonPressedCallback {
        void onButtonPressed(String id);
    }
    
    public void setCreateCallback(ButtonPressedCallback callback) {
    	createPressedCallback = callback;
    }
    
    public void setViewEventCallback(ButtonPressedCallback callback) {
    	viewEventPressedCallback = callback;
    }
		
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.listevents, container, false);
	
//		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        // If your minSdkVersion is 11 or higher, instead use:
        // getActionBar().setDisplayHomeAsUpEnabled(true);
		
		
		// TODO saved instance state
		Intent intent = getActivity().getIntent();
	    UserID 		= intent.getStringExtra(R.string.EXTRA_PREFIX + "userID");
	    getUserEvents();
	    
	    ATTEND_STATUS = ATTENDING;
		
		
		eventUserStatusSpinner 	= (Spinner) 		view.findViewById(R.id.eventUserStatusSpinner);
		nearbyButton 			= (Button) 			view.findViewById(R.id.nearbyButton);
		yourEventsButton 		= (Button) 			view.findViewById(R.id.yourEventsButton);
		createButton 			= (Button) 			view.findViewById(R.id.createButton);
		buttonBar 				= (RelativeLayout) 	view.findViewById(R.id.buttonBar);
		listView 				= (ListView) 		view.findViewById(R.id.eventsList);
		noEventsText			= (TextView) 		view.findViewById(R.id.noEventsText);
		
		
		
		GPSTracker gps = new GPSTracker(this.getActivity());		 
	    latitude =  gps.getLatitude();
		longitude = gps.getLongitude();
		
//		nearbyButton.setText("Nearby Events");
		
		adapter = ArrayAdapter.createFromResource(getActivity(),
		        R.array.events_type, android.R.layout.simple_spinner_item);
		// Specify the layout to use when the list of choices appears
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		// Apply the adapter to the spinner
		eventUserStatusSpinner.setAdapter(adapter);
		eventUserStatusSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				Log.d(TAG,"Spinner position: +" + position);
				switch(position){
				case 0: 
					ATTEND_STATUS = ATTENDING;
					break;
				case 1: 
					ATTEND_STATUS = INVITED;
					break;
				case 2: 
					ATTEND_STATUS = DECLINED;
					break;	
				}
				updateList();
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {
				// TODO Auto-generated method stub
				Log.d(TAG,"Nothing Selected on Spinner");
			}
		});
		
		yourEventsButton.setVisibility(View.GONE);

		
		
		nearbyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            	viewNearbyEvents();
            }
        });
		
		yourEventsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            	yourEventsButton.setVisibility(View.GONE);
            	nearbyButton.setVisibility(View.VISIBLE);
            	buttonBar.setVisibility(View.VISIBLE);
            	getUserEvents();
            	updateList();
            }
        });
		
		createButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (createPressedCallback != null) {
                	Log.d(TAG,"onClick");
                	createPressedCallback.onButtonPressed("Create");
                }
            }
        });
		
		
        updateList();
        
        if (savedInstanceState != null) {
            for (BaseListElement listElement : listElements) {
                listElement.restoreState(savedInstanceState);
            }
        }
        
        
		return view;
        
        
	}
	
	protected void updateList() {
		try{
			// Attach the events to the list
	    	noEventsText.setVisibility(View.GONE);
			listView.setVisibility(View.GONE);
			listElements = new ArrayList<BaseListElement>();
			if(UserEventsPresent){ // Add events to list if present
				for (int i = 0; i < UserEventsJSONArray.length(); i++) {
					try {
						if(UserEventsJSONArray.getJSONObject(i).getString("attnStatus").equals(ATTEND_STATUS)){
							listElements.add(new EventListElement(UserEventsJSONArray.getJSONObject(i)));
						}
					} catch (JSONException e) {
						e.printStackTrace();
					}
				}
				listView.setVisibility(View.VISIBLE);
			}else{
				// Set no events element to be visible
				noEventsText.setVisibility(View.VISIBLE);
			}
			listView.setAdapter(new ActionListAdapter(getActivity(), R.id.eventsList, listElements));
		}catch(Exception e){
			Log.e(TAG,e.toString());
		}
	}

	protected void viewNearbyEvents() {
    	yourEventsButton.setVisibility(View.VISIBLE);
    	nearbyButton.setVisibility(View.GONE);
    	buttonBar.setVisibility(View.GONE);
		// Send location to server
    	sendLocationToServer();
    	// Get nearby events
    	getNearbyEvents();
//    	removeCommonEvents();
    	// Attach the events to the list
    	noEventsText.setVisibility(View.GONE);
		listView.setVisibility(View.GONE);
		listElements = new ArrayList<BaseListElement>();
		if(NearbyEventsPresent){ // Add events to list if present
			for (int i = 0; i < NearbyEventsJSONArray.length(); i++) {
				try {
					listElements.add(new EventListElement(NearbyEventsJSONArray.getJSONObject(i)));
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
			listView.setVisibility(View.VISIBLE);
		}else{
			// Set no events element to be visible
			noEventsText.setVisibility(View.VISIBLE);
		}
		listView.setAdapter(new ActionListAdapter(getActivity(), R.id.eventsList, listElements));
	}

	private void removeCommonEvents() {
		// TODO Auto-generated method stub UserEventsJSONArray, NearbyEventsJSONArray
		if(UserEventsJSONArray == null) return;
		if(NearbyEventsJSONArray == null) return;
		ArrayList<String> nearbyEventIDs = new ArrayList<String>();      
	    for (int i = 0; i < NearbyEventsJSONArray.length(); i++){ 
	    	try {
				nearbyEventIDs.add(NearbyEventsJSONArray.getJSONObject(i).getString("eventID").toString());
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	    } 
	    
	    String userEventID;
	    int j = -1;
	    for (int i = 0; i < NearbyEventsJSONArray.length(); i++){ 
	    	try {
				userEventID = NearbyEventsJSONArray.getJSONObject(i).getString("eventID").toString();
				j = nearbyEventIDs.indexOf(userEventID);
				if(j != -1){
					nearbyEventIDs.remove(j);
//					NearbyEventsJSONArray.remove(j);
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	    } 
	    
	}

	private boolean getNearbyEvents() {
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
    	Log.d(TAG,"Request: " + request.toString());
    	
    	JSONfunctions.setRequestObject(request);
    	new JSONfunctions().execute(getString(R.string.getNearbyEvents));

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
    		if(responseArray == null){
        		Log.d(TAG,"Null response recieved");
        		NearbyEventsJSONArray = responseArray;
        		NearbyEventsPresent = false;
        		return true;
        	}else{
        		Log.d(TAG,responseArray.toString());
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
		    	
		    	if(success == 1) // Events exists, get data
		    	{
		    		NearbyEventsJSONArray = responseArray;
		    		NearbyEventsPresent = true;
		    	}else{
		    		// TODO if events doesn't exist.
		    		return false;
		    	}
		    	return true;
        	}
    	}
		return false;
	}

	private void sendLocationToServer() {
		try{
			JSONfunctions.clearResponseBuffer();
			JSONObject request = new JSONObject();
	    	request.put("userID", UserID);
			request.put("lng", longitude);
			request.put("lat", latitude);
	    	JSONfunctions.setRequestObject(request);
	    	String save_location = getString(R.string.saveLocation);
	    	new JSONfunctions().execute(save_location);
	    	
	    	long timeStart = System.currentTimeMillis();
//	    	boolean timeout = false;
	    	int timeoutSeconds = 10;
//	    	JSONArray responseArray = null;
	    	while (true){
	    		if(timeStart + timeoutSeconds*1000 < System.currentTimeMillis()){	// Timeout (10seconds...)
	    			Log.d("Server","No server response.");
	    			Log.d("Server","Timeout triggered after " + timeoutSeconds + " seconds");
//	    			timeout = true;
	    			break;
	    		}
	    		if(JSONfunctions.checkNewResponse()){
//	    			responseArray = JSONfunctions.getResponseArray();
	    			break;
	    		}
	    	}
	    	
		}catch(JSONException e){
			System.out.println("Send Location to server");
			e.printStackTrace();
		}catch(Exception e){
			System.out.println("General Error");
			e.printStackTrace();
			
		}
	}

	public void createGroup(View view) {
    	Intent intent = new Intent(getActivity(), CreateGroupActivity.class);
    	startActivity(intent);
    }
	
	/*
	public void viewGroup(View view, int position) {
    	Intent intent = new Intent(this, ViewGroupActivity.class);
    	String groupID = Group_string_array_list.get(position);
    	intent.putExtra(R.string.EXTRA_PREFIX + "groupID", groupID);
    	startActivity(intent);
    }
    */
	
	private class ActionListAdapter extends ArrayAdapter<BaseListElement> {
        private List<BaseListElement> listElements;

        public ActionListAdapter(Context context, int resourceId, List<BaseListElement> listElements) {
            super(context, resourceId, listElements);
            this.listElements = listElements;
            for (int i = 0; i < listElements.size(); i++) {
                listElements.get(i).setAdapter(this);
            }
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view = convertView;
            if (view == null) {
                LayoutInflater inflater =
                        (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                view = inflater.inflate(R.layout.listitem, null);
            }

            BaseListElement listElement = listElements.get(position);
            if (listElement != null) {
                view.setOnClickListener(listElement.getOnClickListener());
                ImageView icon = (ImageView) view.findViewById(R.id.icon);
                TextView text1 = (TextView) view.findViewById(R.id.text1);
                TextView text2 = (TextView) view.findViewById(R.id.text2);
                TextView text3 = (TextView) view.findViewById(R.id.text3);
                TextView text4 = (TextView) view.findViewById(R.id.text4);
                if (icon != null) {
                    icon.setImageDrawable(listElement.getIcon());
                }
                if (text1 != null) {
                    text1.setText(listElement.getText1());
                }
                if (text2 != null) {
                    if (listElement.getText2() != null) {
                        text2.setVisibility(View.VISIBLE);
                        text2.setText(listElement.getText2());
                    } else {
                        text2.setVisibility(View.GONE);
                    }
                }
                if (text3 != null) {
                    if (listElement.getText3() != null) {
                        text3.setVisibility(View.VISIBLE);
                        text3.setText(listElement.getText3());
                    } else {
                        text3.setVisibility(View.GONE);
                    }
                }
                if (text4 != null) {
                    if (listElement.getText4() != null) {
                        text4.setVisibility(View.VISIBLE);
                        text4.setText(listElement.getText4());
                    } else {
                        text4.setVisibility(View.GONE);
                    }
                }
            }
            return view;
        }

    }
	
	private class EventListElement extends BaseListElement {
		private JSONObject EventObject;
		public EventListElement(JSONObject eventObject) {
			//getResources().getDrawable(R.drawable.ic_launcher)
			super(eventObject,getIconForList(eventObject));
			EventObject = eventObject;
		}
		

		@Override
		protected OnClickListener getOnClickListener() {
			// TODO Auto-generated method stub
			return new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                	if (viewEventPressedCallback != null) {
                		String eventID = null;
                		try{
                			eventID 	= EventObject.getString("eventID");
            	    	} catch (JSONException e) {
            	    		e.printStackTrace();
            	    	}
                    	viewEventPressedCallback.onButtonPressed(eventID);
                    }
                }
            };
		}

		@Override
		protected void populateOGAction(OpenGraphAction action) {
			// TODO Auto-generated method stub
			
		}
	}
	
	
	public boolean getUserEvents() {
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
    	Log.d(TAG,"Request: " + request.toString());
    	
    	JSONfunctions.setRequestObject(request);
    	new JSONfunctions().execute(getString(R.string.listEvents));

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
    		if(responseArray == null){
        		Log.d(TAG,"Null response recieved");
        		UserEventsJSONArray = responseArray;
        		UserEventsPresent = false;
        		return true;
        	}else{
        		Log.d(TAG,responseArray.toString());
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
		    	
		    	if(success == 1) // Events exists, get data
		    	{
		    		UserEventsJSONArray = responseArray;
		    		UserEventsPresent = true;
		    	}else{
		    		// TODO if events doesn't exist.
		    		return false;
		    	}
		    	return true;
        	}
    	}
		return false;
	}
	
	
	
	private Drawable getIconForList(JSONObject eventObject){
		int sportID = 0;
		try {
			sportID = eventObject.getInt("sportID");
		} catch (JSONException e) {
			e.printStackTrace();
		}
		switch(sportID){
		case 1: return getResources().getDrawable(R.drawable.soccer);
		case 2: return getResources().getDrawable(R.drawable.tennis);
		case 3: return getResources().getDrawable(R.drawable.golf);
		case 4: return getResources().getDrawable(R.drawable.rugby);
		case 5: return getResources().getDrawable(R.drawable.hurling);
		case 6: return getResources().getDrawable(R.drawable.gaelicfootball);
		case 7: return getResources().getDrawable(R.drawable.badminton);
		case 8: return getResources().getDrawable(R.drawable.squash);
		case 9: return getResources().getDrawable(R.drawable.americanfootball);
		case 10: return getResources().getDrawable(R.drawable.hockey);
		case 11: return getResources().getDrawable(R.drawable.ultimatefrisbee);
		case 12: return getResources().getDrawable(R.drawable.snooker);
		case 13: return getResources().getDrawable(R.drawable.darts);
		case 14: return getResources().getDrawable(R.drawable.icehockey);
		case 15: return getResources().getDrawable(R.drawable.basketball);
		case 16: return getResources().getDrawable(R.drawable.cricket);
		case 17: return getResources().getDrawable(R.drawable.baseball);
		case 18: return getResources().getDrawable(R.drawable.handball);
		case 19: return getResources().getDrawable(R.drawable.athletics);
		case 20: return getResources().getDrawable(R.drawable.volleyball);
		case 21: return getResources().getDrawable(R.drawable.sailing);
		case 22: return getResources().getDrawable(R.drawable.surfing);
		case 23: return getResources().getDrawable(R.drawable.swimming);
		case 24: return getResources().getDrawable(R.drawable.cycling);
		}
		
		return getResources().getDrawable(R.drawable.ic_launcher);
	}
	
}
