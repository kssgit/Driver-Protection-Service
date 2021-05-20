package com.example.dps;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.util.Log;

import com.example.dps.Adapter.AnalysisPagerAdapter;
import com.google.android.material.tabs.TabLayout;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttMessageListener;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.util.Locale;


public class AnalysisActivity extends AppCompatActivity {

    private Context mContext;
    private TabLayout mTabLayout;

    private ViewPager mViewPager;
    private AnalysisPagerAdapter mAnalysisPagerAdapter;

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
                                System.out.println(message.toString());
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

//        User_id 가져오기
        Intent intent = getIntent();
        String user_id = intent.getExtras().getString("user_id");

        // TabLayout과 ViewPager 연결하기
        mContext = getApplicationContext();
        mTabLayout = (TabLayout) findViewById(R.id.analysis_tab_layout);
        mViewPager = (ViewPager) findViewById(R.id.pager_content);
        mAnalysisPagerAdapter = new AnalysisPagerAdapter(
                getSupportFragmentManager(), mTabLayout.getTabCount(), user_id);

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
}