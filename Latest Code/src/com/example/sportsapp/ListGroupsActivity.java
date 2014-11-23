package com.example.sportsapp;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;

public class ListGroupsActivity extends ActionBarActivity {

	private ListView lv;
	List<String> Group_string_array_list = new ArrayList<String>();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_list_groups);
	
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        // If your minSdkVersion is 11 or higher, instead use:
        // getActionBar().setDisplayHomeAsUpEnabled(true);
		

		
		lv = (ListView) findViewById(R.id.listView);

        Group_string_array_list.add("UCD first team");
        Group_string_array_list.add("Derezzed");
        Group_string_array_list.add("Pelt");
        Group_string_array_list.add("Tony's Army");
        Group_string_array_list.add("Group 5");
        Group_string_array_list.add("Group 6");
        Group_string_array_list.add("Group 7");
        Group_string_array_list.add("Group 8");
        Group_string_array_list.add("Group 9");
        Group_string_array_list.add("Group 10");
        Group_string_array_list.add("Group 11");
        Group_string_array_list.add("Group 12");
        Group_string_array_list.add("Group 13");
        Group_string_array_list.add("Group 14");
        
        

        // This is the array adapter, it takes the context of the activity as a 
        // first parameter, the type of list view as a second parameter and your 
        // array as a third parameter.
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(
                this, 
//                android.R.layout.simple_list_item_1,
                R.layout.group_list_item,
                Group_string_array_list );

        lv.setAdapter(arrayAdapter); 
        
     // Bind onclick event handler
        lv.setOnItemClickListener(new OnItemClickListener() {
					@Override
					public void onItemClick(AdapterView<?> parent, View view,
							int position, long id) {
						
						Log.d("List","position = " + String.valueOf(position));
						Log.d("List","id 	   = " + String.valueOf(id));
						
						viewGroup(view, position);
						
					}
        });
	}
	
	public void createGroup(View view) {
    	Intent intent = new Intent(this, CreateGroupActivity.class);
    	startActivity(intent);
    }
	
	public void viewGroup(View view, int position) {
    	Intent intent = new Intent(this, ViewGroupActivity.class);
    	String groupID = Group_string_array_list.get(position);
    	intent.putExtra(R.string.EXTRA_PREFIX + "groupID", groupID);
    	startActivity(intent);
    }
}
