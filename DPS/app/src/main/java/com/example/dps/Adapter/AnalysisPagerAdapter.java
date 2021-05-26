package com.example.dps.Adapter;

import android.os.Bundle;

import com.example.dps.Fragment.Co2Fragment;
import com.example.dps.Fragment.EmotionFragment;
import com.example.dps.Fragment.EyeFragment;
import com.example.dps.Fragment.TotalFragment;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;



public class AnalysisPagerAdapter extends FragmentStatePagerAdapter {

//    Json_data
    JSONArray co2;
    JSONArray eye;
    JSONArray emotion;

    String[] co2_time;
    int[] co2_amount;
    int co2_len;

    String[] emotion_time;
    int[] emotion_emotion;
    int emotion_len;

    String[] eye_time;
    int[] eye_issleep;
    int eye_len;

    private int mPageCount;
    private String user_id;
    //생성자
    public AnalysisPagerAdapter(FragmentManager fm, int pageCount, String user_id,JSONArray co2,JSONArray emotion,JSONArray eye) {
        super(fm);
        this.mPageCount = pageCount;
        this.user_id= user_id;
        this.co2 =co2;
        this.eye =eye;
        this.emotion = emotion;

        //test 출력
        System.out.println(user_id);

        // Co2 : Json 형식 String으로 변환
        co2_len = co2.length();
        co2_time = new String[co2_len];
        co2_amount = new int[co2_len];

        for(int i=0; i<co2_len; i++) {
            try {
                JSONObject  jsonObject = co2.getJSONObject(i);
                co2_time[i] = jsonObject.getString("time");
                co2_amount[i] = jsonObject.getInt("amount");
                System.out.println("co2_amount: : " + co2_amount[i]);
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }

        // Emotion: Json 형식 String으로 변환
        emotion_len = emotion.length();
        emotion_time = new String[emotion_len];
        emotion_emotion = new int[emotion_len];

        for(int i=0; i<emotion_len; i++) {
            try {
                JSONObject  jsonObject = emotion.getJSONObject(i);
                emotion_time[i] = jsonObject.getString("time");
                emotion_emotion[i] = jsonObject.getInt("emotion");
                System.out.println("emotion_amount: : " + emotion_emotion[i]);
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }

        // Eye: Json 형식 String으로 변환
        eye_len = emotion.length();
        eye_time = new String[eye_len];
        eye_issleep = new int[eye_len];

        for(int i=0; i<eye_len; i++) {
            try {
                JSONObject  jsonObject = eye.getJSONObject(i);
                eye_time[i] = jsonObject.getString("time");
                eye_issleep[i] = jsonObject.getInt("is_sleep");
                System.out.println("eye_issleep: : " + eye_issleep[i]);
            } catch (JSONException e) {
                e.printStackTrace();
            }

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
                co2Bundle.putString("user_id", user_id);
                co2Bundle.putStringArray("time", co2_time);
                co2Bundle.putIntArray("amount", co2_amount);
                co2Fragment.setArguments(co2Bundle); //Name 변수 값 전달. 생략시 받는 쪽에서 null 값으로 받음
                return co2Fragment;

            case 2:
                EmotionFragment emotionFragment = new EmotionFragment();
                System.out.println("케이스2" + user_id); //확인

                Bundle emotionBundle = new Bundle();
                emotionBundle.putString("user_id", user_id);
                emotionBundle.putStringArray("time", emotion_time);
                emotionBundle.putIntArray("emotion", emotion_emotion);
                emotionFragment.setArguments(emotionBundle); //Name 변수 값 전달. 생략시 받는 쪽에서 null 값으로 받음
                return emotionFragment;

             case 3:
                EyeFragment eyeFragment = new EyeFragment();
                 System.out.println("케이스3" + user_id); //확인

                 Bundle eyeBundle = new Bundle();
                 eyeBundle.putString("user_id", user_id);
                 eyeBundle.putStringArray("time", eye_time);
                 eyeBundle.putIntArray("is_sleep", eye_issleep);
                 eyeFragment.setArguments(eyeBundle); //Name 변수 값 전달. 생략시 받는 쪽에서 null 값으로 받음
                 return eyeFragment;

            default:
                return null;
        }
    }

}