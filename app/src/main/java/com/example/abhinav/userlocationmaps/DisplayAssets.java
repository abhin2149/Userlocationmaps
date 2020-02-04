package com.example.abhinav.userlocationmaps;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import com.example.abhinav.userlocationmaps.Models.Marker;

import java.util.ArrayList;

public class DisplayAssets extends AppCompatActivity {
    private SQLiteDatabase sqLiteDatabase;
    private ArrayList<Marker> mData;
    private RecyclerView mRecyclerView;
    private AssetAdapter mAssetAdapter;

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
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_assets);

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
        Log.i("records",cursor.getCount()+"  ");
        while(!cursor.isAfterLast()) {
            Log.i("id",cursor.getString(idIndex)+"");
            Log.i("latitude",cursor.getDouble(latitudeIndex)+"");
            Log.i("longitude",cursor.getDouble(longitudeIndex)+"");
            Log.i("description",cursor.getString(descriptionIndex)+"");
            Log.i("time",cursor.getString(timeIndex)+"");
            Log.i("image",cursor.getBlob(imageIndex)+"");
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
