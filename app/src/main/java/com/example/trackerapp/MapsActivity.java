package com.example.trackerapp;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import com.directions.route.AbstractRouting;
import com.directions.route.Route;
import com.directions.route.RouteException;
import com.directions.route.Routing;
import com.directions.route.RoutingListener;
import com.example.trackerapp.network.DirectionResponse;
import com.example.trackerapp.network.JsonPlaceHolderApi;
import com.example.trackerapp.network.POLYline;
import com.example.trackerapp.network.RetrofitClientInstance;
import com.example.trackerapp.network.StepResponse;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback,GoogleMap.OnMapLongClickListener, RoutingListener {

    private GoogleMap mMap;
    private FusedLocationProviderClient client;
    private LocationRequest locationRequest;
    private Marker marker;
    private JsonPlaceHolderApi jsonPlaceHolderApi;
    private List<Polyline> polylines=null;

    private Geocoder geocoder;
    private double Lat;
    private double Lon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.

        geocoder =new Geocoder(this);

        locationRequest = LocationRequest.create();
        locationRequest.setInterval(500);
        locationRequest.setFastestInterval(500);
        locationRequest.setPriority(locationRequest.PRIORITY_HIGH_ACCURACY);



        client = LocationServices.getFusedLocationProviderClient(this);

        if(ActivityCompat.checkSelfPermission(MapsActivity.this, Manifest.permission.ACCESS_FINE_LOCATION)== PackageManager.PERMISSION_GRANTED){
            checkSettingsAndStartLocationUpdates();
        }else {
            ActivityCompat.requestPermissions(MapsActivity.this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION},4);
        }

    }

    private void checkSettingsAndStartLocationUpdates() {
        LocationSettingsRequest request = new LocationSettingsRequest.Builder()
                .addLocationRequest(locationRequest).build();
        SettingsClient client = LocationServices.getSettingsClient(this);
        Task<LocationSettingsResponse> locationSettingsResponseTask = client.checkLocationSettings(request);
        locationSettingsResponseTask.addOnSuccessListener(new OnSuccessListener<LocationSettingsResponse>() {
            @Override
            public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
                getCurrentLocation();
            }
        });
        locationSettingsResponseTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                if (e instanceof ResolvableApiException) {
                    ResolvableApiException apiException = (ResolvableApiException) e;
                    try {
                        apiException.startResolutionForResult(MapsActivity.this, 1001);
                    } catch (IntentSender.SendIntentException ex) {
                        ex.printStackTrace();
                    }
                }
            }
        });

    }

    LocationCallback locationCallback = new LocationCallback(){
        @Override
        public void onLocationResult(LocationResult locationResult) {
            super.onLocationResult(locationResult);
            if (mMap != null) {
                setCurrentLocationMarker(locationResult.getLastLocation());
            }
        }
    };

    @SuppressLint("MissingPermission")
    private void setCurrentLocationMarker(Location location) {
        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
        Lat=location.getLatitude();
        Lon=location.getLongitude();

        if(marker==null){
            MarkerOptions markerOptions = new MarkerOptions();
            markerOptions.position(latLng);
            markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.redcar));
            markerOptions.rotation(location.getBearing());
            markerOptions.anchor((float) 0.5, (float) 0.5);
            marker= mMap.addMarker(markerOptions);
            mMap.setMyLocationEnabled(true);
            Log.d("Tag","zoomed !!");
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 17));
        }else{
            marker.setPosition(latLng);
            marker.setRotation(location.getBearing());
//            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 17));
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            client.requestLocationUpdates(locationRequest,locationCallback, Looper.myLooper());
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        client.removeLocationUpdates(locationCallback);
    }

    private void getCurrentLocation() {
        @SuppressLint("MissingPermission") Task<Location> task =client.getLastLocation();
        task.addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if(location!=null){
                    Lat=location.getLatitude();
                    Lon=location.getLongitude();
                    SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                            .findFragmentById(R.id.map);
                    mapFragment.getMapAsync(MapsActivity.this);
                }

            }
        });
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
    @SuppressLint("MissingPermission")
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        mMap.setOnMapLongClickListener(this);

        // Add a marker in Sydney and move the camera
//        LatLng current = new LatLng(Lat, Lon);
//        MarkerOptions options =new MarkerOptions().position(current)
//                .title("Marker in Kolkata");
//        mMap.moveCamera(CameraUpdateFactory.newLatLng(current));

//        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(current,15));
//    mMap.setMyLocationEnabled(true);
//        mMap.addMarker(options);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode==4){
            if(grantResults.length>0 && grantResults[0]==PackageManager.PERMISSION_GRANTED)
                getCurrentLocation();
        }
    }

    @Override
    public void onMapLongClick(LatLng latLng) {
        try {
            List<Address> addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1);
            if (addresses.size() > 0) {
                Address address = addresses.get(0);
                String streetAddress = address.getAddressLine(0);
                mMap.addMarker(new MarkerOptions()
                        .position(latLng)
                        .title(streetAddress)
                        .draggable(true)
                );
//                mMap.addPolyline(new PolylineOptions()
//                .add(new LatLng(Lat,Lon),latLng)
//                .width(10f)
//                .color(Color.BLUE)
//                .geodesic(true)
//                );
//                Retrofit retrofit = RetrofitClientInstance.getRetrofitInstance();
//                jsonPlaceHolderApi = retrofit.create(JsonPlaceHolderApi.class);
//
//                Call<DirectionResponse> call =jsonPlaceHolderApi.getDirection(Lat,Lon,latLng.latitude,latLng.longitude);
//                call.enqueue(new Callback<DirectionResponse>() {
//                    @Override
//                    public void onResponse(Call<DirectionResponse> call, Response<DirectionResponse> response) {
//                        if(response.isSuccessful()) {
//                            POLYline polYline = response.body().getRouteResponse().getLegsResponse().getStepResponse().getPolyline();
//                            List<String>  points= polYline.getPointsList();
//
//                        }
//                    }
//
//                    @Override
//                    public void onFailure(Call<DirectionResponse> call, Throwable t) {
//                        Toast.makeText(MapsActivity.this,t.getMessage(),Toast.LENGTH_SHORT).show();
//                    }
//                });
                Routing routing = new Routing.Builder()
                        .travelMode(Routing.TravelMode.DRIVING)
                        .withListener(this)
                        .alternativeRoutes(true)
                        .waypoints(new LatLng(Lat,Lon),latLng)
                        .key("AIzaSyBdicgnEWaT0DAokMVL69Rl0jAygCdpC_M")
                        .build();
                routing.execute();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @Override
    public void onRoutingFailure(RouteException e) {
        Toast.makeText(MapsActivity.this,"Failed to update route "+ e.getMessage(),Toast.LENGTH_LONG).show();
    }

    @Override
    public void onRoutingStart() {
        Toast.makeText(MapsActivity.this,"Finding Route...",Toast.LENGTH_LONG).show();
    }

    @Override
    public void onRoutingSuccess(ArrayList<Route> arrayList, int short1) {
        Toast.makeText(MapsActivity.this,"Success.",Toast.LENGTH_LONG).show();
        CameraUpdate center = CameraUpdateFactory.newLatLng(new LatLng(Lat,Lon));
        CameraUpdate zoom = CameraUpdateFactory.zoomTo(16);
        if(polylines!=null) {
            polylines.clear();
        }
        PolylineOptions polyOptions = new PolylineOptions();

        for (int i = 0; i <arrayList.size(); i++) {

            if (i == short1) {
                polyOptions.color(getResources().getColor(R.color.colorAccent));
                polyOptions.width(10);
                polyOptions.addAll(arrayList.get(short1).getPoints());
                Polyline polyline = mMap.addPolyline(polyOptions);
                mMap.addPolyline(polyOptions);

            }
        }

    }

    @Override
    public void onRoutingCancelled() {
        Toast.makeText(MapsActivity.this,"Re-route",Toast.LENGTH_LONG).show();
    }
}