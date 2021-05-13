package com.example.dps;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Toast;

public class JoinActivity extends AppCompatActivity {
    Button join_action_button, join_reset_button;
    EditText user_id, user_pwd, birth, phone_number, email, serial_no1, serial_no2;
    RadioGroup gender;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join);


        join_action_button = findViewById(R.id.join_action_button);
        join_reset_button = findViewById(R.id.join_reset_button);

        user_id = findViewById(R.id.user_id);
        user_pwd = findViewById(R.id.user_pwd);
        birth = findViewById(R.id.birth);
        gender = findViewById(R.id.gender);
        phone_number = findViewById(R.id.phone_number);
        email = findViewById(R.id.email);
        serial_no1 = findViewById(R.id.serial_no1);
        serial_no2 = findViewById(R.id.serial_no2);

        // 성별 라디오 버튼 남자로 초기화
        gender.check(R.id.man);

        // 회원가입 버튼 클릭
        join_action_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                // 모든 내용이 입력 되었는지 체크
                if(user_id.getText().toString().length() == 0){
                    Toast.makeText(JoinActivity.this, "아이디를 입력하세요", Toast.LENGTH_SHORT).show();
                    user_id.requestFocus();
                    return;
                }
                if(user_pwd.getText().toString().length() == 0){
                    Toast.makeText(JoinActivity.this, "비밀번호를 입력하세요", Toast.LENGTH_SHORT).show();
                    user_pwd.requestFocus();
                    return;
                }
                if(birth.getText().toString().length() == 0){
                    Toast.makeText(JoinActivity.this, "생년월일을 입력하세요", Toast.LENGTH_SHORT).show();
                    birth.requestFocus();
                    return;
                }
                if(phone_number.getText().toString().length() == 0){
                    Toast.makeText(JoinActivity.this, "휴대폰 번호를 입력하세요", Toast.LENGTH_SHORT).show();
                    phone_number.requestFocus();
                    return;
                }
                if(email.getText().toString().length() == 0){
                    Toast.makeText(JoinActivity.this, "이메일을 입력하세요", Toast.LENGTH_SHORT).show();
                    email.requestFocus();
                    return;
                }
                if(serial_no1.getText().toString().length() == 0){
                    Toast.makeText(JoinActivity.this, "시리얼 넘버1을 입력하세요", Toast.LENGTH_SHORT).show();
                    serial_no1.requestFocus();
                    return;
                }
                if(serial_no2.getText().toString().length() == 0){
                    Toast.makeText(JoinActivity.this, "시리얼 넘버2를 입력하세요", Toast.LENGTH_SHORT).show();
                    serial_no2.requestFocus();
                    return;
                }
                // DB에 해당 내용 저장

                // 인텐트 선언 : 현재 액티비티, 넘어갈 액티비티
                Intent intent = new Intent(JoinActivity.this, MainActivity.class);
                // 인텐트 실행
                startActivity(intent);
            }
        });

        // 초기화 버튼 클릭
        join_reset_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                user_id.setText("");
                user_pwd.setText("");
                birth.setText("");
                gender.check(R.id.man);
                phone_number.setText("");
                email.setText("");
                serial_no1.setText("");
                serial_no2.setText("");

                // 인텐트 선언 : 현재 액티비티, 넘어갈 액티비티
                Intent intent = new Intent(JoinActivity.this, MainActivity.class);
                // 인텐트 실행
                startActivity(intent);
            }
        });
    }
}


