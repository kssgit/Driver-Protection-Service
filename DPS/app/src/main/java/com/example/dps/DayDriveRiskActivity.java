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

import org.json.JSONArray;
import org.json.JSONObject;

import java.security.cert.CertificateException;

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

public class DayDriveRiskActivity extends AppCompatActivity {
    Button analysis_page;
    private Intent intent;


    private String user_id;

    JSONObject emotion; // 어제의 감정
    JSONObject sleep; // 어제의 졸음운전

    // 감정
    JSONArray emotion_time;
    JSONArray emotion_emotion;
    int emotion_len;

    // 졸음
    JSONArray sleep_eye;
    int[] eye;
    //retrofit
    Retrofit retrofit;
    RetrofitAPI retrofitAPI;


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

            user_id = SaveSharedPreference.getUserID(DayDriveRiskActivity.this).toString();
            retrofit = new Retrofit.Builder()
                    .addConverterFactory(GsonConverterFactory.create()) // converter 선언
                    .baseUrl(retrofitAPI.REGIST_URL)
                    .client(getUnsafeOkHttpClient().build())
                    .build();
            retrofitAPI = retrofit.create(RetrofitAPI.class);
            Call<ResponseBody> call = retrofitAPI.getYesterdaydata(user_id);
            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    try {
                        ResponseBody body = response.body();
                        String jsonstr = body.string();
                        JSONObject jsonObj = new JSONObject(jsonstr);
                        emotion = (JSONObject) jsonObj.get("Emotion");
                        sleep = (JSONObject) jsonObj.get("Sleep");

                        System.out.println("sleep: " + sleep);
                        sleep_eye = (JSONArray) sleep.get("0");

                        eye = new int[3];
                        eye[0] = sleep_eye.getInt(0);
                        eye[1] = sleep_eye.getInt(1);
                        eye[2] = sleep_eye.getInt(2);

                        //System.out.println(eye);


                    } catch (Exception e) {
                        e.printStackTrace();
                        System.out.println("JsonObj 오류"); //확인
                    }
                }
                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    System.out.println("통신 실패");
                }
            });


        }


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
  