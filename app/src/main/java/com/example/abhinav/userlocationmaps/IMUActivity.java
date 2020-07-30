package com.example.abhinav.userlocationmaps;

import android.app.Activity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.Viewport;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.util.ArrayList;

public class IMUActivity extends Activity implements SensorEventListener{
    ArrayList<Double> xaxis=new ArrayList<Double>();
    ArrayList<Double>yaxis=new ArrayList<Double>();
    ArrayList<Double>zaxis=new ArrayList<Double>();

    private TextView xText, yText, zText, gyroData;
    private LineGraphSeries<DataPoint> xseries;
    private LineGraphSeries<DataPoint> yseries;
    private LineGraphSeries<DataPoint> zseries;
    private  int lastX = 0,lastY=0,lastZ=0;
    private SensorManager SM;
    private Sensor mySensor;
    private Sensor gyroSenser;
    private SensorEventListener gyroscopeEventListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_imu);

        // we get graph view instance
        GraphView graphx = (GraphView) findViewById(R.id.get);
        GraphView graphy = (GraphView) findViewById(R.id.get2);
        GraphView graphz=(GraphView) findViewById(R.id.get3);
        SM = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mySensor = SM.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        boolean b = SM.registerListener(this, mySensor, SensorManager.SENSOR_DELAY_NORMAL);
        gyroSenser = SM.getDefaultSensor((Sensor.TYPE_GYROSCOPE));

        if (gyroSenser == null){
            Toast.makeText(this, "Gyroscope isn't available on this device", Toast.LENGTH_SHORT).show();
            //finish();
        }

        gyroscopeEventListener = new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent sensorEvent) {

                //positive clockwise
                //  gyroData.setText(" GYRO data : \n" +sensorEvent.values[0]+"\n "+sensorEvent.values[1]+"\n "+sensorEvent.values[2]+"\n");

            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int i) {

            }
        };
        // data
        xseries = new LineGraphSeries<DataPoint>();
        yseries = new LineGraphSeries<DataPoint>();
        zseries = new LineGraphSeries<DataPoint>();

        graphx.addSeries(xseries);
        graphy.addSeries(yseries);
        graphz.addSeries(zseries);
        // customize a little bit viewport
        Viewport viewport = graphx.getViewport();
        Viewport viewport1=graphy.getViewport();
        Viewport viewport2=graphz.getViewport();
        viewport1.setYAxisBoundsManual(true);
        viewport.setYAxisBoundsManual(true);
        viewport2.setYAxisBoundsManual(true);

        viewport.setMinY(0);
        viewport1.setMinY(0);
        viewport1.setMaxY(10);
        viewport2.setMinY(0);


        viewport2.setMaxY(10);
        viewport.setMaxY(10);
        viewport.setScrollable(true);
        viewport1.setScrollable(true);
        viewport2.setScrollable(true);
        xText = (TextView)findViewById(R.id.textView6);
        yText = (TextView)findViewById(R.id.textView7);
        zText = (TextView)findViewById(R.id.textView8);
    }

    @Override
    protected void onResume() {
        super.onResume();
        SM.registerListener(gyroscopeEventListener,gyroSenser, SM.SENSOR_DELAY_FASTEST);
        // we're going to simulate real time with thread that append data to the graph
        new Thread(new Runnable() {

            @Override
            public void run() {
                // we add 100 new entries
                while(true) {
                    runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            addEntry();
                        }
                    });

                    // sleep to slow down the add of entries
                    try {
                        Thread.sleep(5000);
                    } catch (InterruptedException e) {
                        // manage error ...
                    }
                }
            }
        }).start();
    }

    // add random data to graph
    private void addEntry() {

        // here, we choose to display max 10 points on the viewport and we scroll to endRANDOM.nextDouble() * 10d
        for(int i=0;i<yaxis.size();i++)
            yseries.appendData(new DataPoint(lastY++, yaxis.get(i)), true, 10);
        for(int i=0;i<xaxis.size();i++)
            xseries.appendData(new DataPoint(lastX++, xaxis.get(i)), true, 10);
        for(int i=0;i<zaxis.size();i++)
            zseries.appendData(new DataPoint(lastZ++, zaxis.get(i)), true, 10);
        // series.setAnimated(true);

    }
    @Override
    protected void onPause() {
        super.onPause();
        SM.unregisterListener(gyroscopeEventListener);
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {


        //sensor acceleration
        if(sensorEvent.values[0] != 0 ) {
            xText.setText(sensorEvent.values[0] + " m\\" + "s\u00B2");
            xaxis.add(new Double(Math.abs(sensorEvent.values[0])));
            if(xaxis.size()>=10000)xaxis.remove(0);
        }
        if(sensorEvent.values[1] != 0 ) {
            yText.setText(sensorEvent.values[1] + " m\\" + "s\u00B2");
            yaxis.add(new Double(Math.abs(sensorEvent.values[1])));
            if(yaxis.size()>=10000)yaxis.remove(0);
        }
        if(sensorEvent.values[2] != 0 ) {
            zText.setText(sensorEvent.values[2] + " m\\" + "s\u00B2");
            zaxis.add(new Double(Math.abs(sensorEvent.values[2])));
            if(zaxis.size()>=10000)zaxis.remove(0);
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {
        // not used
    }

}