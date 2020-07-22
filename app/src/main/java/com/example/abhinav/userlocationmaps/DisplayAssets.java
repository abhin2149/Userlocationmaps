package com.example.abhinav.userlocationmaps;

import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.example.abhinav.userlocationmaps.Models.Asset;
import com.example.abhinav.userlocationmaps.Models.Marker;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.Random;

public class DisplayAssets extends AppCompatActivity {
    private SQLiteDatabase sqLiteDatabase;
    private ArrayList<Marker> mData;
    private RecyclerView mRecyclerView;
    private AssetAdapter mAssetAdapter;
    private DatabaseReference myRef;
    private StorageReference storage;

    @Override
    protected void onStart() {
        super.onStart();

    }
    public void add_asset(View v){
        Intent myIntent = new Intent(DisplayAssets.this, AssetActivity.class);
        startActivity(myIntent);
        finish();
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.asset_menu, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.update:
                // do something
                return true;

            case R.id.sync:
                //do something
                syncData();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void updateData(){
        // retrive new data from global store
    }

    public void syncData(){
        // send data to global store

        final ProgressDialog progressDoalog;
        progressDoalog = new ProgressDialog(DisplayAssets.this);
        progressDoalog.setMax(100);
        progressDoalog.setMessage("Please wait...");
        progressDoalog.setTitle("Syncing data with the server");
        progressDoalog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDoalog.setCancelable(false);
        Log.i("sync","called");
        Cursor cursor = sqLiteDatabase.rawQuery("SELECT * FROM local_markers", null);

        int idIndex = cursor.getColumnIndex("id");
        int latitudeIndex = cursor.getColumnIndex("latitude");
        int longitudeIndex = cursor.getColumnIndex("longitude");
        int descriptionIndex = cursor.getColumnIndex("description");
        int timeIndex = cursor.getColumnIndex("time");
        int imageIndex = cursor.getColumnIndex("image");

        cursor.moveToFirst();
        Log.i("records",cursor.getCount()+"  ");
        if(cursor.getCount()==0){
            Toast.makeText(DisplayAssets.this,"No data found to sync!",Toast.LENGTH_LONG).show();
            return;
        }
        ArrayList<Asset> assets = new ArrayList<>();
        while(!cursor.isAfterLast()) {
            Log.i("id", cursor.getString(idIndex) + "");
            Log.i("latitude", cursor.getDouble(latitudeIndex) + "");
            Log.i("longitude", cursor.getDouble(longitudeIndex) + "");
            Log.i("description", cursor.getString(descriptionIndex) + "");
            Log.i("time", cursor.getString(timeIndex) + "");
            Log.i("image", cursor.getBlob(imageIndex) + "");
            Asset asset = new Asset();
            asset.setId(cursor.getString(idIndex));
            asset.setDescription(cursor.getString(descriptionIndex));
            asset.setLatitude(cursor.getDouble(latitudeIndex));
            asset.setLongitude(cursor.getDouble(longitudeIndex));
            asset.setTime(cursor.getString(timeIndex));

            final int random = new Random().nextInt(9999);
            storage.child("" + random).putBytes(cursor.getBlob(imageIndex)).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Log.i("image " + random, "uploaded");
                }
            });
            asset.setImage(String.valueOf(random));
            assets.add(asset);
            cursor.moveToNext();
        }
        cursor.close();
        myRef.push().setValue(assets).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                sqLiteDatabase.beginTransaction();
                try{
                    sqLiteDatabase.delete("local_markers",null,null);
                    sqLiteDatabase.setTransactionSuccessful();
                }
                finally {
                    sqLiteDatabase.endTransaction();
                    Log.i("delete", "complete");
                }
                progressDoalog.dismiss();
                Toast.makeText(DisplayAssets.this,"Your Assets have been synced",Toast.LENGTH_LONG).show();
                Intent myIntent = new Intent(DisplayAssets.this, DisplayAssets.class);
                startActivity(myIntent);
                finish();
            }
        });
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_assets);

        myRef = FirebaseDatabase.getInstance().getReference().child("Assets");
        storage = FirebaseStorage.getInstance().getReference().child("Images");
        mData = new ArrayList<>();
        sqLiteDatabase = this.openOrCreateDatabase("OFFLINE_DATA", MODE_PRIVATE, null);
        mRecyclerView = findViewById(R.id.assets_recycler_view);

        mAssetAdapter = new AssetAdapter(this,mData);
        mRecyclerView.setAdapter(mAssetAdapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));


        Cursor cursor = sqLiteDatabase.rawQuery("SELECT * FROM markers", null);

        int idIndex = cursor.getColumnIndex("id");
        int latitudeIndex = cursor.getColumnIndex("latitude");
        int longitudeIndex = cursor.getColumnIndex("longitude");
        int descriptionIndex = cursor.getColumnIndex("description");
        int timeIndex = cursor.getColumnIndex("time");
        int imageIndex = cursor.getColumnIndex("image");

        cursor.moveToFirst();
        //Log.i("records",cursor.getCount()+"  ");
        while(!cursor.isAfterLast()) {
            /*Log.i("id",cursor.getString(idIndex)+"");
            Log.i("latitude",cursor.getDouble(latitudeIndex)+"");
            Log.i("longitude",cursor.getDouble(longitudeIndex)+"");
            Log.i("description",cursor.getString(descriptionIndex)+"");
            Log.i("time",cursor.getString(timeIndex)+"");
            Log.i("image",cursor.getBlob(imageIndex)+"");*/
            Marker marker = new Marker();
            marker.setId(cursor.getString(idIndex));
            marker.setDescription(cursor.getString(descriptionIndex));
            marker.setImage(cursor.getBlob(imageIndex));
            marker.setLatitude(cursor.getDouble(latitudeIndex));
            marker.setLongitude(cursor.getDouble(longitudeIndex));
            marker.setTime(cursor.getString(timeIndex));
            mData.add(marker);
            cursor.moveToNext();
        }
        cursor.close();
        mAssetAdapter.notifyDataSetChanged();
    }
}
