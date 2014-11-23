package com.example.sportsapp;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

public class EditProfileActivity extends ActionBarActivity {
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_edit_profile);
		
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        // If your minSdkVersion is 11 or higher, instead use:
        // getActionBar().setDisplayHomeAsUpEnabled(true);
        
        
        TextView text_userID 	= (TextView) findViewById(R.id.userID);
        EditText edit_firstName = (EditText) findViewById(R.id.firstName);
        EditText edit_lastName 	= (EditText) findViewById(R.id.lastName);
        EditText edit_dob 		= (EditText) findViewById(R.id.dob);
        
        text_userID.setText(DisplayProfileActivity.UserID);
        edit_firstName.setText(DisplayProfileActivity.FirstName);
        edit_lastName.setText(DisplayProfileActivity.LastName);
        edit_dob.setText(DisplayProfileActivity.Dob);
        
		
	}


	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
	    // Inflate the menu items for use in the action bar
	    MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.main_activity_actions, menu);
	    return super.onCreateOptionsMenu(menu);
	}
	
		
	public void saveProfile(View view) {
		// Get values...
		
//		TextView text_userID 	= (TextView) findViewById(R.id.userID);
        EditText edit_firstName = (EditText) findViewById(R.id.firstName);
        EditText edit_lastName 	= (EditText) findViewById(R.id.lastName);
        EditText edit_dob 		= (EditText) findViewById(R.id.dob);
    	
		// TODO Check good values
		
		// Format into JSON object
		
		JSONObject request = new JSONObject();
   	 
    	try {
			request.put("userID", DisplayProfileActivity.UserID);
			request.put("firstName", edit_firstName.getText().toString());
			request.put("lastName", edit_lastName.getText().toString());
			request.put("dob", edit_dob.getText().toString());
		} catch (JSONException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		// Send to server
		
    	JSONfunctions.setRequestObject(request);
    	String save_profile_url = getString(R.string.saveProfile);
    	new JSONfunctions().execute(save_profile_url);
    	
    	// TODO make this function of JSONfunctions...
    	long timeStart = System.currentTimeMillis();
    	boolean timeout = false;
    	int timeoutSeconds = 10;
    	JSONArray responseArray = null;
    	while (true){
    		if(timeStart + timeoutSeconds*1000 < System.currentTimeMillis()){	// Timeout (10seconds...)
    			Log.d("ViewEventActivity","No server response.");
    			Log.d("ViewEventActivity","Timeout triggered after " + timeoutSeconds + " seconds");
    			timeout = true;
    			break;
    		}
    		
    		if(JSONfunctions.checkNewResponse()){
    			responseArray = JSONfunctions.getResponseArray();
    			break;
    		}
    	}
    	if(!timeout){
			if(responseArray.length() != 1)
				Log.e("EditProfileActivity","Unexpected response length...");
			String message = null;
			int success = 0;
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
			
			Log.d("Server","Message: " + message);
			Log.d("Server","Success: " + String.valueOf(success));
			
			// Update display profile..
			if(success == 1){
				// TODO maybe better way: Update Display Profile..
				DisplayProfileActivity.FirstName = edit_firstName.getText().toString();
		        DisplayProfileActivity.LastName = edit_lastName.getText().toString();
		        DisplayProfileActivity.Dob = edit_dob.getText().toString();
			}else{
				// TODO failed profile edit..
			}
    	}
		else{
			// TODO If timeout...
		}
    	
        
    	// Close activity
    	finish();
    }
	
	public void cancelEdit(View view) {
        // Close edit
		finish();
    }
	    
	    
}
