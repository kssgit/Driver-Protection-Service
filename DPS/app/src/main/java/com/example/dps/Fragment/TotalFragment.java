package com.example.dps.Fragment;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.dps.R;

import org.eazegraph.lib.charts.PieChart;
import org.eazegraph.lib.models.PieModel;

import androidx.fragment.app.Fragment;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link TotalFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
// bundle은 Activity에서 Fragment 호출하며 데이터를 전달할 경우 사용
public class TotalFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    
    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public TotalFragment() {
        // Required empty public constructor
    }

    public static TotalFragment newInstance(String param1, String param2) {
        TotalFragment fragment = new TotalFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }
    
    View view;
    PieChart totalchart;

    // test
    TextView testview;
    String user_id;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_total, container, false);

        Bundle bundle = getArguments();
        if (bundle != null) {
            user_id = bundle.getString("user_id"); //Name 받기.
            System.out.println("TotalFragment : " + user_id); //확인
        }

        initView(view);

        return view;
    }



    public void initView(View v){
        totalchart = (PieChart) v.findViewById(R.id.totalchart);
//        testview = (TextView) v.findViewById(R.id.testView);
//        testview.setText(user_id);
        setPieChart();
    }

    // 파이 차트 설정
    private void setPieChart(){
        totalchart.clearChart();
        totalchart.addPieSlice(new PieModel("Freetime", 15, Color.parseColor("#FE6DA8")));
        totalchart.addPieSlice(new PieModel("Sleep", 25, Color.parseColor("#56B7F1")));
        totalchart.addPieSlice(new PieModel("Work", 35, Color.parseColor("#CDA67F")));
        totalchart.addPieSlice(new PieModel("Eating", 9, Color.parseColor("#FED70E")));
    }
}