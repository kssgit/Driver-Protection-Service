package com.example.dps;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;

import com.example.dps.login.SaveSharedPreference;

import org.eazegraph.lib.charts.PieChart;
import org.eazegraph.lib.models.PieModel;
import org.json.JSONArray;
import org.json.JSONObject;

import java.security.cert.CertificateException;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class DayDriveRiskActivity extends AppCompatActivity {
    private Intent intent;


    private String user_id;

    JSONObject emotion; // 어제의 감정
    JSONObject sleep; // 어제의 졸음운전

    // 감정
    JSONArray arr_Emotion_kind;
    JSONArray arr_Emotion_count;
    int emotion_len;
    String[] emotion_kind;
    int[] emotion_count;

    // 졸음
    JSONArray arr_Sleep_kind;
    JSONArray arr_Sleep_count;
    int sleep_len;
    String[] sleep_kind;
    int[] sleep_count;

    //retrofit
    Retrofit retrofit;
    RetrofitAPI retrofitAPI;

    String[] color;
    PieChart yesterday_sleepchart;
    PieChart yesterday_emotionchart;

    ImageButton img_btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_day_drive_risk);

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

                        color = new String[5];
                        color[0] = "#FE6DA8";
                        color[1] = "#56B7F1";
                        color[2] = "#CDA67F";
                        color[3] = "#E71D36";
                        color[4] = "#98DFFF";

                        // 감정 그래프 그리기
                        System.out.println("emotion: " + emotion);

                        arr_Emotion_kind = (JSONArray) emotion.get("emotion");
                        arr_Emotion_count = (JSONArray) emotion.get("count");

                        emotion_len = arr_Emotion_kind.length();
                        System.out.println("emotion_len: " + emotion_len);

                        emotion_kind = new String[emotion_len];
                        emotion_count = new int[emotion_len];
                        for(int i=0; i<emotion_len; i++)
                        {
                            if(arr_Emotion_kind.getInt(i)==0) {
                                emotion_kind[i] = "화남";
                                emotion_count[i] = arr_Emotion_count.getInt(i);
                            }
                            else if(arr_Emotion_kind.getInt(i)==1){
                                emotion_kind[i] = "두려움";
                                emotion_count[i] = arr_Emotion_count.getInt(i);
                            }
                            else if(arr_Emotion_kind.getInt(i)==2){
                                emotion_kind[i] = "행복";
                                emotion_count[i] = arr_Emotion_count.getInt(i);
                            }
                            else if(arr_Emotion_kind.getInt(i)==3){
                                emotion_kind[i] = "슬픔";
                                emotion_count[i] = arr_Emotion_count.getInt(i);
                            }
                            else if(arr_Emotion_kind.getInt(i)==4){
                                emotion_kind[i] = "중립";
                                emotion_count[i] = arr_Emotion_count.getInt(i);
                            }

                        }

                        for(int i=0; i<emotion_len; i++)
                        {
                            System.out.println("emotion_kind: " + emotion_kind);
                            System.out.println("emotion_count: " + emotion_count);
                        }

                        yesterday_emotionchart = findViewById(R.id.yesterday_emotionchart);
                        setEmotionPieChart();

                        // 졸음 그래프 그리기
                        System.out.println("sleep: " + sleep);
                        arr_Sleep_kind = (JSONArray) sleep.get("is_sleep");
                        arr_Sleep_count = (JSONArray) sleep.get("count");

                        sleep_len = arr_Sleep_kind.length();
                        System.out.println("sleep_len: " + sleep_len);

                        sleep_kind = new String[sleep_len];
                        sleep_count = new int[sleep_len];
                        for(int i=0; i<sleep_len; i++)
                        {
                            if(arr_Sleep_kind.getInt(i)==0) {
                                sleep_kind[i] = "최고의 운전 상태";
                                sleep_count[i] = arr_Sleep_count.getInt(i);
                            }
                            else if(arr_Sleep_kind.getInt(i)==1){
                                sleep_kind[i] = "졸음 운전";
                                sleep_count[i] = arr_Sleep_count.getInt(i);
                            }
                            else if(arr_Sleep_kind.getInt(i)==2){
                                sleep_kind[i] = "졸음 운전 경고";
                                sleep_count[i] = arr_Sleep_count.getInt(i);
                            }

                        }

                        for(int i=0; i<sleep_len; i++)
                        {
                            System.out.println("sleep_kind: " + sleep_kind);
                            System.out.println("sleep_count: " + sleep_count);
                        }

                        yesterday_sleepchart = findViewById(R.id.yesterday_sleepchart);
                        setSleepPieChart();



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
            final TrustManager[] trustAllCerts = new TrustManager[]{
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
            builder.sslSocketFactory(sslSocketFactory, (X509TrustManager) trustAllCerts[0]);
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

    private void setEmotionPieChart(){
        yesterday_emotionchart.clearChart();

        for(int i=0; i<emotion_len; i++)
        {
            yesterday_emotionchart.addPieSlice(new PieModel(emotion_kind[i], emotion_count[i], Color.parseColor(color[i])));
        }
    }

    private void setSleepPieChart(){
        yesterday_sleepchart.clearChart();
        for(int i=0; i<sleep_len; i++)
        {
            yesterday_sleepchart.addPieSlice(new PieModel(sleep_kind[i], sleep_count[i], Color.parseColor(color[i])));
        }
    }

}
  