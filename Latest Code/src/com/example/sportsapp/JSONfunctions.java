package com.example.sportsapp;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.AsyncTask;
import android.util.Log;

//public abstract class JSONfunctions extends AsyncTask<String, Integer, Long> {

//protected Long doInBackground(String url) {
//public static JSONObject getJSONfromURL(String url) {


public class JSONfunctions extends AsyncTask<String, Integer, Long> {
	
	private static JSONArray Response = null;
	private static JSONObject Request  = null;
	private static boolean NewResponse = false;
	
	
	@Override
	protected Long doInBackground(String... urls) {
		NewResponse = false;
		Response = null;
		InputStream is = null;
	    String result = "";
	    JSONArray jArray = null;

	    // Download JSON data from URL
	    try {
	        HttpClient httpclient = new DefaultHttpClient();
	        HttpPost httppost = new HttpPost(urls[0]);
	        
	        Log.d("JSON","Attempting to http...");
	        
	        StringEntity params =new StringEntity(Request.toString());
	        httppost.addHeader("content-type", "application/x-www-form-urlencoded");
	        httppost.setEntity(params);
	        Log.d("JSON","Params: " + params);
	        Log.d("JSON","URL: " + urls[0]);
	        
	        HttpResponse response = httpclient.execute(httppost);
	        HttpEntity entity = response.getEntity();
	        is = entity.getContent();

	    } catch (Exception e) {
	        Log.e("JSON", "Error in http connection " + e.toString());
	    }

	    // Convert response to string
	    try {
	        BufferedReader reader = new BufferedReader(new InputStreamReader(
	                is, "iso-8859-1"), 8);
	        StringBuilder sb = new StringBuilder();
	        String line = null;
	        while ((line = reader.readLine()) != null) {
	            sb.append(line + "\n");
	        }
	        is.close();
	        result = sb.toString();
	    } catch (Exception e) {
	        Log.e("JSON", "Error converting result " + e.toString());
	    }
//	    try {
//	    	Log.d("Result",result);
//	    	JSONArray mJsonArray = new JSONArray(result);
//	    	Log.d("mJsonArray",mJsonArray.toString());
//	    	JSONObject mJsonObject = new JSONObject();
//	    	for (int i = 0; i < mJsonArray.length(); i++) {
//	    	    mJsonObject = mJsonArray.getJSONObject(i);
//	    	    Log.d("mJsonObject",mJsonObject.toString());
//	    	}
//	    }catch (JSONException e){
//	    	Log.e("JSON", "Error parsing data " + e.toString());
//	    }
	    Log.d("JSON","result string: " + result);
	    try {
	        jArray = new JSONArray(result);
	        Log.d("JSON","Received object is: " + jArray.toString());
	    } catch (JSONException e) {
	        Log.d("JSON", "Error parsing data " + e.toString());
	    }
	    Response = jArray;
	    NewResponse = true;
		return null;
	}	
	
	public static JSONArray getResponseArray(){
		// TODO Make this better... use the onPostExecute() function..
		NewResponse = false;
		return Response; 
	}
	
	public static void clearResponseBuffer(){
		// TODO Make this better... use the onPostExecute() function..
		NewResponse = false; 
	}
	
	public static boolean checkNewResponse(){
		// TODO Make this better... use the onPostExecute() function..
		return NewResponse;
	}
	
	public static void setRequestObject(JSONObject request){
		Request = request;
		Log.d("JSON","Request in class: " + Request.toString());
	}
	
	protected void onProgressUpdate(Integer... progress) {
//        setProgressPercent(progress[0]);
    }

    protected void onPostExecute(Long result) {
//        showDialog("Downloaded " + result + " bytes");
    	
    }
	
	
	}