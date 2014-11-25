package com.example.sportsapp;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Menu;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class ShowMap extends FragmentActivity {
  static final LatLng HAMBURG = new LatLng(53.558, 9.927);
  static final LatLng KIEL = new LatLng(53.551, 9.993);
  
  
  private double longitude = 0.0;
  private double latitude = 0.0;
  private LatLng Event = null;
  
  private GoogleMap map;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
	  
	  
	Bundle extras = getIntent().getExtras();
	if(extras != null) {
		longitude = extras.getDouble("longitude");
		latitude = extras.getDouble("latitude");
		Log.d("LOG_TAG", "lat" + latitude);
		Log.d("LOG_TAG", "long" + longitude);
		
	}
	  
	Event = new LatLng(latitude, longitude);
	
	  
	 Log.d("LOG_TAG","onCreate function called for Maps_Activity");  
    super.onCreate(savedInstanceState);
    setContentView(R.layout.show_map);
    
    Log.d("LOG_TAG","started intent");
    
    map = ((MapFragment) getFragmentManager().findFragmentById(R.id.map))
        .getMap();
    
    Log.d("LOG_TAG","got past map");
    
    Marker hamburg = map.addMarker(new MarkerOptions().position(Event)
        .title("Soccer in UCD"));
    Marker kiel = map.addMarker(new MarkerOptions()
        .position(KIEL)
        .title("Kiel")
        .snippet("Kiel is cool")
        .icon(BitmapDescriptorFactory
            .fromResource(R.drawable.ic_launcher)));

    // Move the camera instantly to hamburg with a zoom of 15.
    map.moveCamera(CameraUpdateFactory.newLatLngZoom(Event, 15));

    // Zoom in, animating the camera.
    map.animateCamera(CameraUpdateFactory.zoomTo(10), 2000, null);
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    getMenuInflater().inflate(R.menu.show_map, menu);
    return true;
  }

}