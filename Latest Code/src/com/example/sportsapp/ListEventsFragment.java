package com.example.sportsapp;


import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.example.sportsapp.SplashFragment.SkipLoginCallback;
import com.facebook.model.OpenGraphAction;


import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class ListEventsFragment extends Fragment  {

	private static final String TAG = "ListEventsFragment";
	
	private Spinner eventUserStatusSpinner;
	private ArrayAdapter<CharSequence> adapter;
	private Button nearbyButton;
	private Button yourEventsButton;
	private Button createButton;
	private RelativeLayout buttonBar;
	private ListView listView;
	private List<BaseListElement> listElements;
	private String UserID;
	GPSTracker gps;
	private double latitude = 0;
	private double longitude = 0;
	
	private JSONArray UserEventsJSONArray;
	
	private ButtonPressedCallback createPressedCallback;

    public interface ButtonPressedCallback {
        void onButtonPressed();
    }
    
    public void setCreateCallback(ButtonPressedCallback callback) {
    	createPressedCallback = callback;
    }
    
    public void setViewEventCallback(ButtonPressedCallback callback) {
    	createPressedCallback = callback;
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
		
		
		eventUserStatusSpinner 	= (Spinner) 		view.findViewById(R.id.eventUserStatusSpinner);
		nearbyButton 			= (Button) 			view.findViewById(R.id.nearbyButton);
		yourEventsButton 		= (Button) 			view.findViewById(R.id.yourEventsButton);
		createButton 			= (Button) 			view.findViewById(R.id.createButton);
		buttonBar 				= (RelativeLayout) 	view.findViewById(R.id.buttonBar);
		listView 				= (ListView) 		view.findViewById(R.id.eventsList);
		
		
		GPSTracker gps = new GPSTracker(this.getActivity());		 
	    latitude =  gps.getLatitude();
		longitude = gps.getLongitude();
		
		
		nearbyButton.setText("Near By Events");
		adapter = ArrayAdapter.createFromResource(getActivity(),
		        R.array.events_type, android.R.layout.simple_spinner_item);
		// Specify the layout to use when the list of choices appears
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		// Apply the adapter to the spinner
		eventUserStatusSpinner.setAdapter(adapter);
		
		yourEventsButton.setVisibility(View.GONE);
		
		
		
		
		
		
		nearbyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            	try{
	            	yourEventsButton.setVisibility(View.VISIBLE);
	            	nearbyButton.setVisibility(View.GONE);
	            	buttonBar.setVisibility(View.GONE);
	            	JSONObject request = new JSONObject();
	            	request.put("userID", UserID);
	        		request.put("lng", longitude);
	        		request.put("lat", latitude);
	        		// Send to server
	            	JSONfunctions.setRequestObject(request);
	            	String save_location = getString(R.string.saveLocation);
	            	new JSONfunctions().execute(save_location);
            	}catch(JSONException e){
            		System.out.println("Send Location to server");
            		e.printStackTrace();
            	}catch(Exception e){
            		System.out.println("General Error");
            		e.printStackTrace();
            		
            	}
            	
            }
        });
		
		yourEventsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            	yourEventsButton.setVisibility(View.GONE);
            	nearbyButton.setVisibility(View.VISIBLE);
            	buttonBar.setVisibility(View.VISIBLE);
            }
        });

		
		createButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               	eventUserStatusSpinner.setEnabled(false);
            	eventUserStatusSpinner.setAdapter(adapter);
            }
        });
		
		
		listElements = new ArrayList<BaseListElement>();
		
		for (int i = 0; i < UserEventsJSONArray.length(); i++) {
			try {
				listElements.add(new EventListElement(UserEventsJSONArray.getJSONObject(i)));
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
        
        if (savedInstanceState != null) {
            for (BaseListElement listElement : listElements) {
                listElement.restoreState(savedInstanceState);
            }
        }

        listView.setAdapter(new ActionListAdapter(getActivity(), R.id.eventsList, listElements));
		return view;
        
        
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
            }
            return view;
        }

    }
	
	private boolean getUserEvents() {
    	
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
	    	
	    	if(success == 1) // Events exists, get data
	    	{
	    		UserEventsJSONArray = responseArray;
	    	}else{
	    		// TODO if events doesn't exist.
	    		return false;
	    	}
	    	return true;
    	}
		return false;
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
			return null;
		}

		@Override
		protected void populateOGAction(OpenGraphAction action) {
			// TODO Auto-generated method stub
			
		}
	}
	
	private Drawable getIconForList(JSONObject eventObject){
		int sportID = 0;
		try {
			sportID = eventObject.getInt("sportID");
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		switch(sportID){
		case 1: return getResources().getDrawable(R.drawable.soccer);
		}
		
		return getResources().getDrawable(R.drawable.ic_launcher);
	}
	
	
	//GPS Stuff
	
	
	
	
}
