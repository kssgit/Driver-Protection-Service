package com.example.dps.Fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.dps.R;

import org.eazegraph.lib.charts.ValueLineChart;
import org.eazegraph.lib.models.ValueLinePoint;
import org.eazegraph.lib.models.ValueLineSeries;

import androidx.fragment.app.Fragment;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link Co2MeanFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Co2MeanFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public Co2MeanFragment() {
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
    public static Co2MeanFragment newInstance(String param1, String param2) {
        Co2MeanFragment fragment = new Co2MeanFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    View view;
    ValueLineChart co2meanchart;
    String user_id;
    int[] hour;
    int[] co2_mean;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_co2mean, container, false);
        Bundle bundle = getArguments();

        if (bundle != null) {
            user_id = bundle.getString("user_id"); //Name 받기.
            hour = bundle.getIntArray("hour");
            co2_mean = bundle.getIntArray("co2_mean");
            System.out.println("Co2MeanFragment: "+user_id); //확인

            for(int i=0;i<hour.length;i++){
                System.out.println( (i + 1) + "hour : "+hour[i]);
                System.out.println( (i + 1) + "co2_mean" + co2_mean[i]);
            }
        }
        initView(view);
        return view;
    }


    public void initView(View v){
        co2meanchart = (ValueLineChart) v.findViewById(R.id.co2meanchart);
        setBarChart();


    }

    // 막대 차트 설정
    private void setBarChart() {

        ValueLineSeries series = new ValueLineSeries();
        series.setColor(0xFF56B7F1);

        series.addPoint(new ValueLinePoint("공란", 0));

        for(int i=0;i<hour.length;i++){
            series.addPoint(new ValueLinePoint(Integer.toString(hour[i]), co2_mean[i]));
        }
        series.addPoint(new ValueLinePoint("공란", 0));

        co2meanchart.addSeries(series);
        co2meanchart.startAnimation();

    }

}