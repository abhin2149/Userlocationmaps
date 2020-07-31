package com.example.abhinav.userlocationmaps;

import android.graphics.Bitmap;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.abhinav.userlocationmaps.Models.Asset;

public class AssetInfo extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_asset_info);
        // intent vals
        Bundle extras = getIntent().getExtras();
        String descriptionString=" ",latString=" ",longString=" ";
        Bitmap bitmap=null;
        if (extras != null)
        {
             descriptionString = extras.getString("descriptionTextView");
             latString = extras.getString("latTextView");
             longString = extras.getString("longTextView");
             bitmap =  extras.getParcelable("BitmapImage");
        }

        //get references
        TextView DescriptionTextView =  findViewById(R.id.description_textView);
        TextView LatTextView =  findViewById(R.id.latTextView);
        TextView LongTextView =  findViewById(R.id.longitude_textView);
        ImageView AssetView =  findViewById(R.id.imageId);
        Log.i("description ",  descriptionString);
        Log.i("latTextView ",  latString);
        Log.i("longTextView ",  longString);
        //Log.i("bitmap ",  bitmap);

        //set values
        if(descriptionString!=null)
            DescriptionTextView.setText(descriptionString);
        if(latString!=null)
            LatTextView.setText(latString);
        if(longString!=null)
            LongTextView.setText(longString);
        if(bitmap!=null)
        {

            bitmap = Bitmap.createScaledBitmap(bitmap, AssetView.getWidth(),AssetView.getHeight(),true);
            AssetView.setImageBitmap(bitmap);
        }

    }
}
