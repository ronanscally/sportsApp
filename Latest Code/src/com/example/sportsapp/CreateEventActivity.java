package com.example.sportsapp;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;

public class CreateEventActivity extends ActionBarActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_create_event);
	
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        // If your minSdkVersion is 11 or higher, instead use:
        // getActionBar().setDisplayHomeAsUpEnabled(true);
		
		// TODO set edit event button as invisible for non-admins
	}

}
