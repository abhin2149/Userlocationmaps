package com.example.abhinav.userlocationmaps;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.abhinav.userlocationmaps.Models.Marker;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.gson.JsonObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.UUID;

class SaveLastLocationThread extends Thread
{
    SQLiteDatabase sqLiteDatabase;
    Cursor cursor;
    float lastLatitude;
    float lastLongitude;

    SaveLastLocationThread(SQLiteDatabase sqLiteDatabase){
        this.sqLiteDatabase = sqLiteDatabase;
    }

    public boolean isOnline() {
        Runtime runtime = Runtime.getRuntime();
        try {
            Process ipProcess = runtime.exec("/system/bin/ping -c 1 8.8.8.8");
            int     exitValue = ipProcess.waitFor();
            return (exitValue == 0);
        }
        catch (IOException e)          { e.printStackTrace(); }
        catch (InterruptedException e) { e.printStackTrace(); }

        return false;
    }

    public void run() {
        while(true){
            cursor = sqLiteDatabase.rawQuery("SELECT * FROM local_markers", null);
            int latitudeIndex = cursor.getColumnIndex("latitude");
            int longitudeIndex = cursor.getColumnIndex("longitude");
            cursor.moveToFirst();
            float latitude = cursor.getFloat(latitudeIndex);
            float longitude = cursor.getFloat(longitudeIndex);

            // check for internet connection and update if required
            if(this.isOnline() && (latitude!=lastLatitude || longitude!=lastLongitude)) {
                // TODO write code to save to firebase database
                // TODO save latitude and longitude to firebase server

                lastLatitude = latitude;
                lastLongitude = longitude;

                Log.i("lastlocation","last location stored");
            }else{
                Log.i("lastlocation","last location failed due to no internet or no new location");
            }
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}

public class MainActivity extends AppCompatActivity {
    private SQLiteDatabase sqLiteDatabase;

    @Override
    protected void onStart() {
        super.onStart();
        //initialize database

        sqLiteDatabase = this.openOrCreateDatabase("OFFLINE_DATA", MODE_PRIVATE, null);

        sqLiteDatabase.execSQL("CREATE TABLE IF NOT EXISTS markers (" +
                "id VARCHAR PRIMARY KEY, " +
                "latitude FLOAT, " +
                "longitude FLOAT, " +
                "description VARCHAR, " +
                "time VARCHAR, " +
                "image BLOB)");

        sqLiteDatabase.execSQL("CREATE TABLE IF NOT EXISTS local_markers (" +
                "id VARCHAR PRIMARY KEY, " +
                "latitude FLOAT, " +
                "longitude FLOAT, " +
                "description VARCHAR, " +
                "time VARCHAR, " +
                "image BLOB)");

        // Anirudh code begins
        SaveLastLocationThread saveLastLocationThread = new SaveLastLocationThread(sqLiteDatabase);
        saveLastLocationThread.run();
        // Anirudh code ends


        // Code to write to database
        /*sqLiteDatabase.beginTransaction();
        try {
            ContentValues values = new ContentValues();
            ContentValues cv = new ContentValues();
            cv.put("latitude",27.89);
            cv.put("longitude",78.99);
            sqLiteDatabase.insert("markers",null,cv);
            sqLiteDatabase.setTransactionSuccessful();
        } finally {
            sqLiteDatabase.endTransaction();
            Log.i("saved","complete");
        }*/

        //Code to read from database
        Cursor cursor = sqLiteDatabase.rawQuery("SELECT * FROM local_markers", null);

        int idIndex = cursor.getColumnIndex("id");
        int latitudeIndex = cursor.getColumnIndex("latitude");
        int longitudeIndex = cursor.getColumnIndex("longitude");
        int descriptionIndex = cursor.getColumnIndex("description");
        int timeIndex = cursor.getColumnIndex("time");
        int imageIndex = cursor.getColumnIndex("image");

        cursor.moveToFirst();
        Log.i("records",cursor.getCount()+"  ");
        //Marker marker = new Marker();
        while(!cursor.isAfterLast()) {
            Log.i("id",cursor.getString(idIndex)+"");
            Log.i("latitude",cursor.getFloat(latitudeIndex)+"");
            Log.i("longitude",cursor.getFloat(longitudeIndex)+"");
            Log.i("description",cursor.getString(descriptionIndex)+"");
            Log.i("time",cursor.getString(timeIndex)+"");
            Log.i("image",cursor.getBlob(imageIndex)+"");
/*
            marker.setId(cursor.getString(idIndex));
            marker.setDescription(cursor.getString(descriptionIndex));
            marker.setImage(cursor.getBlob(imageIndex));
            marker.setLatitude(cursor.getDouble(latitudeIndex));
            marker.setLongitude(cursor.getDouble(longitudeIndex));
            marker.setTime(cursor.getString(timeIndex));*/
            cursor.moveToNext();
        }
        cursor.close();
       /* FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference().child(preferences.getString("id","assets"));
        marker.setImage(null);
        myRef.push().setValue(marker);*/


        // Download files from Storage

        /*StorageReference storage = FirebaseStorage.getInstance().getReference().child("Images").child("2071");

        File rootPath = new File(Environment.getExternalStorageDirectory(), "images");
        if(!rootPath.exists()) {
            rootPath.mkdirs();
        }
        final File localFile = new File(rootPath,"image.jpg");
        storage.getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                Log.i("downloaded", Uri.fromFile(localFile).toString());
            }
        });*/


    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SharedPreferences preferences = this.getSharedPreferences("com.example.abhinav.userlocationmaps", Context.MODE_PRIVATE);
        if(!preferences.contains("id"))
            preferences.edit().putString("id",UUID.randomUUID().toString()).apply();
        Log.i("id",preferences.getString("id","id"));



        Button assetButton = findViewById(R.id.assetButton);
        Button trackButton = findViewById(R.id.trackButton);
        Button imuButton  = findViewById(R.id.imuButton);
        // Write a message to the database

        imuButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               // Toast.makeText(MainActivity.this,"Coming Soon!",Toast.LENGTH_LONG).show();
                Log.i("IMU Button","Clicked");
                Intent myIntent = new Intent(MainActivity.this, IMUActivity.class);
                startActivity(myIntent);
            }
        });
        assetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i("Asset","Clicked");
                Intent myIntent = new Intent(MainActivity.this, DisplayAssets.class);
                startActivity(myIntent);

            }
        });
        trackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i("Track","Clicked");
                Intent myIntent = new Intent(MainActivity.this, MapsActivity.class);
                startActivity(myIntent);
            }
        });
    }


}
