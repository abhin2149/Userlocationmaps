package com.example.abhinav.userlocationmaps;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.abhinav.userlocationmaps.Models.Asset;
import com.example.abhinav.userlocationmaps.Models.Marker;
import com.example.abhinav.userlocationmaps.Utils.DbBitmapUtility;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;


import java.io.IOException;
import java.util.Calendar;
import java.util.Random;
import java.util.UUID;

public class AssetActivity extends AppCompatActivity {
    ImageView imageView;
    android.support.v7.widget.AppCompatEditText latitude;
    android.support.v7.widget.AppCompatEditText longitude;
    android.support.design.widget.FloatingActionButton CameraButton;
    EditText nameEditText;
    Button saveButton;
    LocationManager locationManager;
    LocationListener locationListener;
    SQLiteDatabase sqLiteDatabase;
    Uri filePath;
    SharedPreferences preferences;
    DatabaseReference myRef;
    StorageReference storage;
    private static final int pic_id = 123;

    public void getPhoto() {

        Intent in = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(in, 1);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == 1) {

            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                        && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
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
                //getPhoto();

            }
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_asset);
        preferences = this.getSharedPreferences("com.example.abhinav.userlocationmaps", Context.MODE_PRIVATE);
        imageView =  findViewById(R.id.click_image);
        CameraButton = findViewById(R.id.cameraButton);

        latitude=findViewById(R.id.latTextView);
        longitude=findViewById(R.id.lonTextView);

        saveButton=findViewById(R.id.saveButton);
        nameEditText = findViewById(R.id.description_edit);
        latitude.setText(String.format("%.3f", 28.610));
        longitude.setText(String.format("%.3f", 77.037));
        sqLiteDatabase = this.openOrCreateDatabase("OFFLINE_DATA", MODE_PRIVATE, null);
        // camera button --------------
        CameraButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                // Create the camera_intent ACTION_IMAGE_CAPTURE
                // it will open the camera for capture the image
                Intent camera_intent
                        = new Intent(MediaStore
                        .ACTION_IMAGE_CAPTURE);
                // Start the activity with camera_intent,
                // and request pic id
                startActivityForResult(camera_intent, pic_id);
            }
        });

        //----------------


        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // save to local DB
                BitmapDrawable drawable = (BitmapDrawable) imageView.getDrawable();
                Bitmap bitmap = drawable.getBitmap();
                final Marker marker;
                marker = new Marker(UUID.randomUUID().toString()
                        ,Double.parseDouble(latitude.getText().toString())
                        ,Double.parseDouble(longitude.getText().toString())
                        ,nameEditText.getText().toString()
                        ,"dummy"            // TODO insert the category
                        ,Calendar.getInstance().getTime().toString()
                        ,DbBitmapUtility.getBytes(bitmap));


                Log.i("marker id",marker.getId());

                sqLiteDatabase.beginTransaction();
                try {
                    ContentValues values = new ContentValues();
                    ContentValues cv = new ContentValues();
                    cv.put("id",marker.getId());
                    cv.put("latitude",marker.getLatitude());
                    cv.put("longitude",marker.getLongitude());
                    cv.put("description",marker.getDescription());
                    cv.put("category",marker.getCategory());
                    cv.put("time",marker.getTime());
                    cv.put("image",marker.getImage());
                    sqLiteDatabase.insert("local_assets",null,cv);
                    sqLiteDatabase.insert("assets",null,cv);
                    sqLiteDatabase.setTransactionSuccessful();

                } finally {
                    sqLiteDatabase.endTransaction();
                    Log.i("saved", "complete");
                    Toast.makeText(AssetActivity.this,"Your Asset has been saved",Toast.LENGTH_LONG).show();
                    Intent myIntent = new Intent(AssetActivity.this, DisplayAssets.class);
                    startActivity(myIntent);
                    finish();
                }


/*                myRef = FirebaseDatabase.getInstance().getReference().child("Assets");
                storage = FirebaseStorage.getInstance().getReference().child("Images");
                int random = new Random().nextInt(9999);
                storage.child(""+random).putFile(filePath).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        Asset asset = new Asset();
                        asset.setId(marker.getId());
                        asset.setDescription(marker.getDescription());
                        asset.setLatitude(marker.getLatitude());
                        asset.setLongitude(marker.getLongitude());
                        asset.setTime(marker.getTime());
                        asset.setImage(taskSnapshot.getMetadata().getName());
                        myRef.push().setValue(asset).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(AssetActivity.this,"Your Asset has been saved",Toast.LENGTH_LONG).show();
                                Intent myIntent = new Intent(AssetActivity.this, MainActivity.class);
                                startActivity(myIntent);
                                finish();
                            }
                        });
                    }
                });*/

            }
        });

        final ProgressDialog progressDoalog;
        progressDoalog = new ProgressDialog(AssetActivity.this);
        progressDoalog.setMax(100);
        progressDoalog.setMessage("Please wait...");
        progressDoalog.setTitle("Fetching location");
        progressDoalog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDoalog.setCancelable(false);
        // show it
        //progressDoalog.show();
        locationManager=(LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        locationListener=new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                Log.i("Latitude",String.valueOf(location.getLatitude()));
                Log.i("Longitude",String.valueOf(location.getLongitude()));
                latitude.setText(String.format("%.3f", location.getLatitude()));
                longitude.setText(String.format("%.3f", location.getLongitude()));
                //progressDoalog.dismiss();

            }

            @Override
            public void onStatusChanged(String s, int i, Bundle bundle) {
                Log.i("Location",s);
            }

            @Override
            public void onProviderEnabled(String s) {
                Log.i("Location",s);

            }

            @Override
            public void onProviderDisabled(String s) {
                Log.i("Location",s);

            }
        };
        if(checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION)!= PackageManager.PERMISSION_GRANTED) {

            requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.ACCESS_FINE_LOCATION}, 1);

        }
        else{
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
           // getPhoto();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        locationManager.removeUpdates(locationListener);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        if (requestCode == pic_id) {

            // BitMap is data structure of image file
            // which stor the image in memory
            if (data.hasExtra("data")){
                Bitmap photo = (Bitmap) data.getExtras().get("data");
                imageView.setImageBitmap(photo);
            }
        }

        if(requestCode==1 && resultCode==RESULT_OK && data!=null ){

            filePath = data.getData();
            try {

                Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(),filePath);
                int nh = (int) ( bitmap.getHeight() * (512.0 / bitmap.getWidth()) );
                Bitmap scaled = Bitmap.createScaledBitmap(bitmap, 512, nh, true);
                imageView.setImageBitmap(scaled);

            } catch (IOException e) {

                e.printStackTrace();

            }
        }
    }
}
