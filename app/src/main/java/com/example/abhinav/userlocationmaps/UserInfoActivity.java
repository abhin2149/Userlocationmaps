package com.example.abhinav.userlocationmaps;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.abhinav.userlocationmaps.Models.User;

import java.util.Calendar;

public class UserInfoActivity extends AppCompatActivity {
    private TextInputEditText nameEditText;
    private TextInputEditText registrationEditText;
    private TextInputEditText beatEditText;
    private TextInputEditText phoneEditText;
    private Button saveButton;
    private SharedPreferences preferences;
    private SQLiteDatabase sqLiteDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_info);

        nameEditText = findViewById(R.id.nameEditText);
        registrationEditText = findViewById(R.id.registrationEditText);
        beatEditText = findViewById(R.id.beatEditText);
        phoneEditText = findViewById(R.id.phoneEditText);
        saveButton = findViewById(R.id.saveButton);
        preferences = this.getSharedPreferences("com.example.abhinav.userlocationmaps", Context.MODE_PRIVATE);
        sqLiteDatabase = this.openOrCreateDatabase("OFFLINE_DATA", MODE_PRIVATE, null);

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                User user = new User(preferences.getString("id","id"),28.610,77.037,
                        nameEditText.getText().toString(),beatEditText.getText().toString(),registrationEditText.getText().toString(),
                            phoneEditText.getText().toString(),Calendar.getInstance().getTime().toString());

                sqLiteDatabase.beginTransaction();
                try {
                    ContentValues cv = new ContentValues();
                    cv.put("id",user.getId());
                    cv.put("last_latitude",user.getLastLatitude());
                    cv.put("last_longitude",user.getLasLongitude());
                    cv.put("name",user.getName());
                    cv.put("beat",user.getBeat());
                    cv.put("reg_no",user.getRegistrationNumber());
                    cv.put("phone_no",user.getPhoneNumber());
                    cv.put("time",user.getTime());
                    sqLiteDatabase.insert("user",null,cv);
                    sqLiteDatabase.setTransactionSuccessful();
                } finally {
                    sqLiteDatabase.endTransaction();
                    Log.i("saved","complete");
                    Toast.makeText(UserInfoActivity.this,"Welcome",Toast.LENGTH_LONG).show();
                    finish();
                }
            }
        });

    }
}
