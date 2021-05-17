package com.example.dps;

import android.content.Context;
import android.os.Bundle;

import com.example.dps.Adapter.AnalysisPagerAdapter;
import com.google.android.material.tabs.TabLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttMessageListener;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;


public class AnalysisActivity extends AppCompatActivity {

    private Context mContext;
    private TabLayout mTabLayout;

    private ViewPager mViewPager;
    private AnalysisPagerAdapter mAnalysisPagerAdapter;

    private MqttAndroidClient mqttAndroidClient;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_analysis);
        //mqtt 설정
        mqttAndroidClient = new MqttAndroidClient(this,"tcp://3.35.174.45:1883", MqttClient.generateClientId());
        try {
          IMqttToken token =mqttAndroidClient.connect();
          token.setActionCallback(new IMqttActionListener() {
              @Override
              public void onSuccess(IMqttToken asyncActionToken) {
                  try {
                      mqttAndroidClient.subscribe("test", 1, new IMqttMessageListener() {
                          @Override
                          public void messageArrived(String topic, MqttMessage message) throws Exception {
                            System.out.println(message.toString());
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
        // TabLayout과 ViewPager 연결하기
        mContext = getApplicationContext();
        mTabLayout = (TabLayout) findViewById(R.id.analysis_tab_layout);
        mViewPager = (ViewPager) findViewById(R.id.pager_content);
        mAnalysisPagerAdapter = new AnalysisPagerAdapter(
                getSupportFragmentManager(), mTabLayout.getTabCount());

        mViewPager.setAdapter(mAnalysisPagerAdapter);

        // ViewPager의 페이지가 변경될 때 알려주는 리스너
        mViewPager.addOnPageChangeListener(
                new TabLayout.TabLayoutOnPageChangeListener(mTabLayout));

        // Tab이 선택 되었을 때 알려주는 리스너
        mTabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            // Tab이 선택 되었을 때 호출되는 메서드
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                mViewPager.setCurrentItem(tab.getPosition());
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
    }
}