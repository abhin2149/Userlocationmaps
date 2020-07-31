package com.example.abhinav.userlocationmaps;

import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;
import android.support.v7.widget.Toolbar;

import com.example.abhinav.userlocationmaps.Models.Asset;
import com.example.abhinav.userlocationmaps.Models.Marker;
import com.example.abhinav.userlocationmaps.Utils.DbBitmapUtility;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

public class DisplayAssets extends AppCompatActivity {
    private SQLiteDatabase sqLiteDatabase;
    private ArrayList<Marker> mData;
    private RecyclerView mRecyclerView;
    private AssetAdapter mAssetAdapter;
    private DatabaseReference myRef;
    private StorageReference storage;
    LinearLayout updateButton;
    LinearLayout syncButton;
    FloatingActionButton fabButton;

    @Override
    protected void onStart() {
        super.onStart();

    }
    public void add_asset(View v){
        Intent myIntent = new Intent(DisplayAssets.this, AssetActivity.class);
        startActivity(myIntent);

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }
    public void updateData(final Context context){
        final ProgressDialog progressDoalog;
        progressDoalog = new ProgressDialog(DisplayAssets.this);
        progressDoalog.setMax(100);
        progressDoalog.setMessage("Please wait...");
        progressDoalog.setTitle("Updating data with the server");
        progressDoalog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDoalog.setCancelable(false);
        progressDoalog.show();
        // delete table assets
        sqLiteDatabase.beginTransaction();
        try{
            sqLiteDatabase.delete("assets",null,null);
            sqLiteDatabase.setTransactionSuccessful();
        }
        finally {
            sqLiteDatabase.endTransaction();
            Log.i("delete", "complete");
        }
        // retrive new data from global store
        Log.i("update","called");
        final ArrayList<Asset> mData = new ArrayList<>();
        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot data : dataSnapshot.getChildren())    {
                    for(DataSnapshot snapshot : data.getChildren()){
                        Log.i("snapshot",snapshot.getValue(Asset.class).toString());
                        mData.add(snapshot.getValue(Asset.class));
                    }
                }
                Log.i("mdata",mData.size()+"");
                // download images
                final ArrayList<Marker> markers = new ArrayList<>();
                File rootPath = new File(Environment.getExternalStorageDirectory(), "images");
                if(!rootPath.exists()) {
                    rootPath.mkdirs();
                }
                for(int i=0;i<mData.size();i++){
                    final Asset asset = mData.get(i);
                    StorageReference imageStorage = storage.child(asset.getImage());
                    final File localFile = new File(rootPath,asset.getImage()+".jpg");
                    final int pos = i;
                    imageStorage.getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                            Log.i("downloaded",taskSnapshot.toString());
                            Bitmap bitmap = null;
                            try {
                                bitmap = MediaStore.Images.Media.getBitmap(context.getContentResolver(), Uri.fromFile(localFile));
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            int nh = (int) ( bitmap.getHeight() * (512.0 / bitmap.getWidth()) );
                            Bitmap scaled = Bitmap.createScaledBitmap(bitmap, 512, nh, true);
                            Marker marker1 = new Marker(asset.getId(),asset.getLatitude(),asset.getLongitude(),asset.getDescription(),asset.getCategory(),asset.getTime()
                                    , DbBitmapUtility.getBytes(scaled));
                            markers.add(marker1);
                            if(pos==mData.size()-1){

                                //save data to local
                                for(Marker marker: markers){
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
                                        sqLiteDatabase.insert("assets",null,cv);
                                        sqLiteDatabase.setTransactionSuccessful();

                                    } finally {
                                        sqLiteDatabase.endTransaction();
                                        Log.i("saved", "complete");
                                    }
                                }
                                progressDoalog.dismiss();
                                Toast.makeText(DisplayAssets.this,"Your Assets have been updated",Toast.LENGTH_LONG).show();
                                Intent myIntent = new Intent(DisplayAssets.this, DisplayAssets.class);
                                startActivity(myIntent);
                                finish();
                            }
                        }
                    });
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
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
        progressDoalog.show();
        Log.i("sync","called");
        Cursor cursor = sqLiteDatabase.rawQuery("SELECT * FROM local_assets", null);

        int idIndex = cursor.getColumnIndex("id");
        int latitudeIndex = cursor.getColumnIndex("latitude");
        int longitudeIndex = cursor.getColumnIndex("longitude");
        int descriptionIndex = cursor.getColumnIndex("description");
        int categoryIndex = cursor.getColumnIndex("category");
        int timeIndex = cursor.getColumnIndex("time");
        int imageIndex = cursor.getColumnIndex("image");

        cursor.moveToFirst();
        Log.i("records",cursor.getCount()+"  ");
        if(cursor.getCount()==0){
            progressDoalog.dismiss();
            Toast.makeText(DisplayAssets.this,"No data found to sync!",Toast.LENGTH_LONG).show();
            return;
        }
        ArrayList<Asset> assets = new ArrayList<>();
        while(!cursor.isAfterLast()) {
            Log.i("id", cursor.getString(idIndex) + "");
            Log.i("latitude", cursor.getDouble(latitudeIndex) + "");
            Log.i("longitude", cursor.getDouble(longitudeIndex) + "");
            Log.i("description", cursor.getString(descriptionIndex) + "");
            Log.i("category", cursor.getString(categoryIndex) + "");
            Log.i("time", cursor.getString(timeIndex) + "");
            Log.i("image", cursor.getBlob(imageIndex) + "");
            Asset asset = new Asset();
            asset.setId(cursor.getString(idIndex));
            asset.setDescription(cursor.getString(descriptionIndex));
            asset.setCategory(cursor.getString(categoryIndex));
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
                    sqLiteDatabase.delete("local_assets",null,null);
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
        updateButton = findViewById(R.id.update_button);
        syncButton = findViewById(R.id.sync_button);
        fabButton = findViewById(R.id.add_asset_button);

        final Context temp1 = this;
        updateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                // Create the camera_intent ACTION_IMAGE_CAPTURE
                // it will open the camera for capture the image
                updateData(temp1);
                // Start the activity with camera_intent,
                // and request pic id
            }
        });
        syncButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                // Create the camera_intent ACTION_IMAGE_CAPTURE
                // it will open the camera for capture the image
                syncData();
                // Start the activity with camera_intent,
                // and request pic id
            }
        });
        fabButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                // Create the camera_intent ACTION_IMAGE_CAPTURE
                // it will open the camera for capture the image
                add_asset(v);
                // Start the activity with camera_intent,
                // and request pic id
            }
        });


        mAssetAdapter = new AssetAdapter(this,mData);
        mRecyclerView.setAdapter(mAssetAdapter);
        mRecyclerView.setLayoutManager(new GridLayoutManager(this, 2, LinearLayoutManager.VERTICAL,false ));

        Cursor cursor = sqLiteDatabase.rawQuery("SELECT * FROM assets", null);

        int idIndex = cursor.getColumnIndex("id");
        int latitudeIndex = cursor.getColumnIndex("latitude");
        int longitudeIndex = cursor.getColumnIndex("longitude");
        int descriptionIndex = cursor.getColumnIndex("description");
        int categoryIndex = cursor.getColumnIndex("category");
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
            String temp = cursor.getString(descriptionIndex);

            marker.setDescription(temp);
            marker.setCategory(cursor.getString(categoryIndex));
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
