package com.geoalgorithm.algorithmtestapp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.location.Location;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.tuillo.algorithmlibrary.AlgorithmControlTower;
import com.tuillo.algorithmlibrary.AlgorithmOptions;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MapsActivity extends FragmentActivity {

    private GoogleMap mMap; // Might be null if Google Play services APK is not available.
    private List<Location> mLocations;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        setUpMapIfNeeded();
    }

    @Override
    protected void onResume() {
        super.onResume();
        setUpMapIfNeeded();

        //We register our location receiver
        LocalBroadcastManager.getInstance(this).registerReceiver(mLocationReceiver, new IntentFilter(AlgorithmControlTower.LIB_INTENT_ACTION));

    }

    /**
     * Sets up the map if it is possible to do so (i.e., the Google Play services APK is correctly
     * installed) and the map has not already been instantiated..
     *
     * Also, we initialize the Library with our provided API Key
     */
    private void setUpMapIfNeeded() {
        if (mMap == null) {

            mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map))
                    .getMap();

            if(mLocations == null && mMap != null) {
                SharedPreferences prefs = getSharedPreferences("MapsTest",Context.MODE_PRIVATE);
                String locationJson = prefs.getString("locationJson", "");

                if(!locationJson.equals("")){

                    Gson gson = new Gson();
                    mLocations = gson.fromJson(locationJson,new TypeToken<ArrayList<Location>>(){}.getType());

                }
                if(mLocations == null){
                    mLocations = new ArrayList<>();
                }

                for(Location location : mLocations) {
                    mMap.addMarker(new MarkerOptions().position(new LatLng(location.getLatitude(),
                            location.getLongitude())).title("Location with accuracy "+location.getAccuracy()+" taken at "+new Date(location.getTime())));
                }

                /**
                 * This is where we initialize the geoAlgorithm library with our supplied API Key, also, we'll be setting our preferences
                 * by passing an AlgorithmOptions instance with bad and good accuracy scenario intervals, only get updates when online
                 * and without GingerBread compatibility.
                 */
                AlgorithmControlTower.initWithOptions(getApplicationContext(), "<YOUR_KEY_HERE>", new AlgorithmOptions(10 * 60 * 1000, 60 * 1000, 1000, true, false));

                /**
                 * We also enable debug logging in order to read important information
                 */
                AlgorithmControlTower.enableDebugLogs(true);

                if(mLocations.size()>0) {

                    Location location = mLocations.get(mLocations.size()-1);

                    addMarker(location);

                    moveCamera(location);
                }
            }
        }

    }


    /**
     * We configure our BroadcastReceiver in order to store our new location and place a marker on our map
     */
    private final BroadcastReceiver mLocationReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {

            if(intent.getAction().equals(AlgorithmControlTower.LIB_INTENT_ACTION)){

                //We make sure that we have a valid location
                if(intent.getExtras().getBoolean(AlgorithmControlTower.HAS_LOCATION_TAG) && mMap != null){

                    //And now we can work with our retrieved location by calling getLastLocation
                    Location location = AlgorithmControlTower.getLastLocation(MapsActivity.this);

                    mLocations.add(location);

                    Gson gson = new GsonBuilder().create();

                    String locationJson = gson.toJson(mLocations, new TypeToken<List<Location>>() {
                    }.getType());

                    SharedPreferences.Editor editor = getSharedPreferences("MapsTest", Context.MODE_PRIVATE).edit();

                    editor.putString("locationJson",locationJson).commit();

                    addMarker(location);

                    moveCamera(location);

                }


            }

        }


    };

    /**
     * Simple method that will move the camera to a location in our map
     * @param location
     */
    private void moveCamera(Location location){

        CameraUpdate center=
                CameraUpdateFactory.newLatLng(new LatLng(location.getLatitude(),
                        location.getLongitude()));
        CameraUpdate zoom=CameraUpdateFactory.zoomTo(15);

        mMap.moveCamera(center);

        mMap.animateCamera(zoom);

    }

    /**
     * Simple method that will add a marker on our map
     * @param location
     */
    private void addMarker(Location location){

        mMap.addMarker(new MarkerOptions().position(new LatLng(location.getLatitude(),
                location.getLongitude())).title("Location with accuracy "+location.getAccuracy()+" taken at "+new Date(location.getTime())));

    }
}
