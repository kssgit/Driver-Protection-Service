package com.example.dps;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Console;
import java.io.IOException;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.http.Tag;



public class MainActivity extends AppCompatActivity {
    Button login_button;
    Button join_button;
    Retrofit retrofit;
    RetrofitAPI retrofitAPI;
    EditText userid,userpwd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        retrofit = new Retrofit.Builder()
                .baseUrl(retrofitAPI.REGIST_URL)
                .build();
        retrofitAPI = retrofit.create(RetrofitAPI.class);


        //  로그인 버튼 클릭
        login_button = findViewById(R.id.login_button);
        userid=findViewById(R.id.userID);
        userpwd=findViewById(R.id.userPwd);
        login_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){

//                String csrf = null;
//                try{
//                    csrf = new GetCsrf_Token().csrftoken;
//                }catch (IOException e){
//
//                }
                String user_id = userid.getText().toString();
                String user_pwd = userpwd.getText().toString();

                Call<ResponseBody> call = retrofitAPI.getUserdata("test1");
                call.enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        try{
                            Log.i("TEST1",response.body().toString());
                            System.out.println("성공!!!!");
                        }catch (Exception e){

                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        System.out.println("실패");

                        System.out.println(t.toString());
                    }
                });

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