/**
 * Copyright 2010-present Facebook.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.sportsapp;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.BaseAdapter;
import com.facebook.model.OpenGraphAction;


/**
 * Base class for a list element in the Scrumptious main display, consisting of an
 * icon to the left, and a two line display to the right.
 */
public abstract class BaseListElement {

    private Drawable icon;
    private String text1;
    private String text2;
    private String text3;
    private String text4;
    private BaseAdapter adapter;

    /**
     * Constructs a new list element.
     *
     * @param icon the drawable for the icon
     * @param requestCode the requestCode to start new Activities with
     * @param text3 
     */
    public BaseListElement(Drawable icon, String text1, String text2, String text3, String text4, int requestCode) {
        this.icon = icon;
        this.text1 = text1;
        this.text2 = text2;
        this.text3 = text3;
        this.text4 = text4;
    }

    public BaseListElement(JSONObject eventObject, Drawable sportIcon) {
		// TODO Auto-generated constructor stub
    	// getActivity().getResources().getDrawable(R.drawable.ic_launcher)
    	this.icon  = sportIcon;
    	String eventName = null;
    	String eventLocation = null;
    	String eventDate = null;
    	String eventTime = null;
    	try{
	    	eventName = eventObject.getString("eventName");
	    	// TODO get readable location
	    	eventLocation = eventObject.getString("eventID");
	    	eventTime = eventObject.getString("startTime");
	    	eventDate = eventObject.getString("date");
    	}catch(JSONException e1){
    		e1.printStackTrace();
    	}
//    	if(date is within a week){
//    		eventDate = day;
//    	}
    	this.text1 = eventName;
    	this.text2 = eventLocation;
    	this.text3 = eventTime;
    	this.text4 = eventDate;
    	
	}

	/**
     * The Adapter associated with this list element (used for notifying that the
     * underlying dataset has changed).
     * @param adapter the adapter associated with this element
     */
    public void setAdapter(BaseAdapter adapter) {
        this.adapter = adapter;
    }
    public Drawable getIcon() {
        return icon;
    }
    public String getText1() {
        return text1;
    }
    public String getText2() {
        return text2;
    }
    public String getText3() {
        return text3;
    }
    public String getText4() {
        return text4;
    }

    public void setText1(String text1) {
        this.text1 = text1;
        if (adapter != null) {
            adapter.notifyDataSetChanged();
        }
    }

    public void setText2(String text2) {
        this.text2 = text2;
        if (adapter != null) {
            adapter.notifyDataSetChanged();
        }
    }
    
    public void setText3(String text3) {
        this.text3 = text3;
        if (adapter != null) {
            adapter.notifyDataSetChanged();
        }
    }
    
    public void setText4(String text4) {
        this.text4 = text4;
        if (adapter != null) {
            adapter.notifyDataSetChanged();
        }
    }

    /**
     * Returns the OnClickListener associated with this list element. To be
     * overridden by the subclasses.
     *
     * @return the OnClickListener associated with this list element
     */
    protected abstract View.OnClickListener getOnClickListener();

    /**
     * Populate an OpenGraphAction with the results of this list element.
     *
     * @param action the action to populate with data
     */
    protected abstract void populateOGAction(OpenGraphAction action);

    /**
     * Callback if the OnClickListener happens to launch a new Activity.
     *
     * @param data the data associated with the result
     */
    protected void onActivityResult(Intent data) {}

    /**
     * Save the state of the current element.
     *
     * @param bundle the bundle to save to
     */
    protected void onSaveInstanceState(Bundle bundle) {}

    /**
     * Restore the state from the saved bundle. Returns true if the
     * state was restored.
     *
     * @param savedState the bundle to restore from
     * @return true if state was restored
     */
    protected boolean restoreState(Bundle savedState) {
        return false;
    }

    /**
     * Notifies the associated Adapter that the underlying data has changed,
     * and to re-layout the view.
     */
    protected void notifyDataChanged() {
        adapter.notifyDataSetChanged();
    }

}
