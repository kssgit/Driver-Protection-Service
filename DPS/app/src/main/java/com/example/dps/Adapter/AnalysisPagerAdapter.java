package com.example.dps.Adapter;

import android.os.Bundle;

import com.example.dps.Fragment.Co2Fragment;
import com.example.dps.Fragment.EmotionFragment;
import com.example.dps.Fragment.EyeFragment;
import com.example.dps.Fragment.TotalFragment;
import com.example.dps.RetrofitAPI;

import org.json.JSONArray;
import org.json.JSONObject;

import java.security.cert.CertificateException;
import java.util.UUID;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;



public class AnalysisPagerAdapter extends FragmentStatePagerAdapter {
    Retrofit retrofit;
    RetrofitAPI retrofitAPI;

    private int mPageCount;
    private String user_id;
    public AnalysisPagerAdapter(FragmentManager fm, int pageCount, String user_id) {
        super(fm);
        this.mPageCount = pageCount;
        this.user_id= user_id;
    }

    @Override

    public int getCount() {

        return mPageCount;

    }

    @Override

    public Fragment getItem(int position) {

        //  1. user_id를 이용해서 장고에 데이터 요청
        //  2. 해당하는 데이터를 케이스에 따라 보내기
        retrofit = new Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create()) // converter 선언
                .baseUrl(retrofitAPI.REGIST_URL)
                .client(getUnsafeOkHttpClient().build())
                .build();
        retrofitAPI = retrofit.create(RetrofitAPI.class);
        String csrfToken = UUID.randomUUID().toString();
        Call<ResponseBody> call = retrofitAPI.getUserdata(user_id);


        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                JSONArray co2;
                JSONArray eye;
                JSONArray emotion;
                try {
                    ResponseBody body = response.body();
                    String jsonstr = body.string();
                    System.out.println(jsonstr);
                    JSONObject jsonObj = new JSONObject(jsonstr);

                    co2 = (JSONArray) jsonObj.get("Co2");
                    eye = (JSONArray) jsonObj.get("Eye");
                    emotion = (JSONArray) jsonObj.get("Emotion");
                    System.out.println("co2" + co2); //확인

                    // Array를 각각의 fragment에 보내는 코드 짜기 (ArrayList???)

                } catch (Exception e) {
                    e.printStackTrace();
                    System.out.println("co2 안되는듯"); //확인
                }


            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

            }
        });

        Bundle bundle = new Bundle();
        bundle.putString("user_id", user_id);
        switch (position) {

            case 0:
                TotalFragment totalFragment = new TotalFragment();
                System.out.println("케이스0" + user_id); //확인
                Bundle totalBundle = new Bundle();
                totalBundle.putString("user_id", user_id);
                totalFragment.setArguments(totalBundle); //Name 변수 값 전달. 생략시 받는 쪽에서 null 값으로 받음
                return totalFragment;

            case 1:
                Co2Fragment co2Fragment = new Co2Fragment();
                System.out.println("케이스1" + user_id); //확인
                Bundle co2Bundle = new Bundle();
                bundle.putString("user_id", user_id);
                co2Fragment.setArguments(co2Bundle); //Name 변수 값 전달. 생략시 받는 쪽에서 null 값으로 받음
                return co2Fragment;

            case 2:
                EmotionFragment emotionFragment = new EmotionFragment();
                System.out.println("케이스2" + user_id); //확인
                Bundle emotionBundle = new Bundle();
                bundle.putString("user_id", user_id);
                emotionFragment.setArguments(emotionBundle); //Name 변수 값 전달. 생략시 받는 쪽에서 null 값으로 받음
                return emotionFragment;

             case 3:
                EyeFragment eyeFragment = new EyeFragment();
                 System.out.println("케이스3" + user_id); //확인
                 Bundle eyeBundle = new Bundle();
                 bundle.putString("user_id", user_id);
                 eyeFragment.setArguments(eyeBundle); //Name 변수 값 전달. 생략시 받는 쪽에서 null 값으로 받음
                 return eyeFragment;

            default:
                return null;
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

    }

}