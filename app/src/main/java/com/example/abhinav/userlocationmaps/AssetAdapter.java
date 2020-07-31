package com.example.abhinav.userlocationmaps;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;


import com.example.abhinav.userlocationmaps.Models.Marker;
import com.example.abhinav.userlocationmaps.Utils.DbBitmapUtility;


import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

class AssetAdapter extends RecyclerView.Adapter<AssetAdapter.ViewHolder> {

    private ArrayList<Marker> mData;
    private Context mContext;


    public AssetAdapter(Context mContext, ArrayList<Marker> mData) {
        this.mData = mData;
        this.mContext = mContext;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.asset_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        /*final ProgressDialog progressDialog;
        progressDialog = new ProgressDialog(mContext);
        progressDialog.setMax(100);
        progressDialog.setMessage("Please wait...");
        progressDialog.setTitle("Loading UI");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.show();*/
        Marker marker = mData.get(position);
        holder.descriptionTextView.setText(marker.getDescription());
        holder.latTextView.setText("Lat. " + marker.getLatitude().toString());
        holder.longTextView.setText("Long. " +marker.getLongitude().toString());

        holder.assetImageView.setImageBitmap(DbBitmapUtility.getImage(marker.getImage()));

    }
    @Override
    public int getItemCount() {
        return mData.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView descriptionTextView;
        TextView latTextView;
        TextView longTextView;
        ImageView assetImageView;
        Button ReadMore;
        private Context temp  ;
        public ViewHolder(View itemView) {
            super(itemView);

            descriptionTextView = itemView.findViewById(R.id.description_textView);
            //ReadMore = itemView.findViewById(R.id.readMore);
            latTextView =  itemView.findViewById(R.id.latitude_textView);
            longTextView =  itemView.findViewById(R.id.longitude_textView);
            assetImageView =  itemView.findViewById(R.id.asset_imageView);
            temp = itemView.getContext();
            assetImageView.buildDrawingCache();

            assetImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v)
                {
                    Intent myIntent = new Intent(temp, AssetInfo.class);
                    myIntent.putExtra("descriptionTextView", descriptionTextView.getText().toString());
                    myIntent.putExtra("latTextView", latTextView.getText().toString());
                    myIntent.putExtra("longTextView", longTextView.getText().toString());
                    Bitmap bitmap = assetImageView.getDrawingCache();
                    myIntent.putExtra("BitmapImage", bitmap);
                    temp.startActivity(myIntent);
                }
            });

        }


    }


}
