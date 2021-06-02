package com.example.dps;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.content.Intent;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.example.dps.login.SaveSharedPreference;

public class DayDriveRiskActivity extends AppCompatActivity {
    Button analysis_page;
    private Intent intent;
    String user_id ;
    ImageButton img_btn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_day_drive_risk);
        analysis_page=findViewById(R.id.analysis_page);
        //메뉴 위젯 등록
        img_btn =(ImageButton)findViewById(R.id.menu_btn);
        registerForContextMenu(img_btn);
        // 자동 로그인 여부 확인
        if(SaveSharedPreference.getUserID(DayDriveRiskActivity.this).length() == 0) {
            // call Login Activity
            intent = new Intent(DayDriveRiskActivity.this, MainActivity.class);
            startActivity(intent);
            this.finish();
        } else {
            // 자동 로그인 활성화 되어 있다면
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

    //menu 선택 이벤트
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater mi = getMenuInflater();
        if(v == img_btn) {
            System.out.println("실행");
            mi.inflate(R.menu.menu, menu);
        }

    }
    // menu item 선택 시 실행
    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        switch(item.getItemId()){
            //로그아웃
            case R.id.logout_btn:
                SaveSharedPreference.clearUserName(this);
                ActivityCompat.finishAffinity(this);
                System.exit(0);
                return true;
            case R.id.user_update:

                return true;
            case R.id.all_data:
                Intent intent_1 = new Intent(this,AnalysisActivity.class);
                intent_1.putExtra("user_id", SaveSharedPreference.getUserID(this).toString());
                startActivity(intent_1);
                DayDriveRiskActivity.this.finish();

                return true;
            case R.id.one_day_data:
                Intent intent_2 = new Intent(this,DayDriveRiskActivity.class);
                intent_2.putExtra("user_id", SaveSharedPreference.getUserID(this).toString());
                startActivity(intent_2);
                DayDriveRiskActivity.this.finish();

                return true;
        }

        return false;
    }
}