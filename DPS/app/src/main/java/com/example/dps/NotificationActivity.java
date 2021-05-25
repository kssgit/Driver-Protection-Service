package com.example.dps;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import com.example.dps.login.SaveSharedPreference;

public class NotificationActivity extends AppCompatActivity {
    private Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);
        if(SaveSharedPreference.getUserID(NotificationActivity.this).length() == 0) {
            // call Login Activity
            intent = new Intent(NotificationActivity.this, MainActivity.class);
            startActivity(intent);
            this.finish();
        } else {
            // Call Next Activity
            intent = new Intent(NotificationActivity.this, DayDriveRiskActivity.class);
            intent.putExtra("user_id", SaveSharedPreference.getUserID(this).toString());
            startActivity(intent);
            this.finish();
        }
    }
}