package com.geoalgorithm.algorithmtestapp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.support.v4.content.LocalBroadcastManager;

import com.geoalgorithm.library.AlgorithmControlTower;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;


/**
 * Receiver class to handle location updates, it will store the location in an array
 * and then notify the Activity that a new location has arrived.
 */
public class MyReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        //First we check if we have retrieved a valid location
        if(intent.getExtras().getBoolean(AlgorithmControlTower.HAS_LOCATION_TAG)) {

            //Now all we have to do is recover that location and add it to the array
            Location location = AlgorithmControlTower.getLastLocation(context);

            List<Location> mLocations = new ArrayList<>();

            SharedPreferences prefs = context.getSharedPreferences("MapsTest", Context.MODE_PRIVATE);
            String locationJson = prefs.getString("locationJson", "");

            if (!locationJson.equals("")) {

                Gson gson = new Gson();
                mLocations = gson.fromJson(locationJson, new TypeToken<ArrayList<Location>>() {}.getType());

            }

            mLocations.add(location);

            Gson gson = new GsonBuilder().create();

            locationJson = gson.toJson(mLocations, new TypeToken<List<Location>>() {
            }.getType());

            SharedPreferences.Editor editor = context.getSharedPreferences("MapsTest", Context.MODE_PRIVATE).edit();

            editor.putString("locationJson", locationJson).commit();

            //Now that we have everything stored, we'll notify the Activity to show the new Data
            LocalBroadcastManager.getInstance(context).sendBroadcast(new Intent(MapsActivity.NEW_LOCATION_RECEIVED));

        }
    }
}
