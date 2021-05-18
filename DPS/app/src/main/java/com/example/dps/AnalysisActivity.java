package com.example.dps;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.example.dps.Adapter.AnalysisPagerAdapter;
import com.google.android.material.tabs.TabLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;


public class AnalysisActivity extends AppCompatActivity {

    private Context mContext;
    private TabLayout mTabLayout;

    private ViewPager mViewPager;
    private AnalysisPagerAdapter mAnalysisPagerAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_analysis);

        Intent intent = getIntent();
        String user_id = intent.getExtras().getString("user_id");

        // TabLayout과 ViewPager 연결하기
        mContext = getApplicationContext();
        mTabLayout = (TabLayout) findViewById(R.id.analysis_tab_layout);
        mViewPager = (ViewPager) findViewById(R.id.pager_content);
        mAnalysisPagerAdapter = new AnalysisPagerAdapter(
                getSupportFragmentManager(), mTabLayout.getTabCount(), user_id);

        mViewPager.setAdapter(mAnalysisPagerAdapter);

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
    }
}