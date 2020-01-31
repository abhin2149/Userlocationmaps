package com.example.abhinav.userlocationmaps;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i("before","Before content view");
        setContentView(R.layout.activity_main);

        Log.i("af","after content view");
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
                Intent myIntent = new Intent(MainActivity.this, AssetActivity.class);
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
