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


public class ListFriendsActivity extends ActionBarActivity {
	
	//static String ListType	= null;
	
	static String GroupID	= null;
	static String EventID	= null;
	
	static boolean Admin	= false;
	

	ListView Friends; 
	ArrayList<String> listdata = new ArrayList<String>();
	
	private JSONObject JSON_data;
	private JSONArray FriendsJSONArray;
	private boolean FriendsPresent = false;
	
	
	private String UserID = null;
	static String TAG = "ListFriendsActivity";
	
	String message 		= null;
	int success 		= 0;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_list_friends);
		
		Intent intent = getIntent();
		UserID 	= intent.getStringExtra(R.string.EXTRA_PREFIX + "userID");
		Friends = (ListView) findViewById(R.id.listView);
		
		updateFriends();
	}
		
	
	@Override
	protected void onRestart (){
		super.onRestart();
		updateFriends();
	}
	
	
	private void updateFriends() {
		FriendsJSONArray = null;
		listdata = new ArrayList<String>();
		getFriends();
		if (FriendsJSONArray != null) { 
			   for (int i=0; i<FriendsJSONArray.length(); i++){ 
			    try {
			    	JSON_data = FriendsJSONArray.getJSONObject(i);
			    	String lastname = JSON_data.getString("lastName");
			    	String firstname = JSON_data.getString("firstName");
			    	String name = (firstname+" "+lastname);
			    	listdata.add(name);
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			   } 
			}
		final StableArrayAdapter adapter = new StableArrayAdapter(this,
	            android.R.layout.simple_list_item_1, listdata);
		Friends.setAdapter(adapter);
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
    	new JSONfunctions().execute(getString(R.string.listFriends));

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
        		FriendsJSONArray = responseArray;
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
		    		FriendsJSONArray = responseArray;
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
		
	
	public void AddAFriendActivity (View view) {
		Log.d("ListFriendsActivity", "AddaFriendActivity called from List Friends Activity");
		Intent intent = new Intent(this, AddAFriendActivity.class);
		intent.putExtra("UserID", UserID);
    	startActivity(intent);
	}
	
	public void endActivity(View view) {
		finish();
	}
	
}