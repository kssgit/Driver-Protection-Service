package com.example.dps.Adapter;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.example.dps.Fragment.TotalFragment;
import com.example.dps.Fragment.Co2Fragment;
import com.example.dps.Fragment.EmotionFragment;
import com.example.dps.Fragment.EyeFragment;

public class AnalysisPagerAdapter extends FragmentStatePagerAdapter {
    private int mPageCount;

    public AnalysisPagerAdapter(FragmentManager fm, int pageCount) {
        super(fm);
        this.mPageCount = pageCount;
    }

    @Override

    public Fragment getItem(int position) {
        switch (position) {

            case 0:
                TotalFragment totalFragment = new TotalFragment();
                return totalFragment;

            case 1:
                Co2Fragment co2Fragment = new Co2Fragment();
                return co2Fragment;

            case 2:
                EmotionFragment emotionFragment = new EmotionFragment();
                return emotionFragment;

             case 3:
                EyeFragment eyeFragment = new EyeFragment();
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