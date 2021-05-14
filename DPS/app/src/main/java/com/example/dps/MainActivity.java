package com.example.dps;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    Button login_button;
    Button join_button;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //  로그인 버튼 클릭
        login_button = findViewById(R.id.login_button);
        login_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                //  1. 옳은 로그인인지 판단

                // 1-1. 옳은 로그인이 아니면

                //  1-2. 옳은 로그인이면

                // 인텐트 선언 : 현재 액티비티, 넘어갈 액티비티
                Intent intent = new Intent(MainActivity.this, AnalysisActivity.class);
                // 인텐트 실행
                startActivity(intent);
            }
        });

        //  회원가입 버튼 클릭
        join_button = findViewById(R.id.join_button);
        join_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                // 인텐트 선언 : 현재 액티비티, 넘어갈 액티비티
                Intent intent = new Intent(MainActivity.this, JoinActivity.class);
                // 인텐트 실행
                startActivity(intent);
            }
        });
    }
}