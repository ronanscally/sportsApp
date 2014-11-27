package com.example.sportsapp;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

//import com.example.sportsapp.ListUsersActivity.StableArrayAdapter;

//import com.example.sportsapp.ListEventsFragment.ActionListAdapter;
//import com.example.sportsapp.ListEventsFragment.EventListElement;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;


public class AddAFriendActivity extends ActionBarActivity {
	
	//static String ListType	= null;
	
	static String GroupID	= null;
	static String EventID	= null;
	
	static boolean Admin	= false;
	

	ListView AddFriends; 
	
	ArrayList<String> listdata = new ArrayList<String>();
	ArrayList<String> IDs = new ArrayList<String>();
	
	private JSONObject JSON_data;
	private JSONArray AddFriendsJSONArray;
	private boolean FriendsPresent = false;
	
	
	private String UserID = null;
	private String addid = null;
	
	static String TAG = "AddAFriendActivity";
	
	String message 		= null;
	int success 		= 0;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		//setContentView(R.layout.activity_list_friends);
		setContentView(R.layout.activity_add_friends);
		
		//Intent intent = getIntent();
		//UserID 	= intent.getStringExtra(R.string.EXTRA_PREFIX + "userID");
		
		//ListType = "Friends";
		
		Bundle extras = getIntent().getExtras();
		if(extras != null) {
			UserID = extras.getString("UserID");
			//latitude = extras.getDouble("latitude");
			//Log.d("LOG_TAG", "lat" + latitude);
			//Log.d("LOG_TAG", "long" + longitude);
			
		}
		
		AddFriends = (ListView) findViewById(R.id.listView);
		
		
		getFriends();
		
		
		if (AddFriendsJSONArray != null) { 
			   for (int i=0; i<AddFriendsJSONArray.length(); i++){ 
			    try {
			    	JSON_data = AddFriendsJSONArray.getJSONObject(i);
			    	String lastname = JSON_data.getString("lastName");
			    	String firstname = JSON_data.getString("firstName");
			    	
			    	
			    	String name = firstname+" "+lastname;
			    	listdata.add(name);
			    	
			    	
			    	String id = JSON_data.getString("userID");
			    	IDs.add(id);
			    	
			    	Log.d(TAG,"name: " + name);
			    	Log.d(TAG,"listdata: " + listdata);
			    	
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			   } 
			}
		
		Log.d(TAG,"listdata " + AddFriends);
		

		
		final StableArrayAdapter adapter = new StableArrayAdapter(this,
	            android.R.layout.simple_list_item_1, listdata);
		
		
		
		AddFriends.setAdapter(adapter);
		
		
		AddFriends.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, final View view,
                int position, long id) {
            	
            	Log.d(TAG,"listdata " + position);
            	
            	//Add as Friend
            	addid = IDs.get(position);
            	IDs.remove(position);
            	UpdateFriends();
            	
            	
            	
              final String item = (String) parent.getItemAtPosition(position);
              view.animate().setDuration(2000).alpha(0)
                  .withEndAction(new Runnable() {
                    @Override
                    public void run() {
                      listdata.remove(item);
                      adapter.notifyDataSetChanged();
                      view.setAlpha(1);
                    }
                  });
            }

          });
		
		
	}
		

	
	private class StableArrayAdapter extends ArrayAdapter<String> {

	    HashMap<String, Integer> mIdMap = new HashMap<String, Integer>();

	    public StableArrayAdapter(Context context, int textViewResourceId,
	        List<String> objects) {
	      super(context, textViewResourceId, objects);
	      for (int i = 0; i < objects.size(); ++i) {
	        mIdMap.put(objects.get(i), i);
	      }
	    }
	}
	
		
	private boolean getFriends() {
    	JSONfunctions.clearResponseBuffer();
		
    	
    	JSONObject request = new JSONObject();
    	 
    	try {
			request.put("userID", UserID);
		} catch (JSONException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
    	Log.d(TAG,"Request: " + request.toString());
    	
    	JSONfunctions.setRequestObject(request);
    	new JSONfunctions().execute(getString(R.string.otherUsers));

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
        		AddFriendsJSONArray = responseArray;
        		FriendsPresent = false;
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
		    	
		    	if(success == 1) // Friends exists, get data
		    	{
		    		AddFriendsJSONArray = responseArray;
		    		FriendsPresent = true;
		    	}else{
		    		// TODO if events doesn't exist.
		    		return false;
		    	}
		    	return true;
        	}
    	}
		return false;
	}
	
	
	private void UpdateFriends() {
		      // Your implementation here.
		    	try{
		    		Log.d(TAG,"addFriend");
		        	JSONObject request = new JSONObject();
		        	request.put("userID", UserID);
		        	request.put("friendID", addid);
		    		//request.put("deviceID", regid);
		    		// Send to server
		        	JSONfunctions.setRequestObject(request);
		        	String add_friend = getString(R.string.addFriend);
		        	new JSONfunctions().execute(add_friend);
		        	
		            long timeStart = System.currentTimeMillis();
		        	int timeoutSeconds = 10;
		        	while (true){
		        		if(timeStart + timeoutSeconds*1000 < System.currentTimeMillis()){	// Timeout (10seconds...)
		        			Log.d(TAG,"No server response.");
		        			Log.d(TAG,"Timeout triggered after " + timeoutSeconds + " seconds");
		        			break;
		        		}
		        		if(JSONfunctions.checkNewResponse()){
		        			break;
		        		}
		        	}
		        	Log.d(TAG,"addFriend Finished");
		    	}catch(JSONException e){
		    		System.out.println("Send Location to server");
		    		e.printStackTrace();
		    	}catch(Exception e){
		    		System.out.println("General Error");
		    		e.printStackTrace();
		    		
		    	}
		    }
	
}
