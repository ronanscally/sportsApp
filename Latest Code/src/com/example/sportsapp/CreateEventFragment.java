package com.example.sportsapp;


import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.json.JSONObject;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;

public class CreateEventFragment extends Fragment {

	static final String TAG = "CreateEventFragment";
	private TextView text_eventName;
	private Spinner spinner_sport;
	private Spinner spinner_locationAddress;
	private Spinner spinner_playersRequired;
	
	private JSONObject EventObject = new JSONObject();
	private Button backButton;
	private ButtonPressedCallback backPressedCallback;
	private Button saveButton;
//	private ArrayAdapter<CharSequence> adapter;
	private ArrayAdapter<CharSequence> adapter_location;
	private ArrayAdapter<CharSequence> adapter_sport;
	private ArrayAdapter<String> adapter_playersRequired;
	private static Button pickStartTimeButton;
	private static Button pickStartDateButton;
	private static Button pickEndTimeButton;
	private static Button pickEndDateButton;
	
	
	private static Calendar currentTime = Calendar.getInstance();
	private static Calendar startTime = Calendar.getInstance();
	private static Calendar endTime = Calendar.getInstance();
	
	public interface ButtonPressedCallback {
        void onButtonPressed(String id);
    }
    
    public void setBackCallback(ButtonPressedCallback callback) {
    	backPressedCallback = callback;
    }

	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		View view = inflater.inflate(R.layout.create_event, container, false);
		
		
		// TODO put code into functions to update these values and their button displays
		currentTime = Calendar.getInstance();
		startTime.setTime(currentTime.getTime());
		endTime.setTime(startTime.getTime());
		endTime.add(Calendar.HOUR_OF_DAY, 1); // Plus 1 hour
		
		
		text_eventName			= (TextView) 	view.findViewById(R.id.eventName);
		spinner_sport			= (Spinner) 	view.findViewById(R.id.sport);
		spinner_playersRequired	= (Spinner) 	view.findViewById(R.id.playersRequired);
		spinner_locationAddress	= (Spinner) 	view.findViewById(R.id.locationAddress);
		
		pickStartTimeButton	= (Button)		view.findViewById(R.id.pickStartTimeButton);
		pickStartDateButton	= (Button)		view.findViewById(R.id.pickStartDateButton);
		pickEndTimeButton	= (Button)		view.findViewById(R.id.pickEndTimeButton);
		pickEndDateButton	= (Button)		view.findViewById(R.id.pickEndDateButton);
		
		pickStartTimeButton.setText("" + startTime.get(Calendar.HOUR_OF_DAY) + ":" + startTime.get(Calendar.MINUTE));
		pickStartDateButton.setText("" + startTime.get(Calendar.DAY_OF_MONTH) + "/" + startTime.get(Calendar.MONTH) + "/" + startTime.get(Calendar.YEAR));
		pickEndTimeButton.setText("" + endTime.get(Calendar.HOUR_OF_DAY) + ":" + endTime.get(Calendar.MINUTE));
		pickEndDateButton.setText("" + endTime.get(Calendar.DAY_OF_MONTH) + "/" + endTime.get(Calendar.MONTH) + "/" + endTime.get(Calendar.YEAR));
		
		
		
		pickStartTimeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            	showTimePickerDialog(view,true);
            }
        });
		
		pickStartDateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            	showDatePickerDialog(view,true);
            }
        });
		
		pickEndTimeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            	showTimePickerDialog(view,false);
            }
        });
		
		pickEndDateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            	showDatePickerDialog(view,false);
            }
        });
		
		
		adapter_location = ArrayAdapter.createFromResource(getActivity(),
		        R.array.event_locations, android.R.layout.simple_spinner_item);
		// Specify the layout to use when the list of choices appears
		adapter_location.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		// Apply the adapter to the spinner
		spinner_locationAddress.setAdapter(adapter_location);
		
		adapter_sport = ArrayAdapter.createFromResource(getActivity(),
		        R.array.sport_names, android.R.layout.simple_spinner_item);
		// Specify the layout to use when the list of choices appears
		adapter_sport.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		// Apply the adapter to the spinner
		spinner_sport.setAdapter(adapter_sport);
		
		List<String> numbersList = new ArrayList<String>();
        for (int i = 2; i < 30; ++i) {
        	numbersList.add(String.valueOf(i));
        }
        
		adapter_playersRequired = new ArrayAdapter<String>(getActivity(),
				android.R.layout.simple_spinner_item, numbersList);
		// Specify the layout to use when the list of choices appears
		adapter_playersRequired.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		// Apply the adapter to the spinner
		spinner_playersRequired.setAdapter(adapter_playersRequired);
		
		
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
    	
    	saveButton 			= (Button) 			view.findViewById(R.id.saveButton);
    	saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            	saveEvent();
            	backButton.callOnClick();
            }
        });
    	
		return view;

	}
	

	private void saveEvent(){
		if(getUserInputData()){
			// Clear buffer
			JSONfunctions.clearResponseBuffer();
			JSONObject request = EventObject;
			Log.d(TAG,"Request: " + request.toString());
	    	

	    	JSONfunctions.setRequestObject(request);
	    	String url = getString(R.string.createEvent);
	    	Log.d(TAG,"Url: " + url);
	    	new JSONfunctions().execute(url);

	    	// TODO more with timeout error... (make global?)
	    	long timeStart = System.currentTimeMillis();
//	    	boolean timeout = false;
	    	int timeoutSeconds = 10;
//	    	JSONArray responseArray = null;
	    	while (true){
	    		if(timeStart + timeoutSeconds*1000 < System.currentTimeMillis()){	// Timeout (10seconds...)
	    			Log.d(TAG,"No server response.");
	    			Log.d(TAG,"Timeout triggered after " + timeoutSeconds + " seconds");
//	    			timeout = true;
	    			break;
	    		}
	    		
	    		if(JSONfunctions.checkNewResponse()){
//	    			responseArray = JSONfunctions.getResponseArray();
	    			break;
	    		}
	    	}
			return;
		}else{
			// Display alert message
		}
	}

	private boolean getUserInputData() {
		try{
			// Create object from text data...
			Log.d(TAG,"Creating object");
			String UserID = Events.UserID;
			EventObject.put("userID", UserID);
			Log.d(TAG,"HostID set");
			EventObject.put("title", text_eventName.getText().toString());
			
			String sport = spinner_sport.getSelectedItem().toString();  
			String playersRequired = spinner_playersRequired.getSelectedItem().toString();
			EventObject.put("sport", sport);
			EventObject.put("numReqd", playersRequired);
			// Date and time, start and end
//			String startTime = "2014-12-21 15:00:00";
//			String endTime = "2014-12-21 18:00:00";
			EventObject.put("startTime", startTime.getTimeInMillis());
			EventObject.put("endTime", endTime.getTimeInMillis());
			Log.d(TAG,"Times added");
			// Location
			String address = spinner_locationAddress.getSelectedItem().toString();
			if(address.equals("UCD Bowl")){
				EventObject.put("lng", -6.228134);
				EventObject.put("lat", 53.310376);
			}else if(address.equals("UCD Sports Hall B")){
				EventObject.put("lng", -6.229142);
				EventObject.put("lat", 53.308328);
			}else if(address.equals("UCD Swimming Pool")){
				EventObject.put("lng", -6.228074);
				EventObject.put("lat", 53.308819);
			}else if(address.equals("Herbert Park")){
				EventObject.put("lng", -6.235490);
				EventObject.put("lat", 53.327435);
			}else if(address.equals("Rosses Point Golf Club (Sligo)")){
				EventObject.put("lng", -8.565783);
				EventObject.put("lat", 54.307183);
			}else{
				EventObject.put("lng", 0.0);
				EventObject.put("lat", 0.0);
			}
			Log.d(TAG,"Location set");
			return true;
		}catch(Exception e){
			Log.d(TAG,"Failed to get inputed data and set to object");
		}
		return false;
	}
	
	public void showTimePickerDialog(View v,Boolean start) {
	    DialogFragment newFragment = new TimePickerFragment(start);
	    newFragment.show(getFragmentManager(), "timePicker");
	}
	
	public static class TimePickerFragment extends DialogFragment
	implements TimePickerDialog.OnTimeSetListener {
		
		private boolean Start;

		public TimePickerFragment(boolean start) {
			Start = start;
		}
	
		@Override
		public Dialog onCreateDialog(Bundle savedInstanceState) {
			// Use the current time as the default values for the picker
			Calendar c = Calendar.getInstance();
			if(Start){
				c = startTime;
			}else{
				c = endTime;
			}
			int hour = c.get(Calendar.HOUR_OF_DAY);
			int minute = c.get(Calendar.MINUTE);
			
			// Create a new instance of TimePickerDialog and return it
			return new TimePickerDialog(getActivity(), this, hour, minute,
			DateFormat.is24HourFormat(getActivity()));
		}
		
		public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
		// Do something with the time chosen by the user
			Log.d(TAG,"Time chosen= "+hourOfDay+":"+minute);
			if(Start){
				startTime.set(Calendar.HOUR_OF_DAY, hourOfDay);
				startTime.set(Calendar.MINUTE, minute);
				if(startTime.compareTo(currentTime) <= 0){
					startTime.setTime(currentTime.getTime());
				}
				if(startTime.compareTo(endTime) >= 0){
					endTime.setTime(startTime.getTime());
					endTime.add(Calendar.HOUR_OF_DAY, 1); // Plus 1 hour
					pickEndTimeButton.setText("" + endTime.get(Calendar.HOUR_OF_DAY) + ":" + endTime.get(Calendar.MINUTE));
					pickEndDateButton.setText("" + endTime.get(Calendar.DAY_OF_MONTH) + "/" + endTime.get(Calendar.MONTH) + "/" + endTime.get(Calendar.YEAR));
				}
				// Set text
				pickStartTimeButton.setText("" + startTime.get(Calendar.HOUR_OF_DAY) + ":" + startTime.get(Calendar.MINUTE));
			}else{
				endTime.set(Calendar.HOUR_OF_DAY, hourOfDay);
				endTime.set(Calendar.MINUTE, minute);
				if(startTime.compareTo(endTime) >= 0){
					endTime.setTime(startTime.getTime());
					endTime.add(Calendar.HOUR_OF_DAY, 1); // Plus 1 hour
				}
				pickEndTimeButton.setText("" + endTime.get(Calendar.HOUR_OF_DAY) + ":" + endTime.get(Calendar.MINUTE));
			}
		}
	}
	
	public void showDatePickerDialog(View v, boolean start) {
	    DialogFragment newFragment = new DatePickerFragment(start);
	    newFragment.show(getFragmentManager(), "datePicker");
	}
	
	public static class DatePickerFragment extends DialogFragment
	    implements DatePickerDialog.OnDateSetListener {
		
		private boolean Start;

		public DatePickerFragment(boolean start) {
			Start = start;
		}

		@Override
		public Dialog onCreateDialog(Bundle savedInstanceState) {
			// Use the current date as the default date in the picker
			final Calendar c = Calendar.getInstance();
		
			// Create a new instance of DatePickerDialog and return it
			DatePickerDialog datePicker;
			if(Start){
				datePicker = new DatePickerDialog(getActivity(), this, startTime.get(Calendar.YEAR), startTime.get(Calendar.MONTH), startTime.get(Calendar.DAY_OF_MONTH));
				datePicker.getDatePicker().setMinDate(c.getTimeInMillis());	// Min is current date
			}else{
				datePicker = new DatePickerDialog(getActivity(), this, endTime.get(Calendar.YEAR), endTime.get(Calendar.MONTH), endTime.get(Calendar.DAY_OF_MONTH));
				datePicker.getDatePicker().setMinDate(startTime.getTimeInMillis()); // Min is start time
			}
			return datePicker;
			}
		
		public void onDateSet(DatePicker view, int year, int month, int day) {
		// Do something with the date chosen by the user
			Log.d(TAG,"Date chosen= "+year+"/"+month+"/"+day);
			if(Start){
				startTime.set(year, month, day);
				if(startTime.compareTo(endTime) >= 0){
					endTime.setTime(startTime.getTime());
					endTime.add(Calendar.HOUR_OF_DAY, 1);
					pickEndTimeButton.setText("" + endTime.get(Calendar.HOUR_OF_DAY) + ":" + endTime.get(Calendar.MINUTE));
					pickEndDateButton.setText("" + endTime.get(Calendar.DAY_OF_MONTH) + "/" + endTime.get(Calendar.MONTH) + "/" + endTime.get(Calendar.YEAR));
				}
				pickStartDateButton.setText("" + startTime.get(Calendar.DAY_OF_MONTH) + "/" + startTime.get(Calendar.MONTH) + "/" + startTime.get(Calendar.YEAR));
			}else{
				endTime.set(year, month, day);
				if(startTime.compareTo(endTime) >= 0){
					endTime.setTime(startTime.getTime());
					endTime.add(Calendar.HOUR_OF_DAY, 1); // Plus 1 hour
				}
				pickEndDateButton.setText("" + endTime.get(Calendar.DAY_OF_MONTH) + "/" + endTime.get(Calendar.MONTH) + "/" + endTime.get(Calendar.YEAR));
			}
		}
	}
}

