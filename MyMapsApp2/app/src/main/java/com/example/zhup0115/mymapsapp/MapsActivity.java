package com.example.zhup0115.mymapsapp;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;


import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        //title but first must extend the appcompat
        // getSupportActionBar().setTitle("Maps Project");
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {

        mMap = googleMap;
        googleMap.setMyLocationEnabled(true);
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            googleMap.setMyLocationEnabled(true);
        }
        // Add a marker in Sydney and move the camera
        LatLng birthPlace = new LatLng(32.7157, -117.161);
        mMap.addMarker(new MarkerOptions().position(birthPlace).title("Born Here"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(birthPlace));

        mMap.setMyLocationEnabled(true);
    }


    private int numClicks = 0;

    public void changeView(View v) {
        numClicks++;

        if (numClicks % 2 == 0) {
            mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
        } else {
            mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        }
    }

    private LocationManager locationManager;
    private boolean isGPSenabled = false;
    private boolean isNetworkEnabled = false;
    private boolean canGetLocation = false;
    private static final long MIN_TIME_BW_UPDATES = 1000 * 15 * 1;
    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 5;

    public void getLocation() {
        try {
            locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

            //get GPS status
            isGPSenabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
            if (isGPSenabled) Log.d("MyMaps", "getLocation:GPS is enabled");

            //get network status
            isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
            if (isNetworkEnabled) Log.d("MyMaps", "getLocation:Network is enabled");

            if (!isGPSenabled && !isNetworkEnabled) {
                Log.d("MyMaps", "No Provider is Enabled");

            } else {
                this.canGetLocation = true;
                if (isNetworkEnabled) {
                    Log.d("MyMaps", "getLocation: network enabled and requesting location updates");
                    locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, MIN_TIME_BW_UPDATES, MIN_DISTANCE_CHANGE_FOR_UPDATES, locationListenerNetwork);
                    Log.d("MyMaps", "getLocation: NetworkLoc update request was successful");
                    Toast.makeText(this, "UsingNetwork", Toast.LENGTH_SHORT);
                }
                if (isGPSenabled) {
                    Log.d("MyMaps", "getLocation: GPS enabled and requesting location updates");
                    if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        // TODO: Consider calling
                        //    ActivityCompat#requestPermissions
                        // here to request the missing permissions, and then overriding
                        //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                        //                                          int[] grantResults)
                        // to handle the case where the user grants the permission. See the documentation
                        // for ActivityCompat#requestPermissions for more details.
                        return;
                }
                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, MIN_TIME_BW_UPDATES, MIN_DISTANCE_CHANGE_FOR_UPDATES, locationListenerGPS);
                    Log.d("MyMaps", "getLocation: GPSLoc update request was successful");
                    Toast.makeText(this, "UsingGPS", Toast.LENGTH_SHORT);
                }
            }

        } catch (Exception e) {
            Log.d("MyMaps", "Caught Exception in getLocation");
            e.printStackTrace();
        }
    }

    public void dropAMarker(){
        mMap.addMarker(new MarkerOptions()
                .position(new LatLng(10, 10))
                .title("Dropped Marker Here"));
    }

    android.location.LocationListener locationListenerGPS = new android.location.LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            //output in Log.d and Toast that GPS is enabled and working
            Log.d("MyMaps", "GPS Enabled");
            Toast.makeText(MapsActivity.this,"GPS Enabled", Toast.LENGTH_SHORT).show();

            //drop a marker on the map - create a method called dropAMarker

            //remove the network location update hint: see locationManager for update removal method

        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
            //output in Log.d and Toast that GPS is enabled and working
            Log.d("MyMaps", "GPS Enabled");
            Toast.makeText(MapsActivity.this,"GPS Enabled", Toast.LENGTH_SHORT).show();

            //setup a switch statement to check the status input parameter
            //case LocationProvider.AVAILABLE --> output message to Log.d and toast
            //case LocationProvider.OUT_OF_SERVICE --> request updates from network provider
            //case LocationProvider.TEMPORARILY_UNAVAILABLE --> request updates from network provider
            //case default --> request updates from network provider
            switch (status) {
                case 1:  status = LocationProvider.AVAILABLE;
                    Log.d("MyMaps", "Location Provider Available");
                    Toast.makeText(MapsActivity.this,"Location Provider Available", Toast.LENGTH_SHORT).show();
                    break;
                case 2:  status = LocationProvider.OUT_OF_SERVICE;
                    locationManager.requestLocationUpdates(
                            LocationManager.NETWORK_PROVIDER,
                            MIN_TIME_BW_UPDATES,
                            MIN_DISTANCE_CHANGE_FOR_UPDATES,
                            new getLocation());                                 //PROBLEM

                    break;
                case 3:  status = LocationProvider.TEMPORARILY_UNAVAILABLE;

                    break;
                default:

                    break;
            }
        }

        @Override
        public void onProviderEnabled(String provider) {
        }

        @Override
        public void onProviderDisabled(String provider) {
        }
    };

    android.location.LocationListener locationListenerNetwork = new android.location.LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            //output in Log.d and toast that Network is enabled and working
            Log.d("MyMaps", "Network Enabled");
            Toast.makeText(MapsActivity.this,"Network Enabled", Toast.LENGTH_SHORT).show();

            //Drop a marker on map - create a method called Drop a marker

            //relaunch the network provider request (requestLocationUpdates (NETWORK_PROVIDER))
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
            //output message in Log.d and Toast
            Log.d("MyMaps", "Network Enabled");
            Toast.makeText(MapsActivity.this,"Network Enabled", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onProviderEnabled(String provider) {
        }

        @Override
        public void onProviderDisabled(String provider) {
        }
    };
}

