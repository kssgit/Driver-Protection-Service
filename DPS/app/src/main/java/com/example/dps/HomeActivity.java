package com.example.dps;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.View;
import android.widget.Button;


import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttMessageListener;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.util.Locale;

public class HomeActivity extends AppCompatActivity implements TextToSpeech.OnInitListener{

    private MqttAndroidClient mqttAndroidClient;
    private TextToSpeech tts;

    private Button button;

    private String subMessage;

    public void a() {
        //tts
        tts = new TextToSpeech(HomeActivity.this , this);


        //mqtt 설정
        mqttAndroidClient = new MqttAndroidClient(this,"tcp://3.35.174.45:1883", MqttClient.generateClientId());
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
        setContentView(R.layout.activity_home);
        button = findViewById(R.id.graph_button);


        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 인텐트 선언 : 현재 액티비티, 넘어갈 액티비티
                Intent intent = new Intent(HomeActivity.this, AnalysisActivity.class);
                // 인텐트 실행
                startActivity(intent);
            }
        });



    }

    @Override
    protected void onStart() {
        super.onStart();
        a();
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

    @RequiresApi(api = Build.VERSION_CODES.R)
    @Override
    public void onInit(int status) {
        Log.e("status", status+"");
        Log.e("TextToSpeech", TextToSpeech.SUCCESS+"");

        if (status == TextToSpeech.SUCCESS) {
            int result = tts.setLanguage(Locale.KOREA);
            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                Log.e("TTS", "This Language is not supported");
            } else {
//                btn_Speak.setEnabled(true);
                speakOut();
            }
        } else {
            Log.e("TTS", "Initilization Failed!");
        }


    }
}