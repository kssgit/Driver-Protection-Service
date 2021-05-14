package com.example.dps;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.example.dps.vo.JoinVo;

import org.json.JSONObject;

import java.security.cert.CertificateException;
import java.util.UUID;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import androidx.appcompat.app.AppCompatActivity;
import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class JoinActivity extends AppCompatActivity {
    Button join_action_button, join_reset_button;
    EditText user_id, user_pwd, name , birth, phone_number, email, serial_no1, serial_no2;
    RadioGroup gender;
    Retrofit retrofit;
    RetrofitAPI retrofitAPI;
    RadioButton rd;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join);
        retrofit = new Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create()) // converter 선언
                .baseUrl(retrofitAPI.REGIST_URL)
                .client(getUnsafeOkHttpClient().build())
                .build();
        retrofitAPI = retrofit.create(RetrofitAPI.class);

        join_action_button = findViewById(R.id.join_action_button);
        join_reset_button = findViewById(R.id.join_reset_button);

        user_id = findViewById(R.id.user_id);
        user_pwd = findViewById(R.id.user_pwd);
        name = findViewById(R.id.name);
        birth = findViewById(R.id.birth);
        gender = findViewById(R.id.gender);
        phone_number = findViewById(R.id.phone_number);
        email = findViewById(R.id.email);
        serial_no1 = findViewById(R.id.serial_no1);
        serial_no2 = findViewById(R.id.serial_no2);

        // 성별 라디오 버튼 남자로 초기화
        gender.check(R.id.man);
        rd = findViewById(R.id.man);
        gender.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                rd = findViewById(checkedId);
            }
        });
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
                if(name.getText().toString().length() == 0){
                    Toast.makeText(JoinActivity.this, "이름을 입력하세요", Toast.LENGTH_SHORT).show();
                    name.requestFocus();
                    return;
                }
                if(user_id.getText().toString().length() == 0){
                    Toast.makeText(JoinActivity.this, "아이디를 입력하세요", Toast.LENGTH_SHORT).show();
                    user_id.requestFocus();
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
                JoinVo vo = new JoinVo(
                        user_id.getText().toString(),
                        user_pwd.getText().toString(),
                        birth.getText().toString(),
                        name.getText().toString(),
                        phone_number.getText().toString(),
                        serial_no1.getText().toString(),
                        serial_no2.getText().toString(),
                        rd.getText().toString(),
                        email.getText().toString()
                );
                String csrfToken = UUID.randomUUID().toString();
                Call<ResponseBody> call = retrofitAPI.getJoin(csrfToken,vo);
                call.enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        ResponseBody bodys = response.body();
//                            string 형채의 json 데이터
                        try {
                            String jsonstr = bodys.string();
                            JSONObject jsonObj = new JSONObject(jsonstr);
                            boolean success = (Boolean) jsonObj.get("success");
                            if (success==true){
                                System.out.println("유저생성 완료");
                                // 인텐트 선언 : 현재 액티비티, 넘어갈 액티비티
                                Intent intent = new Intent(JoinActivity.this, MainActivity.class);
                                // 인텐트 실행
                                startActivity(intent);
                            }else{
                                System.out.println("유저 생성 못함");
                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {

                    }
                });


            }
        });

        // 초기화 버튼 클릭
        join_reset_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                user_id.setText("");
                user_pwd.setText("");
                name.setText("");
                birth.setText("");
                gender.check(R.id.man);
                phone_number.setText("");
                email.setText("");
                serial_no1.setText("");
                serial_no2.setText("");

            }
        });
    }
    //SSL 인증 없이 HTTPS 통과
    public static OkHttpClient.Builder getUnsafeOkHttpClient() {
        try {
            final TrustManager[] trustAllCerts = new TrustManager[] {
                    new X509TrustManager() {
                        @Override
                        public void checkClientTrusted(java.security.cert.X509Certificate[] chain, String authType) throws CertificateException {
                        }

                        @Override
                        public void checkServerTrusted(java.security.cert.X509Certificate[] chain, String authType) throws CertificateException {
                        }

                        @Override
                        public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                            return new java.security.cert.X509Certificate[]{};
                        }
                    }
            };

            final SSLContext sslContext = SSLContext.getInstance("SSL");
            sslContext.init(null, trustAllCerts, new java.security.SecureRandom());

            final SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();

            OkHttpClient.Builder builder = new OkHttpClient.Builder();
            builder.sslSocketFactory(sslSocketFactory, (X509TrustManager)trustAllCerts[0]);
            builder.hostnameVerifier(new HostnameVerifier() {
                @Override
                public boolean verify(String hostname, SSLSession session) {
                    return true;
                }
            });
            return builder;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }
}


