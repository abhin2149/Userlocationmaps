package com.example.abhinav.userlocationmaps;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private double lat, lon;
    LocationManager locationManager;
    LocationListener locationListener;
    SQLiteDatabase sqLiteDatabase;

    private static final float DEFAULT_ZOOM = 20f;

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {


            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.INTERNET) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }

            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);


        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        // recenter buttons ----------------------------------------

        LinearLayout MapButton3 = (LinearLayout) findViewById(R.id.map_button3);

        MapButton3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LatLng ulocal = new LatLng(lat, lon);
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(ulocal,15f));

            }
        });
        // map button done- --------------------

        // add button link to assets
        View AddButton = (View) findViewById(R.id.add_button);

        AddButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("New asset being added ","Clicked");
                Intent myIntent = new Intent(MapsActivity.this, AssetActivity.class);
                startActivity(myIntent);
                //finish();
            }
        });

        // ----------------- done ----------


        // ---------- List Buttons ----------------------

        LinearLayout ListButton3 = (LinearLayout) findViewById(R.id.listButton3);

        ListButton3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("Asset","Clicked");
                Intent myIntent = new Intent(MapsActivity.this, DisplayAssets.class);
                startActivity(myIntent);
            }
        });


        // ------------- List Button ends --------------------------
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
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

        // Add a marker in Sydney and move the camera

        locationManager=(LocationManager)this.getSystemService(Context.LOCATION_SERVICE);
        final ProgressDialog progressDoalog;
        progressDoalog = new ProgressDialog(MapsActivity.this);
        progressDoalog.setMax(100);
        progressDoalog.setMessage("Please wait...");
        progressDoalog.setTitle("Tracking location");
        progressDoalog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        // show it
        //progressDoalog.show();
        Toast.makeText(this,"Tracking has now started",Toast.LENGTH_LONG).show();


        //My Work***************************************


        sqLiteDatabase = this.openOrCreateDatabase("OFFLINE_DATA", MODE_PRIVATE, null);
        Cursor cursor = sqLiteDatabase.rawQuery("SELECT * FROM assets", null);
        int idIndex = cursor.getColumnIndex("id");
        int latitudeIndex = cursor.getColumnIndex("latitude");
        int longitudeIndex = cursor.getColumnIndex("longitude");
        int descriptionIndex = cursor.getColumnIndex("description");
        int categoryIndex = cursor.getColumnIndex("category");
        //int timeIndex = cursor.getColumnIndex("time");
        //int imageIndex = cursor.getColumnIndex("image");

        cursor.moveToFirst();
        while(!cursor.isAfterLast())
        {
            double lat = cursor.getDouble(latitudeIndex) , lng = cursor.getDouble(longitudeIndex);
            LatLng pos = new LatLng(lat,lng);
            String category = cursor.getString(categoryIndex),desc = cursor.getString(descriptionIndex);
            switch (category)
            {
                case "Herbivore": mMap.addMarker(new MarkerOptions().icon(BitmapDescriptorFactory.fromResource(R.drawable.b_w_herbivore)).position(pos).title(desc));
                break;
                case "Carnivore" :  mMap.addMarker(new MarkerOptions().icon(BitmapDescriptorFactory.fromResource(R.drawable.b_w_carnivore)).position(pos).title(desc));
                break;
                case "Plant": mMap.addMarker(new MarkerOptions().icon(BitmapDescriptorFactory.fromResource(R.drawable.b_w_plant)).position(pos).title(desc));
                break;
                case "Broken break-in": mMap.addMarker(new MarkerOptions().icon(BitmapDescriptorFactory.fromResource(R.drawable.b_w_broken_wall)).position(pos).title(desc));
                break;
                case "Bird": mMap.addMarker(new MarkerOptions().icon(BitmapDescriptorFactory.fromResource(R.drawable.b_w_bird)).position(pos).title(desc));
                break;
                case "Poachers": mMap.addMarker(new MarkerOptions().icon(BitmapDescriptorFactory.fromResource(R.drawable.b_w_poacher)).position(pos).title(desc));
                break;
                case "Reptile" : mMap.addMarker(new MarkerOptions().icon(BitmapDescriptorFactory.fromResource(R.drawable.b_w_reptile)).position(pos).title(desc));
                break;

                default: mMap.addMarker(new MarkerOptions().icon(BitmapDescriptorFactory.fromResource(R.drawable.b_w_herbivore)).position(pos).title(desc));
            }
            //mMap.addMarker(new MarkerOptions().icon(BitmapDescriptorFactory.fromResource(R.drawable.bird)).position(pos).title(desc));
            cursor.moveToNext();
        }
        cursor.close();
        //***********************************************

        LatLng ulocal = new LatLng(28.610, 77.037);
        //mMap.clear();
        //mMap.addMarker(new MarkerOptions().position(ulocal).title("You are here!"));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(ulocal,15f));
        locationListener=new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {

                Log.i("Location info",location.toString());
                //progressDoalog.dismiss();
                LatLng ulocal = new LatLng(location.getLatitude(), location.getLongitude());
                //mMap.clear();
                lat = location.getLatitude();
                lon = location.getLongitude();
                mMap.addMarker(new MarkerOptions().position(ulocal).title("You are here!"));
               // mMap.setMyLocationEnabled(true);
                //mMap.moveCamera(CameraUpdateFactory.newLatLng(ulocal));

                Geocoder geocoder=new Geocoder(getApplicationContext(), Locale.getDefault());

                try {

                    List<Address> addressList=geocoder.getFromLocation(location.getLatitude(),location.getLongitude(),1);

                    if(addressList.size()>0 && addressList!=null){

                        Log.i("locale",addressList.get(0).toString());

                        Toast.makeText(getApplicationContext(),"Street number:" + addressList.get(0).getLocality().toString() +
                                "Postal Code:" + addressList.get(0).getPostalCode().toString()+
                                "Address:" + addressList.get(0).getAddressLine(0).toString()+
                                "Country:" + addressList.get(0).getCountryName().toString(),Toast.LENGTH_LONG);

                    }


                }


                catch (IOException e) {

                    e.printStackTrace();
                }


            }

            @Override
            public void onStatusChanged(String s, int i, Bundle bundle) {

            }

            @Override
            public void onProviderEnabled(String s) {

            }

            @Override
            public void onProviderDisabled(String s) {

            }
        };


        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)!= PackageManager.PERMISSION_GRANTED){


            ActivityCompat.requestPermissions(this,new String []{Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE},1);



        } else{

            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 5000, 0, locationListener);
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 0, locationListener);
        }
    }
}