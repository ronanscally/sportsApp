package com.example.sportsapp;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class ListUsersActivity extends ActionBarActivity {
	
	static String ListType	= null;
	
	static String UserID	= null;
	static String GroupID	= null;
	static String EventID	= null;
	
	static boolean Admin	= false;
	
//	private ListView lv;
	List<String> UserName_string_array_list = new ArrayList<String>();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_list_users);
		
		// Get the message from the intent
	    Intent intent 	= getIntent();
	    
	    try{
	    	ListType	= intent.getStringExtra(R.string.EXTRA_PREFIX + "listType");
	    }catch(Exception e){
	    	// TODO exception
	    }
	    
	    // TODO Make this better (enumeration). Switch statement not allowed with string...
	    if(ListType == "Group"){
	    	GroupID		= intent.getStringExtra(R.string.EXTRA_PREFIX + "groupID");
	    	Admin		= intent.getBooleanExtra(R.string.EXTRA_PREFIX + "admin",false);
	    }else if(ListType == "Event"){
	    	EventID		= intent.getStringExtra(R.string.EXTRA_PREFIX + "eventID");
	    	Admin		= intent.getBooleanExtra(R.string.EXTRA_PREFIX + "admin",false);
	    }else if(ListType == "Friends"){
	    	UserID		= intent.getStringExtra(R.string.EXTRA_PREFIX + "userID");
	    	Admin		= intent.getBooleanExtra(R.string.EXTRA_PREFIX + "admin",false);
	    }else{
	    	
	    }
	    
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        // If your minSdkVersion is 11 or higher, instead use:
        // getActionBar().setDisplayHomeAsUpEnabled(true);
		
		
//		lv = (ListView) findViewById(R.id.listView);
//
//		// TODO get data...
//        UserName_string_array_list.add("Ronan Scally");
//        UserName_string_array_list.add("Mark Purcell");
//        UserName_string_array_list.add("Owen Binchy");
//        UserName_string_array_list.add("Tony Hawk");
//        UserName_string_array_list.add("UserName 5");
//        UserName_string_array_list.add("UserName 6");
//        UserName_string_array_list.add("UserName 7");
//        UserName_string_array_list.add("UserName 8");
        
        

        // This is the array adapter, it takes the context of the activity as a 
        // first parameter, the type of list view as a second parameter and your 
        // array as a third parameter.
//        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(
//                this, 
////                android.R.layout.simple_list_item_1,
//                R.layout.user_list_item,
//                UserName_string_array_list );
////                R.layout.user_list_item);
//        
//        lv.setAdapter(arrayAdapter); 
        
        final ListView listview = (ListView) findViewById(R.id.listView);
        
        String[] values = new String[] { "Android", "iPhone", "WindowsMobile",
                "Blackberry", "WebOS", "Ubuntu", "Windows7", "Max OS X",
                "Linux", "OS/2", "Ubuntu", "Windows7", "Max OS X", "Linux",
                "OS/2", "Ubuntu", "Windows7", "Max OS X", "Linux", "OS/2",
                "Android", "iPhone", "WindowsMobile" };
        
        
        

            final ArrayList<String> list = new ArrayList<String>();
            for (int i = 0; i < values.length; ++i) {
              list.add(values[i]);
            }
            final StableArrayAdapter adapter = new StableArrayAdapter(this,
                android.R.layout.simple_list_item_1, list);
            listview.setAdapter(adapter);

            listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                @Override
                public void onItemClick(AdapterView<?> parent, final View view,
                    int position, long id) {
                  final String item = (String) parent.getItemAtPosition(position);
                  view.animate().setDuration(2000).alpha(0)
                      .withEndAction(new Runnable() {
                        @Override
                        public void run() {
                          list.remove(item);
                          adapter.notifyDataSetChanged();
                          view.setAlpha(1);
                        }
                      });
                }

              });

        

        
        
//     // Bind onclick event handler
//        lv.setOnItemClickListener(new OnItemClickListener() {
//					@Override
//					public void onItemClick(AdapterView<?> parent, View view,
//							int position, long id) {
//						
//						Log.d("List","position = " + String.valueOf(position));
//						Log.d("List","id 	   = " + String.valueOf(id));
//						
//						viewProfile(view, position);
//						
//					}
//        });
	}
	
	public void viewProfile(View view, int position) {
    	// View own profile
    	Intent intent = new Intent(this, DisplayProfileActivity.class);
    	// TODO get user id 
    	String profileID = UserName_string_array_list.get(position);
    	intent.putExtra(R.string.EXTRA_PREFIX + "userID", profileID);
    	intent.putExtra(R.string.EXTRA_PREFIX + "admin", false);
    	startActivity(intent);
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

	    @Override
	    public long getItemId(int position) {
	      String item = getItem(position);
	      return mIdMap.get(item);
	    }

	    @Override
	    public boolean hasStableIds() {
	      return true;
	    }

	  }
	
}
