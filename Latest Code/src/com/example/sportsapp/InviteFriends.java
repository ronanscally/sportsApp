package com.example.sportsapp;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.support.v7.app.ActionBarActivity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

public class InviteFriends extends ActionBarActivity {
	
	private String TAG = "InviteFriends";
	
	private String UserID;
	private String EventID;
	
	JSONArray UserFriendsJSONArray = null;
	boolean FriendsPresent = false;

	private ListView listView;

	private TextView noFriendsText;

	private ArrayList<BaseListElement> listElements;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.invite_friends);
		
		if (savedInstanceState != null) {
			UserID 		= savedInstanceState.getString("UserID");
			EventID 	= savedInstanceState.getString("EventID");
		}else{
			// Get the message from the intent
		    Intent intent = getIntent();
		    UserID 		= intent.getStringExtra(R.string.EXTRA_PREFIX + "userID");
		    EventID		= intent.getStringExtra(R.string.EXTRA_PREFIX + "eventID");
		    Log.d(TAG,"EventID: "+EventID);
		}
		
		// Get friends data
		getFriendsData(UserID);
		
		// Attach to list

		listView 				= (ListView) 		findViewById(R.id.friendsList);
		noFriendsText			= (TextView) 		findViewById(R.id.noFriendsText);
		
		noFriendsText.setVisibility(View.GONE);
		listView.setVisibility(View.GONE);
		listElements = new ArrayList<BaseListElement>();
		
		final List<String> friendNames 	= new ArrayList<String>();
		final List<String> friendIDs	= new ArrayList<String>();
		
		
		if(FriendsPresent){ // Add friends' names to list if present
			for (int i = 0; i < UserFriendsJSONArray.length(); i++) {
				try {
					friendNames.add(UserFriendsJSONArray.getJSONObject(i).getString("firstName") + " " + UserFriendsJSONArray.getJSONObject(i).getString("lastName"));
					friendIDs.add(UserFriendsJSONArray.getJSONObject(i).getString("userID"));
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
			listView.setVisibility(View.VISIBLE);
		}else{
			// Set no events element to be visible
			noFriendsText.setVisibility(View.VISIBLE);
		}
        
		
		final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(
                this, 
                android.R.layout.simple_list_item_1,
                friendNames );
		listView.setAdapter(arrayAdapter);
		
		// Add on click listener to invite the friend
		listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, final View view,
                int position, long id) {
              final String item = (String) parent.getItemAtPosition(position);
              final int pos = position;
              view.animate().setDuration(2000).alpha(0)
                  .withEndAction(new Runnable() {
                    @Override
                    public void run() {
                    	// Send invitation to server
                    	String id = friendIDs.get(pos);
                    	Log.d(TAG,"EventID: "+EventID);
                    	sendInvitation(EventID,id);
                    	// upon success...
                    	friendIDs.remove(pos);
                      friendNames.remove(item);
                      arrayAdapter.notifyDataSetChanged();
                      view.setAlpha(1);
                    }
                  });
            }

          });
		
		Button backButton = (Button) findViewById(R.id.backButton);
		backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            		finish();
                }
        });
		
		
		// Display small notification on inviting the friend
		
		Log.d(TAG,"onCreate Exit");
	}

	
	@Override
	public void onSaveInstanceState(Bundle savedInstanceState) {
		// Save stuff..
	    savedInstanceState.putString("UserID", UserID);
	    savedInstanceState.putString("EventID", EventID);
		
	    super.onSaveInstanceState(savedInstanceState);
	}
	
	private void sendInvitation(String eventID, String friend_userID) {
		JSONfunctions.clearResponseBuffer();
//    	String message 		= null;
//    	int success 		= 0;
    	
    	JSONObject request = new JSONObject();
    	Log.d(TAG,"eventID: "+eventID);
    	try {
			request.put("eventID", eventID);
			request.put("userID", friend_userID);
			request.put("sendNotification", true);
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
    	// TODO care about response?
//		return false;
	}
	

	private boolean getFriendsData(String userID){
		JSONfunctions.clearResponseBuffer();
    	String message 		= null;
    	int success 		= 0;
    	
    	JSONObject request = new JSONObject();
    	 
    	try {
			request.put("userID", userID);
		} catch (JSONException e1) {
			e1.printStackTrace();
		}
    	Log.d(TAG ,"Request: " + request.toString());
    	

    	JSONfunctions.setRequestObject(request);
    	String url = getString(R.string.getFriends);
    	Log.d(TAG,"Url: " + url);
    	new JSONfunctions().execute(url);

    	// TODO more with timeout error... (make global?)
    	long timeStart = System.currentTimeMillis();
    	boolean timeout = false;
    	int timeoutSeconds = 10;
    	JSONArray responseArray = null;
    	while (true){
    		if(timeStart + timeoutSeconds*1000 < System.currentTimeMillis()){	// Timeout (10seconds...)
    			Log.d(TAG,"No server response.");
    			Log.d(TAG,"Timeout triggered after " + timeoutSeconds + " seconds");
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
        		UserFriendsJSONArray = responseArray;
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
		    		UserFriendsJSONArray = responseArray;
		    		FriendsPresent = true;
		    	}else{
		    		// TODO if friends don't exist.
		    		return false;
		    	}
		    	return true;
        	}
    	}
		return false;
	}
	
	public void endActivity(View view) {
		finish();
	}
}
