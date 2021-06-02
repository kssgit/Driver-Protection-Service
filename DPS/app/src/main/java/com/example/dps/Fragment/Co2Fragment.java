package com.example.dps.Fragment;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.dps.R;

import org.eazegraph.lib.charts.BarChart;
import org.eazegraph.lib.models.BarModel;

import androidx.fragment.app.Fragment;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link Co2Fragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Co2Fragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    TextView co2_view;


    public Co2Fragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Co2Fragment.
     */
    // TODO: Rename and change types and number of parameters
    public static Co2Fragment newInstance(String param1, String param2) {
        Co2Fragment fragment = new Co2Fragment();
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
    String user_id;
    int amount;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_co2, container, false);
        Bundle bundle = getArguments();

        if (bundle != null) {
            user_id = bundle.getString("user_id"); //Name 받기.
            amount = bundle.getInt("amount");
            System.out.println("Co2Fragment: " + user_id); //확인
        }
        initView(view);

        return view;
    }

    public void initView(View v){
        co2_view = view.findViewById(R.id.co2_view);
        if(amount > 2000) {
            co2_view.setTextColor(Color.parseColor("#E71D36"));
            co2_view.setText("" + amount + "ppm");
        }
        if(amount < 2000) {
            co2_view.setTextColor(Color.parseColor("#FFFFF3"));
            co2_view.setText("" + amount + "ppm");
        }


    }



}