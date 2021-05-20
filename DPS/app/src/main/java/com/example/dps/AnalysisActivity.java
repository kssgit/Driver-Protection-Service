package com.example.dps;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.util.Log;

import com.example.dps.Adapter.AnalysisPagerAdapter;
import com.google.android.material.tabs.TabLayout;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttMessageListener;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.json.JSONArray;
import org.json.JSONObject;

import java.security.cert.CertificateException;
import java.util.Locale;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;
import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class AnalysisActivity extends AppCompatActivity {
    private String user_id;
    private Context mContext;
    private TabLayout mTabLayout;

    private ViewPager mViewPager;
    private AnalysisPagerAdapter mAnalysisPagerAdapter;
    //JsonData
    JSONArray co2;
    JSONArray eye;
    JSONArray emotion;
    //retrofit
    Retrofit retrofit;
    RetrofitAPI retrofitAPI;
    //mqtt
    private String subMessage;
    private MqttAndroidClient mqttAndroidClient;
    //tts
    private TextToSpeech tts;
    TextToSpeech.OnInitListener listener = new TextToSpeech.OnInitListener() {
        @RequiresApi(api = Build.VERSION_CODES.R)
        @Override
        public void onInit(int status) {
            if (status == TextToSpeech.SUCCESS) {
                int result = tts.setLanguage(Locale.KOREA);
                if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                    Log.e("TTS", "This Language is not supported");
                } else {
                    //btn_Speak.setEnabled(true);
//                    speakOut();
                }
            } else {
                Log.e("TTS", "Initilization Failed!");
            }
        }
    };

//  mqtt
    public void mqtt() {
        mqttAndroidClient = new MqttAndroidClient(this,"tcp://13.208.255.135:1883", MqttClient.generateClientId());
        try {
            IMqttToken token =mqttAndroidClient.connect();
            token.setActionCallback(new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    try {
                        mqttAndroidClient.subscribe("test", 1, new IMqttMessageListener() {
                            @RequiresApi(api = Build.VERSION_CODES.R)
                            @Override
                            public void messageArrived(String topic, MqttMessage message) throws Exception {
                                System.out.println("1"+message.toString());
                                //TTS 변환 및 팝업 activity 실행
                                subMessage = message.toString();
                                speakOut();
                            }
                        });
                    } catch (MqttException e) {
                        e.printStackTrace();
                    }
                }
                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                }
            });
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_analysis);
        //tts
        tts = new TextToSpeech(this , listener);

        //User_id 가져오기
        Intent intent = getIntent();
        user_id = intent.getExtras().getString("user_id");

        //Json_data 가져오기
        //  1. user_id를 이용해서 장고에 데이터 요청
        //  2. 해당하는 데이터를 케이스에 따라 보내기
        retrofit = new Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create()) // converter 선언
                .baseUrl(retrofitAPI.REGIST_URL)
                .client(getUnsafeOkHttpClient().build())
                .build();
        retrofitAPI = retrofit.create(RetrofitAPI.class);
        Call<ResponseBody> call = retrofitAPI.getUserdata(user_id);
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

                    // Array를 각각의 fragment에 보내는 코드 짜기 (ArrayList???)
                    // TabLayout과 ViewPager 연결하기
                    mContext = getApplicationContext();
                    mTabLayout = (TabLayout) findViewById(R.id.analysis_tab_layout);
                    mViewPager = (ViewPager) findViewById(R.id.pager_content);
                    mAnalysisPagerAdapter = new AnalysisPagerAdapter(
                            getSupportFragmentManager(), mTabLayout.getTabCount(), user_id,co2,emotion,eye);

                    mViewPager.setAdapter(mAnalysisPagerAdapter);
                    mViewPager.setOffscreenPageLimit(mTabLayout.getTabCount());

                    // ViewPager의 페이지가 변경될 때 알려주는 리스너
                    mViewPager.addOnPageChangeListener(
                            new TabLayout.TabLayoutOnPageChangeListener(mTabLayout));

                    // Tab이 선택 되었을 때 알려주는 리스너
                    mTabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
                        // Tab이 선택 되었을 때 호출되는 메서드
                        @Override
                        public void onTabSelected(TabLayout.Tab tab) {
                            mViewPager.setCurrentItem(tab.getPosition()); // 해당 탭으로 전환
                            System.out.println("계속 나오는건가?");
                        }
                        // Tab이 선택되지 않았을 때 호출되는 메서드
                        @Override
                        public void onTabUnselected(TabLayout.Tab tab) {

                        }
                        // Tab이 다시 선택되었을 때 호출되는 메서드
                        @Override
                        public void onTabReselected(TabLayout.Tab tab) {

                        }
                    });

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


        //mqtt 호출
        mqtt() ;
    }

    @RequiresApi(api = Build.VERSION_CODES.R)
    private void speakOut() {
        CharSequence text = subMessage;
        tts.setPitch((float) 0.6);
        tts.setSpeechRate((float) 0.1);
        tts.speak(text,TextToSpeech.QUEUE_FLUSH,null,"id1");
    }

    @Override public void onDestroy() {
        if (tts != null) {
            tts.stop();
            tts.shutdown();
        }
        super.onDestroy();
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