package com.banyan.iiyndinai.activity;


import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.banyan.iiyndinai.R;


public class HomeFragment extends Fragment {

    public HomeFragment() {
    }

    ImageView img_slider;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_home, container, false);

        img_slider = (ImageView) rootView.findViewById(R.id.home_img_slider);

        /**************************************
         *  Slider Function
         * ************************************/
        final int[] slide = new int[]{R.drawable.banner1, R.drawable.banner2, R.drawable.banner3, R.drawable.banner4};

        final Handler handler = new Handler();
        Runnable runnable = new Runnable() {
            int i = 0;

            public void run() {
                img_slider.setImageResource(slide[i]);
                i++;
                if (i > slide.length - 1) {
                    i = 0;
                }
                handler.postDelayed(this, 10000);  //for interval... slide changes
            }
        };
        handler.postDelayed(runnable, 10000); //for initial delay..



        return rootView;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }
}
