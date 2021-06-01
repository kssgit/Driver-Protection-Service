package com.example.dps;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.ContextMenu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;


import com.example.dps.Adapter.AnalysisPagerAdapter;
import com.example.dps.login.SaveSharedPreference;
import com.example.dps.notification.Constants;
import com.example.dps.notification.NotificationHelper;
import com.example.dps.notification.PreferenceHelper;

import com.github.angads25.toggle.LabeledSwitch;
import com.github.angads25.toggle.interfaces.OnToggledListener;
import com.google.android.material.tabs.TabLayout;
import com.google.gson.JsonObject;

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

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.viewpager.widget.ViewPager;
import androidx.work.WorkManager;

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
    ImageButton img_btn;
    TextView co2_view;
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



    // 푸시알림 설정
    private void initSwitchLayout(final WorkManager workManager) {
        LabeledSwitch labeledSwitch = findViewById(R.id.switch_second_notify);
        labeledSwitch.setOn(PreferenceHelper.getBoolean(
                getApplicationContext(), Constants.SHARED_PREF_NOTIFICATION_KEY
        ));
        labeledSwitch.setOnToggledListener(new OnToggledListener() {
            @Override
            public void onSwitched(LabeledSwitch labeledSwitch, boolean isOn) {
                if (isOn) {
                    Toast.makeText(AnalysisActivity.this, "8시 푸시알람이 켜졌습니다", Toast.LENGTH_SHORT).show();
                    boolean isChannelCreated = NotificationHelper.isNotificationChannelCreated(getApplicationContext());
                    if (isChannelCreated) {
                        PreferenceHelper.setBoolean(getApplicationContext(), Constants.SHARED_PREF_NOTIFICATION_KEY, true);
                        NotificationHelper.setScheduledNotification(workManager);
                    } else {
                        NotificationHelper.createNotificationChannel(getApplicationContext());
                    }
                } else {
                    Toast.makeText(AnalysisActivity.this, "8시 푸시알람이 꺼졌습니다", Toast.LENGTH_SHORT).show();
                    PreferenceHelper.setBoolean(getApplicationContext(), Constants.SHARED_PREF_NOTIFICATION_KEY, false);
                    workManager.cancelAllWork();
                }
            }
        });
    }
    //mqtt_pub
    public void mqtt_pub(){
        mqttAndroidClient = new MqttAndroidClient(this,"tcp://54.180.214.221:1883", MqttClient.generateClientId());

        try {
            mqttAndroidClient.connect();
            mqttAndroidClient.publish("android/01", new MqttMessage(new String("start").getBytes()));
            System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>pub 성공");
        }catch (Exception e){
            e.printStackTrace();
        }
    }


//  mqtt sub
    public void mqtt_sub() {
        mqttAndroidClient = new MqttAndroidClient(this,"tcp://54.180.214.221:1883", MqttClient.generateClientId());
//        mqttAndroidClient = new MqttAndroidClient(this,"tcp://13.208.255.135:1883", MqttClient.generateClientId());
        //알람 mp3 설정
        MediaPlayer player = MediaPlayer.create(this,R.raw.alam);
        //co2 text
        co2_view = findViewById(R.id.co2_view);

        try {
            IMqttToken token =mqttAndroidClient.connect();
            token.setActionCallback(new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    try {
                        mqttAndroidClient.subscribe("android/"+user_id, 1, new IMqttMessageListener() {
                            @RequiresApi(api = Build.VERSION_CODES.R)
                            @Override
                            public void messageArrived(String topic, MqttMessage message) throws Exception {
                                //TTS 변환 및 팝업 activity 실행
//
                                String jsonmessage = message.toString();
                                JSONObject jsonObj = new JSONObject(jsonmessage);
                                Integer type = (Integer)jsonObj.get("type");

                                if (type == 1){
                                    subMessage = (String)jsonObj.get("message");
                                    speakOut();
                                }
                                if(type == 2){
                                    player.start();
                                }
                                if(type == 3){
                                    Integer co2_v = (Integer) jsonObj.get("co2");
                                    if(co2_v > 2000) {
                                        co2_view.setTextColor(Color.parseColor("#E71D36"));
                                        co2_view.setText("" + co2_v + "ppm");
                                    }
                                    if(co2_v < 2000) {
                                        co2_view.setTextColor(Color.parseColor("#FFFFF3"));
                                        co2_view.setText("" + co2_v + "ppm");
                                    }
                                }


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
        mqtt_pub();

        //tts
        tts = new TextToSpeech(this , listener);

        //User_id 가져오기
        Intent intent = getIntent();
        user_id = intent.getExtras().getString("user_id");

        mContext = getApplicationContext();
        mTabLayout = (TabLayout) findViewById(R.id.analysis_tab_layout);
        mViewPager = (ViewPager) findViewById(R.id.pager_content);

        //메뉴 위젯 등록
        img_btn =(ImageButton)findViewById(R.id.menu_btn);
        registerForContextMenu(img_btn);

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

                    //네비게이션 바
//                    final NavigationTabStrip navigationTabStrip = (NavigationTabStrip) findViewById(R.id.navigation_header);
//                    navigationTabStrip.setTitles("종합", "Co2", "졸음","감정");

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
        mqtt_sub() ;

        //notification
        initSwitchLayout(WorkManager.getInstance(getApplicationContext()));

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
                SaveSharedPreference.clearUserName(AnalysisActivity.this);
                ActivityCompat.finishAffinity(AnalysisActivity.this);
                System.exit(0);
                return true;
            case R.id.user_update:
                System.out.println();
                return true;
            case R.id.all_data:
                return true;
            case R.id.one_day_data:


                return true;
        }

        return false;
    }


    @RequiresApi(api = Build.VERSION_CODES.R)
    private void speakOut() {
        CharSequence text = subMessage;
        tts.setPitch((float) 0.3);
        tts.setSpeechRate((float) 1.0);
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