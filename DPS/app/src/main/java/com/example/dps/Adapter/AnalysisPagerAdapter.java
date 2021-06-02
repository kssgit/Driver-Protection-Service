package com.example.dps.Adapter;

import android.os.Bundle;

import com.example.dps.Fragment.Co2Fragment;
import com.example.dps.Fragment.Co2MeanFragment;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;



public class AnalysisPagerAdapter extends FragmentStatePagerAdapter {

    //  Json_data
    Integer co2;

    // co2_mean
    JSONArray arr_Co2_hour;
    JSONArray arr_Co2_Mean;
    int len = 24;
    int[] co2_hour;
    int[] co2_mean;

    private int mPageCount;
    private String user_id;
    //생성자
    public AnalysisPagerAdapter(FragmentManager fm, int pageCount, String user_id,Integer co2,JSONObject co2Mean) {
        super(fm);
        this.mPageCount = pageCount;
        this.user_id = user_id;
        this.co2 = co2;


        //test 출력
        System.out.println(user_id);

        // Co2 : Json 형식 String으로 변환
        try {
            arr_Co2_hour = (JSONArray) co2Mean.get("hour");
            arr_Co2_Mean = (JSONArray) co2Mean.get("co2_mean");
            len = arr_Co2_hour.length();
            System.out.println("hour_len: " + len);

            co2_hour = new int[len];
            co2_mean = new int[len];
            for(int i=0; i<len; i++)
            {

                co2_hour[i] = arr_Co2_hour.getInt(i);
                co2_mean[i] = (int) Math.round(arr_Co2_Mean.getDouble(i));


            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        for(int i=0; i<len; i++)
        {
            System.out.println("co2_hour: " + co2_hour);
            System.out.println("co2_mean: " + co2_mean);
        }
    }

    @Override
    public int getCount() {
        return mPageCount;
    }

    @Override
    public Fragment getItem(int position) {

        switch (position) {

            case 0:
                Co2Fragment co2Fragment = new Co2Fragment();
                System.out.println("케이스1" + user_id); //확인

                Bundle co2Bundle = new Bundle();
                co2Bundle.putString("user_id", user_id);
                co2Bundle.putInt("amount", co2);
                co2Fragment.setArguments(co2Bundle); //Name 변수 값 전달. 생략시 받는 쪽에서 null 값으로 받음
                return co2Fragment;

            case 1:
                Co2MeanFragment co2MeanFragment = new Co2MeanFragment();
                System.out.println("케이스2" + user_id); //확인

                Bundle co2MeanBundle = new Bundle();
                co2MeanBundle.putString("user_id", user_id);
                co2MeanBundle.putIntArray("hour", co2_hour);
                co2MeanBundle.putIntArray("co2_mean", co2_mean);
                co2MeanFragment.setArguments(co2MeanBundle); //Name 변수 값 전달. 생략시 받는 쪽에서 null 값으로 받음
                return co2MeanFragment;

            default:
                return null;
        }
    }

}