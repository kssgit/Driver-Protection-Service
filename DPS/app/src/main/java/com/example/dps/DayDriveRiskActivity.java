package com.example.dps;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.dps.login.SaveSharedPreference;

public class DayDriveRiskActivity extends AppCompatActivity {
    Button analysis_page;
    private Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_day_drive_risk);
        analysis_page=findViewById(R.id.analysis_page);
        if(SaveSharedPreference.getUserID(DayDriveRiskActivity.this).length() == 0) {
            // call Login Activity
            intent = new Intent(DayDriveRiskActivity.this, MainActivity.class);
            startActivity(intent);
            this.finish();
        } else {
            // Call Next Activity
            analysis_page.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    intent = new Intent(DayDriveRiskActivity.this, AnalysisActivity.class);
                    intent.putExtra("user_id", SaveSharedPreference.getUserID(DayDriveRiskActivity.this).toString());
                    startActivity(intent);
                    DayDriveRiskActivity.this.finish();
                }
            });
        }


    }
}