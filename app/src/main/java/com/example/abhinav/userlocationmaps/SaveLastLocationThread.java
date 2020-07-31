package com.example.abhinav.userlocationmaps;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.example.abhinav.userlocationmaps.Models.User;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.IOException;

class SaveLastLocationThread extends Thread
{
    private SQLiteDatabase sqLiteDatabase;
    private Cursor cursor;
    private Double lastLatitude;
    private Double lastLongitude;
    private String timestamp;
    private String id;

    SaveLastLocationThread(SQLiteDatabase sqLiteDatabase,String id){
        this.sqLiteDatabase = sqLiteDatabase;
        this.id = id;
        lastLatitude = 0.00;
        lastLongitude = 0.00;
    }

    private boolean isOnline() {
        Runtime runtime = Runtime.getRuntime();
        try {
            Process ipProcess = runtime.exec("/system/bin/ping -c 1 8.8.8.8");
            int     exitValue = ipProcess.waitFor();
            Log.i("checkinternet","internet connection checked");
            return (exitValue == 0);
        }
        catch (IOException | InterruptedException e)          { e.printStackTrace(); }

        return false;
    }

    public void run(){
        try {
            while(true){
//                Log.i("lastlocation","inside the last location thread");
                cursor = sqLiteDatabase.rawQuery("SELECT * FROM user where id = ?", new String[]{this.id});
                int idIndex = cursor.getColumnIndex("id");
                int nameIndex = cursor.getColumnIndex("name");
                int beatIndex = cursor.getColumnIndex("beat");
                int regisNoIndex = cursor.getColumnIndex("reg_no");
                int phoneNoIndex = cursor.getColumnIndex("phone_no");
                int latitudeIndex = cursor.getColumnIndex("last_latitude");
                int longitudeIndex = cursor.getColumnIndex("last_longitude");
                int timeIndex = cursor.getColumnIndex("time");


                boolean isValid = cursor.moveToFirst();
                if(!isValid){
                    Log.i("lastlocation","For user:" + id + "location not present in the database" + cursor.getCount());
                    Thread.sleep(5000);
                    continue;
                }

                User user = new User(cursor.getString(idIndex),cursor.getDouble(latitudeIndex),
                        cursor.getDouble(longitudeIndex),cursor.getString(nameIndex),cursor.getString(beatIndex),
                        cursor.getString(regisNoIndex),cursor.getString(phoneNoIndex),cursor.getString(timeIndex));

                timestamp = user.getTime();

                DatabaseReference myRef = FirebaseDatabase.getInstance().getReference().child("Users").child(id);

                // check for internet connection and update if required
                if(this.isOnline() && (Math.abs(user.getLastLatitude()-lastLatitude) > 1.00 || Math.abs(user.getLastLongitude()-lastLongitude) > 1.00)) {
                    // TODO write code to save to firebase database
                    lastLatitude = user.getLastLatitude();
                    lastLongitude = user.getLastLongitude();

                    myRef.setValue(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Log.i("lastlocation","For user:" + id  + " last location stored" + " " + lastLatitude + " " + lastLongitude + " " + timestamp );
                        }
                    });
                    Thread.sleep(20000);
                }else {
                    Log.i("lastlocation", "For user:" + id + " last location failed due to no internet or no new location");
                    Thread.sleep(5000);
                }
            }
        }catch (Exception e) {
            e.printStackTrace();
        }
    }
}
