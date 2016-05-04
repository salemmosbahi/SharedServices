package it.mdev.sharedservices.activity;

import android.app.AlertDialog;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import it.mdev.sharedservices.R;
import it.mdev.sharedservices.util.Controllers;

/**
 * Created by salem on 29/04/16.
 */
public class PositionMAP extends Fragment {
    SharedPreferences pref;
    Controllers conf = new Controllers();
    //GPSTracker gps;
    MapView mMapView;
    private GoogleMap googleMap;
    Service service;
    protected LocationManager locationManager;// Declaring a Location Manager
    Location location; // location
    private CameraPosition cameraPosition;
    private double latitude, longitude;
    //private Boolean isStart = false;
    boolean isGPSEnabled = false;// flag for GPS status
    boolean isNetworkEnabled = false;// flag for network status
    boolean canGetLocation = false;// flag for GPS status
    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 3;// The minimum distance to change Updates in meters // 3 meters
    private static final long MIN_TIME_BW_UPDATES = 1000 * 3 * 1;// The minimum time between updates in milliseconds // 3 seconds


    public PositionMAP() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.map, container, false);
        pref = getActivity().getSharedPreferences(conf.app, Context.MODE_PRIVATE);

        mMapView = (MapView) v.findViewById(R.id.mapView);
        mMapView.onCreate(savedInstanceState);
        mMapView.onResume();

        try {
            MapsInitializer.initialize(getActivity().getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
        }

        googleMap = mMapView.getMap();
        googleMap.getUiSettings().setZoomControlsEnabled(true);
        getLocation();
        if(canGetLocation()){
            latitude = getLatitude();
            longitude = getLongitude();
        }else{
            //latitude = 0;
            //longitude = 0;
        }
        MarkerOptions x = new MarkerOptions().position(new LatLng(latitude, longitude)).title("Hello in Shared Services Maps");
        x.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
        googleMap.addMarker(x);
        cameraPosition = new CameraPosition.Builder().target(new LatLng(latitude, longitude)).zoom(10).build();
        googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
        googleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            public boolean onMarkerClick(Marker arg0) {
                arg0.remove();
                Toast.makeText(getActivity()
                        , "Remove Marker " + String.valueOf(arg0.getId())
                        , Toast.LENGTH_SHORT).show();
                return true;
            }
        });
        googleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            public void onMapClick(LatLng arg0) {
                googleMap.clear();
                latitude = arg0.latitude;
                longitude = arg0.longitude;

                SharedPreferences.Editor edit = pref.edit();
                edit.putString(conf.tag_latitude, latitude + "");
                edit.putString(conf.tag_longitude, longitude + "");
                edit.commit();

                googleMap.addMarker(new MarkerOptions().position(arg0)
                        .title("Hello in Shared Services Maps")
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));
            }
        });
        return v;
    }

    public Location getLocation() {
        try {
            locationManager = (LocationManager) getActivity().getSystemService(service.LOCATION_SERVICE);
            isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);// getting GPS status
            isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);// getting network status

            if (!isGPSEnabled) {// no GPS provider is enabled
                showSettingsAlert();
            } else {
                this.canGetLocation = true;
                if (isNetworkEnabled) {// First get location from Network Provider
                    if (locationManager != null) {
                        location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                        if (location != null) {
                            latitude = location.getLatitude();
                            longitude = location.getLongitude();
                        }
                    }
                }
                if (isGPSEnabled) {// if GPS Enabled get lat/long using GPS Services
                    if (location == null) {
                        if (locationManager != null) {
                            location = locationManager
                                    .getLastKnownLocation(LocationManager.GPS_PROVIDER);
                            if (location != null) {
                                latitude = location.getLatitude();
                                longitude = location.getLongitude();
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return location;
    }

    public void stopUsingGPS(){//Stop using GPS listener & Calling this function will stop using GPS in your app
        if(locationManager != null){
            locationManager = null;
        }
    }

    public double getLatitude(){//Function to get latitude
        if(location != null){
            latitude = location.getLatitude();
        }
        return latitude;
    }

    public double getLongitude(){//Function to get longitude
        if(location != null){
            longitude = location.getLongitude();
        }
        return longitude;
    }
    //Function to check GPS/wifi enabled
    public boolean canGetLocation() {
        return this.canGetLocation;
    }

    public void showSettingsAlert(){//Function to show settings alert dialog & On pressing Settings button will lauch Settings Options
        final AlertDialog.Builder builder =  new AlertDialog.Builder(getActivity());
        final String action = android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS;
        final String title = "GPS is settings";// Setting Dialog Title
        final String message = "GPS is not enabled. Do you want open GPS setting?";// Setting Dialog Message
        builder.setTitle(title).setMessage(message)
                .setPositiveButton("OK",
                        new DialogInterface.OnClickListener() {// On pressing Settings button
                            public void onClick(DialogInterface d, int id) {
                                getActivity().startActivity(new Intent(action));
                                d.dismiss();
                            }
                        })
                .setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {// on pressing cancel button
                            public void onClick(DialogInterface d, int id) {
                                d.cancel();
                            }
                        });
        builder.create().show();// Showing Alert Message
    }

    @Override
    public void onResume() {
        super.onResume();
        mMapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mMapView.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mMapView.onDestroy();
        stopUsingGPS();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mMapView.onLowMemory();
    }
}
