package com.banyan.iiyndinai.activity;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.banyan.iiyndinai.R;
import com.banyan.iiyndinai.adapter.GridViewAdapter;
import com.banyan.iiyndinai.model.GridItem;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class HomeFragment extends Fragment {

    public HomeFragment() {
    }

    private static final String TAG = MainActivity.class.getSimpleName();

    ImageView img_slider;
    private GridView mGridView;
    private ProgressBar mProgressBar;
    GridItem item;
    String str_selected_id;

    private GridViewAdapter mGridAdapter;
    private ArrayList<GridItem> mGridData;

    //Web api url
    public static final String DATA_URL = "http://iiyndinai.com/android/ws_category.php";


    //Tag values to read from json
    public static final String TAG_IMAGE_URL = "category_image";
    public static final String TAG_NAME = "category_name";
    public static final String TAG_ID = "category_id";
    public static final String TAG_POS = "category_pos";
    public static final String TAG_STATUS = "category_status";

    public static RequestQueue queue;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_home, container, false);

        img_slider = (ImageView) rootView.findViewById(R.id.home_img_slider);
        mGridView = (GridView) rootView.findViewById(R.id.home_gridView);
        mProgressBar = (ProgressBar) rootView.findViewById(R.id.home_progressBar);

        //Initialize with empty data
        mGridData = new ArrayList<>();

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

        //Grid view click event
        mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                //Get item at position
                GridItem item = (GridItem) parent.getItemAtPosition(position);

                str_selected_id = mGridData.get(position).getID();

                AppConfig.cid = str_selected_id;

                Intent in = new Intent(getActivity(), Activity_Products.class);
                startActivity(in);
            }
        });


        //Calling the getData method
        try {
            mProgressBar.setVisibility(View.VISIBLE);
            queue = Volley.newRequestQueue(getActivity());
            getData();

        } catch (Exception e) {
            // TODO: handle exception
        }


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

    private void getData() {

        StringRequest request = new StringRequest(Request.Method.GET,
                DATA_URL, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d(TAG, response.toString());
                try {
                    JSONObject obj = new JSONObject(response);
                    int success = obj.getInt("success");

                    if (success == 1) {
                        JSONArray arr1;

                        arr1 = obj.getJSONArray("category");


                        for (int i = 0; arr1.length() > i; i++) {
                            JSONObject obj1 = arr1.getJSONObject(i);

                            String cate_name = obj1.getString(TAG_NAME);
                            String cate_image = obj1.getString(TAG_IMAGE_URL);
                            String cate_id = obj1.getString(TAG_ID);

                            item = new GridItem();
                            item.setTitle(cate_name);
                            item.setImage(cate_image);
                            item.setID(cate_id);
                            mGridData.add(item);
                        }

                        mGridAdapter = new GridViewAdapter(getActivity(), R.layout.home_grid_item_layout, mGridData);
                        mGridView.setAdapter(mGridAdapter);
                        //Hide progressbar
                        mProgressBar.setVisibility(View.GONE);


                    }
                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();


                return params;
            }

        };

        // Adding request to request queue
        queue.add(request);
    }


}
