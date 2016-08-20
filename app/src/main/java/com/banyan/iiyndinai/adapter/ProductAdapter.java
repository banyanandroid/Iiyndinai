package com.banyan.iiyndinai.adapter;


import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.banyan.iiyndinai.R;
import com.banyan.iiyndinai.activity.Activity_Products;
import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Sangavi on 5/25/2016.
 */

public class ProductAdapter extends BaseAdapter {
    private Activity activity;
    private ArrayList<HashMap<String, String>> data;
    private static LayoutInflater inflater = null;
    Context context;
//    ImageLoader imageLoader = AppController.getInstance().getImageLoader();

    public ProductAdapter(Activity a, ArrayList<HashMap<String, String>> d) {
        activity = a;
        data = d;
        inflater = (LayoutInflater) activity
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public int getCount() {
        return data.size();
    }

    public Object getItem(int position) {
        return position;
    }

    public long getItemId(int position) {
        return position;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;
        if (convertView == null)
            v = inflater.inflate(R.layout.list_products, null);

        TextView name = (TextView) v.findViewById(R.id.prod_name_txt);
        TextView weight = (TextView) v.findViewById(R.id.prod_weight_txt);
        TextView price = (TextView) v.findViewById(R.id.prod_price_txt);
        // Button interested = (Button)v.findViewById(R.id.interested);
        ImageView prod_img = (ImageView) v.findViewById(R.id.prod_img);

        HashMap<String, String> result = new HashMap<String, String>();
        result = data.get(position);

        // no.setText(result.get(ResultFragment.TAG_TEAM_POSITION));
        name.setText(result.get(Activity_Products.TAG_PROD_NAME));
        weight.setText(result.get(Activity_Products.TAG_PROD_WEIGHT));
        price.setText(result.get(Activity_Products.TAG_PROD_PRICE));

        String impath = result.get(Activity_Products.TAG_PROD_IMAGE_URL);

        Glide.with(activity)
                .load(impath)
                .placeholder(R.mipmap.ic_launcher)
                .into(prod_img);

        // Checking for null feed url
        if (result.get(Activity_Products.TAG_PROD_IMAGE_URL) != null) {

        } else {
            // url is null, remove from the view
            System.out.println("SUCCESS"
                    + result.get(Activity_Products.TAG_PROD_IMAGE_URL));
        }

        return v;

    }

}
