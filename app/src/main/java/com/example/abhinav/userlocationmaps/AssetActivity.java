package com.example.abhinav.userlocationmaps;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
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
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

import static android.R.layout.simple_spinner_dropdown_item;
import static android.R.layout.simple_spinner_item;
import static android.app.PendingIntent.getActivity;

public class AssetActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    ImageView imageView;
    android.support.v7.widget.AppCompatEditText latitude;
    android.support.v7.widget.AppCompatEditText longitude;
    android.support.v7.widget.AppCompatEditText name;
    android.support.v7.widget.AppCompatEditText description;
    android.support.design.widget.FloatingActionButton CameraButton;
    Spinner category;
    Spinner sub_category;
    Button saveButton;
    LocationManager locationManager;
    LocationListener locationListener;
    SQLiteDatabase sqLiteDatabase;
    Uri filePath;
    SharedPreferences preferences;
    DatabaseReference myRef;
    StorageReference storage;
    private static final int pic_id = 123;
    String[] assetsCategories = { "Mammals", "Birds", "Reptiles","Echinoderms","Molluscs","Corals","Offence Record","Deceased animal","Diseased","Animal Droppings","Pawprints"};
    int number_categories = assetsCategories.length;
    int main_categories = 6;
    String selected_category = "Mammals";
    String other_text;

    public void getPhoto() {
        Intent in = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(in, 1);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == 1) {

            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
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
        category = findViewById(R.id.category_spinner);
        sub_category = findViewById(R.id.sub_category_spinner);
        saveButton=findViewById(R.id.saveButton);
        name = findViewById(R.id.nameEditText);
        description = findViewById(R.id.descriptionEditText);
        latitude.setText(String.format("%.3f", 28.610));
        longitude.setText(String.format("%.3f", 77.037));
        sqLiteDatabase = this.openOrCreateDatabase("OFFLINE_DATA", MODE_PRIVATE, null);



        // TODO populate the arraylist from database
        // Populate ArrayList for Spinner objects
        ArrayList<String> categories = new ArrayList<String>();
        Collections.addAll(categories,assetsCategories);

        final Map<String, ArrayList<String>> category_to_subcategory = new HashMap<String, ArrayList<String>>();
        final ArrayList<String>[] sub_categories  = new ArrayList[number_categories];


        sub_categories[0] = new ArrayList<>((Arrays.asList("Andaman Wild Pig (Blyth)","Nicobar Wild Pig","Other")));
        sub_categories[1]  = new ArrayList<>((Arrays.asList("Andaman Teal","Narcondam Hornbill","Other")));
        sub_categories[2] = new ArrayList<>((Arrays.asList("Saltwater Crocodile","Green Sea Turtle","Other")));
        sub_categories[3] = new ArrayList<>((Arrays.asList("Sea Cucumber","Curry Fish","Other")));
        sub_categories[4]  = new ArrayList<>((Arrays.asList("Pine Apple Shell","Yellow Helmet","Other")));
        sub_categories[5] = new ArrayList<>((Arrays.asList("Brush Coral","Knob Coral","Other")));

        for(int i=main_categories;i<number_categories;i++){
            sub_categories[i] = new ArrayList<>((Arrays.asList("Other")));
        }


        for(int i=0;i<number_categories;i++){
            category_to_subcategory.put(categories.get(i),sub_categories[i]);
        }

        // Populating complete . Now create a adapter for the categories spinner
        ArrayAdapter<String> categories_adapter = new ArrayAdapter<String>(AssetActivity.this,
                simple_spinner_item,categories);

        categories_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        category.setAdapter(categories_adapter);


        // In the event listener set instantiate the sub_category spinner
        category.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selected_category = (String) parent.getItemAtPosition(position);
//                Toast.makeText(getApplicationContext(), "Selected category: "+selected_category ,Toast.LENGTH_SHORT).show();
                Log.i("spinnertest","Got category selected as: " + selected_category + " at position: " + position);

                ArrayList<String> current_sub_category = category_to_subcategory.get(selected_category);
                ArrayAdapter<String> sub_categories_adapter = new ArrayAdapter<String>(AssetActivity.this,
                        simple_spinner_item,current_sub_category);

                sub_categories_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                sub_category.setAdapter(sub_categories_adapter);
                sub_category.setOnItemSelectedListener(AssetActivity.this);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


//        sub_categories_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//        sub_category.setAdapter(sub_categories_adapter);
//        sub_category.setOnItemSelectedListener(this);


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
                        ,name.getText().toString()
                        ,Double.parseDouble(latitude.getText().toString())
                        ,Double.parseDouble(longitude.getText().toString())
                        ,description.getText().toString()
                        ,category.getSelectedItem().toString()
                        ,"species"                       // TODO insert the species
                        ,Calendar.getInstance().getTime().toString()
                        ,DbBitmapUtility.getBytes(bitmap));


                Log.i("marker id",marker.getId());

                sqLiteDatabase.beginTransaction();
                try {
                    //TODO update the tables according to our categories
                    ContentValues values = new ContentValues();
                    ContentValues cv = new ContentValues();
                    cv.put("id",marker.getId());
                    cv.put("name",marker.getName());
                    cv.put("latitude",marker.getLatitude());
                    cv.put("longitude",marker.getLongitude());
                    cv.put("description",marker.getDescription());
                    cv.put("category",marker.getCategory());
                    cv.put("species",marker.getSpecies());
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
                Bitmap bitmap = (Bitmap) data.getExtras().get("data");
                int nh = (int) ( bitmap.getHeight() * (512.0 / bitmap.getWidth()) );
                Bitmap scaled = Bitmap.createScaledBitmap(bitmap, 512, nh, true);
                imageView.setImageBitmap(scaled);
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

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//        Toast.makeText(getApplicationContext(), "Selected asset: "+assetsCategories[position] ,Toast.LENGTH_SHORT).show();
        String selected_category = (String) parent.getItemAtPosition(position);
//        Toast.makeText(getApplicationContext(), "Selected category: "+selected_category ,Toast.LENGTH_SHORT).show();
        Log.i("spinnertest","Got sub-category selected as: " + selected_category);

        /*if(selected_category == "Other"){
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Enter the name");
            final EditText input = new EditText(this);
            // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
            input.setInputType(InputType.TYPE_CLASS_TEXT);
            builder.setView(input);
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    other_text = input.getText().toString();
                    Log.i("spinnertest","Got other text as " + other_text);
                }
            });
            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });
            builder.show();
        }*/
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
