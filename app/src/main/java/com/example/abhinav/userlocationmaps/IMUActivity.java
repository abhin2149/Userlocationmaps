package com.example.abhinav.userlocationmaps;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.support.v7.app.AppCompatActivity;
import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.w3c.dom.Text;

import java.io.IOException;


public class IMUActivity extends AppCompatActivity implements SensorEventListener {


    private TextView xText, yText, zText, gyroData;
    private SensorManager SM;
    private Sensor mySensor;
    private Sensor gyroSenser;
    private SensorEventListener gyroscopeEventListener;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_imu);


        SM = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mySensor = SM.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        boolean b = SM.registerListener(this, mySensor, SensorManager.SENSOR_DELAY_NORMAL);
        gyroSenser = SM.getDefaultSensor((Sensor.TYPE_GYROSCOPE));

        if (gyroSenser == null){
            Toast.makeText(this, "Gyroscope isn't available on this device", Toast.LENGTH_SHORT).show();
             finish();
        }

        gyroscopeEventListener = new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent sensorEvent) {

                //positive clockwise
                gyroData.setText(" GYRO data : \n" +sensorEvent.values[0]+"\n "+sensorEvent.values[1]+"\n "+sensorEvent.values[2]+"\n");

            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int i) {

            }
        };

        //Assign Text
        xText = (TextView)findViewById(R.id.xText);
        yText = (TextView)findViewById(R.id.yText);
        zText = (TextView)findViewById(R.id.zText);
        gyroData = (TextView)findViewById(R.id.gyroData);


    }

    @Override
    protected void onResume() {
        super.onResume();
        SM.registerListener(gyroscopeEventListener,gyroSenser, SM.SENSOR_DELAY_FASTEST);

    }

    @Override
    protected void onPause() {
        super.onPause();
        SM.unregisterListener(gyroscopeEventListener);
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {

        //sensor acceleration
        if(sensorEvent.values[0] != 0 )
            xText.setText("X : " + sensorEvent.values[0]+" m/s^2");
        if(sensorEvent.values[1] != 0 )
           yText.setText("Y : " + sensorEvent.values[1]+" m/s^2");
        if(sensorEvent.values[2] != 0 )
            zText.setText("Z : " + sensorEvent.values[2]+" m/s^2");
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {
    // not used
    }
}