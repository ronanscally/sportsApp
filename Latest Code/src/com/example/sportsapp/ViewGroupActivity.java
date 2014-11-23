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


public class ViewGroupActivity extends ActionBarActivity {
	
	static String GroupID 	= null;
	static boolean Admin	= false;
	
	private ListView lv;
	List<String> Message_string_array_list = new ArrayList<String>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_view_group);
	
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        // If your minSdkVersion is 11 or higher, instead use:
        // getActionBar().setDisplayHomeAsUpEnabled(true);
		
		// TODO get GroupID and Admin
		// TODO set edit event button as invisible for non-admins
		
		
		lv = (ListView) findViewById(R.id.listView);

        Message_string_array_list.add("This group is GREAT!!             - Ronan");
        Message_string_array_list.add("I love the new groups feature!!   - Tony Hawk");
        Message_string_array_list.add("More messages 3	- God");
        Message_string_array_list.add("More messages 4	- God");
        Message_string_array_list.add("More messages 5	- God");
        Message_string_array_list.add("More messages 6	- God");
        Message_string_array_list.add("More messages 7	- God");
        Message_string_array_list.add("More messages 8	- God");
        Message_string_array_list.add("More messages 9	- God");
        Message_string_array_list.add("More messages 10	- God");
        

        // This is the array adapter, it takes the context of the activity as a 
        // first parameter, the type of list view as a second parameter and your 
        // array as a third parameter.
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(
                this, 
//                android.R.layout.simple_list_item_1,
                R.layout.event_list_item,
                Message_string_array_list );

        lv.setAdapter(arrayAdapter); 
        
     // Bind onclick event handler
        lv.setOnItemClickListener(new OnItemClickListener() {
					@Override
					public void onItemClick(AdapterView<?> parent, View view,
							int position, long id) {
						
						Log.d("List","position = " + String.valueOf(position));
						Log.d("List","id 	   = " + String.valueOf(id));
						
					}
        });
	}
	
    public void viewMembers(View view) {
    	// View own profile
    	Intent intent = new Intent(this, ListUsersActivity.class);
    	intent.putExtra(R.string.EXTRA_PREFIX + "listType", "Group");
    	intent.putExtra(R.string.EXTRA_PREFIX + "groupID", GroupID);
    	intent.putExtra(R.string.EXTRA_PREFIX + "admin", Admin);
    	startActivity(intent);
    }

}
