package com.example.dps.Adapter;

import android.os.Bundle;

import com.example.dps.Fragment.Co2Fragment;
import com.example.dps.Fragment.EmotionFragment;
import com.example.dps.Fragment.EyeFragment;
import com.example.dps.Fragment.TotalFragment;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

public class AnalysisPagerAdapter extends FragmentStatePagerAdapter {
    private int mPageCount;
    private String user_id;
    public AnalysisPagerAdapter(FragmentManager fm, int pageCount, String user_id) {
        super(fm);
        this.mPageCount = pageCount;
        this.user_id= user_id;
    }

    @Override

    public Fragment getItem(int position) {
        Bundle bundle = new Bundle();
        bundle.putString("user_id", user_id);
        switch (position) {

            case 0:
                TotalFragment totalFragment = new TotalFragment();
                System.out.println("케이스2" + user_id); //확인
                // Bundle bundle1 = new Bundle();
                // bundle1.putString("user_id", user_id);
                totalFragment.setArguments(bundle); //Name 변수 값 전달. 생략시 받는 쪽에서 null 값으로 받음
                return totalFragment;

            case 1:
                Co2Fragment co2Fragment = new Co2Fragment();
                System.out.println("케이스1" + user_id); //확인
                co2Fragment.setArguments(bundle); //Name 변수 값 전달. 생략시 받는 쪽에서 null 값으로 받음
                return co2Fragment;

            case 2:
                EmotionFragment emotionFragment = new EmotionFragment();
                System.out.println("케이스2" + user_id); //확인
                emotionFragment.setArguments(bundle); //Name 변수 값 전달. 생략시 받는 쪽에서 null 값으로 받음
                return emotionFragment;

             case 3:
                EyeFragment eyeFragment = new EyeFragment();
                 System.out.println("케이스3" + user_id); //확인
                 eyeFragment.setArguments(bundle); //Name 변수 값 전달. 생략시 받는 쪽에서 null 값으로 받음
                 return eyeFragment;

            default:
                return null;
        }
    }



    @Override

    public int getCount() {

        return mPageCount;

    }

}