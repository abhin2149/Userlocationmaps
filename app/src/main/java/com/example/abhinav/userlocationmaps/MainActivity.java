package com.example.abhinav.userlocationmaps;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.UUID;


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
        Cursor cursor = sqLiteDatabase.rawQuery("SELECT * FROM markers", null);

        int idIndex = cursor.getColumnIndex("id");
        int latitudeIndex = cursor.getColumnIndex("latitude");
        int longitudeIndex = cursor.getColumnIndex("longitude");
        int descriptionIndex = cursor.getColumnIndex("description");
        int timeIndex = cursor.getColumnIndex("time");
        int imageIndex = cursor.getColumnIndex("image");

        cursor.moveToFirst();
        Log.i("records",cursor.getCount()+"  ");
        while(!cursor.isAfterLast()) {
            Log.i("id",cursor.getString(idIndex)+"");
            Log.i("latitude",cursor.getFloat(latitudeIndex)+"");
            Log.i("longitude",cursor.getFloat(longitudeIndex)+"");
            Log.i("description",cursor.getString(descriptionIndex)+"");
            Log.i("time",cursor.getString(timeIndex)+"");
            Log.i("image",cursor.getBlob(imageIndex)+"");
            cursor.moveToNext();
        }
        cursor.close();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        Button assetButton = findViewById(R.id.assetButton);
        Button trackButton = findViewById(R.id.trackButton);
        Button imuButton  =findViewById(R.id.imuButton);
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
