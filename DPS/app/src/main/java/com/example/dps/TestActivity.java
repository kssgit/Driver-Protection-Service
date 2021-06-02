package com.example.dps;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import org.eazegraph.lib.charts.BarChart;
import org.json.JSONArray;
import org.json.JSONException;
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

public class TestActivity extends AppCompatActivity {

    private String user_id;
    //JsonData
    JSONArray co2;
    JSONArray eye;
    JSONArray emotion;
    JSONObject test;

    // chart
    View view;
    BarChart yesterday_eyechart;
    JSONArray co2_time;
    JSONArray co2_amount;

    int co2_len;
    String[] time;
    int[] amount;
    //retrofit
    Retrofit retrofit;
    RetrofitAPI retrofitAPI;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);

        //User_id 가져오기
        Intent intent = getIntent();
        user_id = intent.getExtras().getString("user_id");

        yesterday_eyechart = (BarChart) findViewById(R.id.yesterday_eyechart);

        //Json_data 가져오기
        //  1. user_id를 이용해서 장고에 데이터 요청
        //  2. 해당하는 데이터를 케이스에 따라 보내기
        retrofit = new Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create()) // converter 선언
                .baseUrl(retrofitAPI.REGIST_URL)
                .client(getUnsafeOkHttpClient().build())
                .build();
        retrofitAPI = retrofit.create(RetrofitAPI.class);
        Call<ResponseBody> call = retrofitAPI.getTestdata(user_id);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    ResponseBody body = response.body();
                    String jsonstr = body.string();
                    JSONObject jsonObj = new JSONObject(jsonstr);
                    co2 = (JSONArray) jsonObj.get("Co2");
                    eye = (JSONArray) jsonObj.get("Eye");
                    emotion = (JSONArray) jsonObj.get("Emotion");

                    test = (JSONObject) jsonObj.get("Test");

                    System.out.println(test);


                    co2_time = (JSONArray) test.get("time");
                    co2_amount = (JSONArray) test.get("amount");

                    co2_len = co2_time.length();
                    time = new String[co2_len];
                    amount = new int[co2_len];


                    for(int i=0; i<co2_len; i++) {
                        try {
                            time[i] = co2_time.getString(i);
                            amount[i] = co2_amount.getInt(i);
                            System.out.println("co2_amount: : " + amount[i]);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }



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
    // 막대 차트 설정
    private void setBarChart() {




       // yesterday_eyechart.clearChart();


        /*
        for(int i=0;i<time.length;i++){
            yesterday_eyechart.addBar(new BarModel(time[i].substring(11, 16), amount[i], 0xFF56B7F1));
        }
        */

        /*
        co2chart.addBar(new BarModel("12", 10f, 0xFF56B7F1));
        co2chart.addBar(new BarModel("13", 10f, 0xFF56B7F1));
        co2chart.addBar(new BarModel("14", 10f, 0xFF56B7F1));
        co2chart.addBar(new BarModel("15", 20f, 0xFF56B7F1));
        co2chart.addBar(new BarModel("16", 10f, 0xFF56B7F1));
        co2chart.addBar(new BarModel("17", 10f, 0xFF56B7F1));
        */
        //yesterday_eyechart.startAnimation();

    }
}



