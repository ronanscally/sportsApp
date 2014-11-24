package com.example.sportsapp;

import android.content.Intent;
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
  private GoogleMap map;
  
  private LatLng Location;
  private String EventName;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.show_map);
    
    // Get location from intent extra
    Intent intent = getIntent();
//    if (savedInstanceState != null) {
//    }else{
    	EventName 		=  intent.getStringExtra(R.string.EXTRA_PREFIX + "EventName");
		double lng 		=  intent.getDoubleExtra(R.string.EXTRA_PREFIX + "Lng", 0);
		double lat 		=  intent.getDoubleExtra(R.string.EXTRA_PREFIX + "Lat", 0);
		Location = new LatLng(lng,lat);
//    }
    
    map = ((MapFragment) getFragmentManager().findFragmentById(R.id.map))
        .getMap();
    
//    Marker hamburg = map.addMarker(new MarkerOptions().position(HAMBURG)
//        .title("Hamburg"));
//    Marker kiel = map.addMarker(new MarkerOptions()
//        .position(KIEL)
//        .title("Kiel")
//        .snippet("Kiel is cool")
//        .icon(BitmapDescriptorFactory
//            .fromResource(R.drawable.ic_launcher)));

    // Move the camera instantly to hamburg with a zoom of 15.
//    map.moveCamera(CameraUpdateFactory.newLatLngZoom(HAMBURG, 15));
    
    Marker eventLocation = map.addMarker(new MarkerOptions().position(Location)
        .title(EventName));
    map.moveCamera(CameraUpdateFactory.newLatLngZoom(Location, 15));

    // Zoom in, animating the camera.
    map.animateCamera(CameraUpdateFactory.zoomTo(10), 2000, null);
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    getMenuInflater().inflate(R.menu.show_map, menu);
    return true;
  }

}