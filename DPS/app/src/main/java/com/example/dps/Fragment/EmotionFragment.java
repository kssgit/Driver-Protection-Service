package com.example.dps.Fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.dps.R;

import org.eazegraph.lib.charts.BarChart;
import org.eazegraph.lib.models.BarModel;

import androidx.fragment.app.Fragment;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link EmotionFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class EmotionFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public EmotionFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment EmotionFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static EmotionFragment newInstance(String param1, String param2) {
        EmotionFragment fragment = new EmotionFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    View view;
    BarChart emotionchart;
    String user_id;
    String[] time;
    int[] emotion;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_emotion, container, false);
        Bundle bundle = getArguments();

        if (bundle != null) {
            user_id = bundle.getString("user_id"); //Name 받기.
            time = bundle.getStringArray("time");
            emotion = bundle.getIntArray("emotion");
            System.out.println("EmotionFragment: "+user_id); //확인

            for(int i=0;i<time.length;i++){
                System.out.println( (i + 1) + "time : "+time[i]);
                System.out.println( (i + 1) + "emotion" + emotion[i]);
            }
        }
        initView(view);
        return view;
    }


    public void initView(View v){
        emotionchart = (BarChart) v.findViewById(R.id.emotionchart);
        setBarChart();


    }

    // 막대 차트 설정
    private void setBarChart() {

        emotionchart.clearChart();

        for(int i=0;i<time.length;i++){
            emotionchart.addBar(new BarModel(time[i].substring(11, 16), emotion[i], 0xFF56B7F1));
        }

        emotionchart.startAnimation();

    }

}