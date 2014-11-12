package com.Sporty.SportsApp;

import org.json.JSONException;
import org.json.JSONObject;

//import com.Sporty.SportsApp.checkGooglePlayServices.ErrorDialogFragment;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;

import android.support.v4.app.DialogFragment;
import android.support.v7.app.ActionBarActivity;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.IntentSender;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;



public class MainActivity extends ActionBarActivity {
	// Global constants
    /*
     * Define a request code to send to Google Play services
     * This code is returned in Activity.onActivityResult
     */
    private final static int
            CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;
    static final int 
    		REQUEST_CODE_RECOVER_PLAY_SERVICES = 1001;
    
    
	
	public final static String EXTRA_MESSAGE = "com.example.myfirstapp.MESSAGE";
	JSONObject jsonobject;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        
//    	jsonobject = JSONfunctions.getJSONfromURL("http://csiserver.ucd.ie/~09333541/sportsapp/profiles/checkProfileExists.php");
//        jsonobject = new JSONObject().put("JSON", "Hello, World!");
//        myString = new JSONObject().put("JSON", "Hello, World!").toString()
//        try {
//			jsonobject = new JSONObject().put("MESSAGE", "Hello, World!");
//		} catch (JSONException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//        jsonobject = JSONfunctions.getJSONfromURL("http://csiserver.ucd.ie/~09333541/sportsapp/profiles/checkProfileExists.php");
        
        
    }

    public void sendMessage(View view) {
        // Do something in response to button
    	Intent intent = new Intent(this, DisplayMessageActivity.class);
    	EditText editText = (EditText) findViewById(R.id.edit_message);
    	String message = editText.getText().toString();
    	intent.putExtra(EXTRA_MESSAGE, message);
    	startActivity(intent);
    }
    
    public void login(View view) {
        // Do something in response to button
    	Intent intent = new Intent(this, DisplayMessageActivity.class);
    	EditText username = (EditText) findViewById(R.id.username);
//    	EditText password = (EditText) findViewById(R.id.password);
	
    	Log.d("log_tag","Attempting login..");
    	
    	String message = null;
    	JSONObject request = new JSONObject();
    	 
    	try {
			request.put("userID", username.getText().toString());
		} catch (JSONException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
    	Log.d("log_tag","Request: " + request.toString());
    	
    	JSONfunctions.setRequestObject(request);
    	
    	new JSONfunctions().execute("http://csiserver.ucd.ie/~09333541/sportsapp/profiles/checkProfileExists.php");

    	JSONObject jsonobject = null;
    	while (jsonobject == null){
    		jsonobject = JSONfunctions.getResponseObject();
    	}
    	try {
			message = jsonobject.getString("message");
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	intent.putExtra(EXTRA_MESSAGE, message);
    	startActivity(intent);
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
//        int id = item.getItemId();
//        if (id == R.id.action_settings) {
//            return true;
//        }
//        return super.onOptionsItemSelected(item);
        
        switch (item.getItemId()) {
        case R.id.action_search:
            openSearch();
            return true;
        case R.id.action_settings:
            openSettings();
            return true;
        default:
            return super.onOptionsItemSelected(item);
    }
    }
    

    private void openSettings() {
		// TODO Auto-generated method stub
		
	}


	private void openSearch() {
		// TODO Auto-generated method stub
		
	}


	@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main_activity_actions, menu);
        return true;
    }
	
    // Define a DialogFragment that displays the error dialog
    public static class ErrorDialogFragment extends DialogFragment {
        // Global field to contain the error dialog
        private Dialog mDialog;
        // Default constructor. Sets the dialog field to null
        public ErrorDialogFragment() {
            super();
            mDialog = null;
        }
        // Set the dialog to display
        public void setDialog(Dialog dialog) {
            mDialog = dialog;
        }
        // Return a Dialog to the DialogFragment.
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            return mDialog;
        }
    }
    
    @Override
    protected void onActivityResult(
            int requestCode, int resultCode, Intent data) {
        // Decide what to do based on the original request code
        switch (requestCode) {
            case CONNECTION_FAILURE_RESOLUTION_REQUEST :
            /*
             * If the result code is Activity.RESULT_OK, try
             * to connect again
             */
                switch (resultCode) {
                    case Activity.RESULT_OK :
                    /*
                     * Try the request again
                     */
                    break;
                }
        }
     }
    
    
    
    private boolean servicesConnected() {
        // Check that Google Play services is available
        int resultCode =
                GooglePlayServicesUtil.
                        isGooglePlayServicesAvailable(this);
        // If Google Play services is available
        if (ConnectionResult.SUCCESS == resultCode) {
            // In debug mode, log the status
            Log.d("Location Updates",
                    "Google Play services is available.");
            // Continue
            return true;
        // Google Play services was not available for some reason.
        // resultCode holds the error code.
        } else {
            // Get the error dialog from Google Play services
            Dialog errorDialog = GooglePlayServicesUtil.getErrorDialog(
                    resultCode,
                    this,
                    CONNECTION_FAILURE_RESOLUTION_REQUEST);

            // If Google Play services can provide an error dialog
            if (errorDialog != null) {
                // Create a new DialogFragment for the error dialog
                ErrorDialogFragment errorFragment =
                        new ErrorDialogFragment();
                // Set the dialog in the DialogFragment
                errorFragment.setDialog(errorDialog);
                // Show the error dialog in the DialogFragment
                errorFragment.show(getSupportFragmentManager(),
                        "Location Updates");
            }
        }
		return false;
    }
	
    
    
    
    
    
    
    
    
        /*
         * Called by Location Services when the request to connect the
         * client finishes successfully. At this point, you can
         * request the current location or start periodic updates
         */
        @Override
        public void onConnected(Bundle dataBundle) {
            // Display the connection status
            Toast.makeText(this, "Connected", Toast.LENGTH_SHORT).show();

        }
        /*
         * Called by Location Services if the connection to the
         * location client drops because of an error.
         */
        @Override
        public void onDisconnected() {
            // Display the connection status
            Toast.makeText(this, "Disconnected. Please re-connect.",
                    Toast.LENGTH_SHORT).show();
        }
        
        
        /*
         * Called by Location Services if the attempt to
         * Location Services fails.
         */
        @Override
        public void onConnectionFailed(ConnectionResult connectionResult) {
            /*
             * Google Play services can resolve some errors it detects.
             * If the error has a resolution, try sending an Intent to
             * start a Google Play services activity that can resolve
             * error.
             */
            if (connectionResult.hasResolution()) {
                try {
                    // Start an Activity that tries to resolve the error
                    connectionResult.startResolutionForResult(
                            this,
                            CONNECTION_FAILURE_RESOLUTION_REQUEST);
                    /*
                     * Thrown if Google Play services canceled the original
                     * PendingIntent
                     */
                } catch (IntentSender.SendIntentException e) {
                    // Log the error
                    e.printStackTrace();
                }
            } else {
                /*
                 * If no resolution is available, display a dialog to the
                 * user with the error.
                 */
                showErrorDialog(connectionResult.getErrorCode());
            }
        }
    
    
    
        void showErrorDialog(int code) {
        	  GooglePlayServicesUtil.getErrorDialog(code, this, 
        	      REQUEST_CODE_RECOVER_PLAY_SERVICES).show();
        	}
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
}

































/* OLD

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;


public class MainActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
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
}

*/