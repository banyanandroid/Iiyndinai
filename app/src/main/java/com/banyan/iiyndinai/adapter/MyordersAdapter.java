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
import com.banyan.iiyndinai.activity.Activity_Orders_Detail;
import com.banyan.iiyndinai.activity.Activity_Products;
import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Jo on 23/8/2016.
 */

public class MyordersAdapter extends BaseAdapter {
    private Activity activity;
    private ArrayList<HashMap<String, String>> data;
    private static LayoutInflater inflater = null;
    Context context;
//    ImageLoader imageLoader = AppController.getInstance().getImageLoader();

    public MyordersAdapter(Activity a, ArrayList<HashMap<String, String>> d) {
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
            v = inflater.inflate(R.layout.list_myorder_details, null);

        TextView name = (TextView) v.findViewById(R.id.mycart_prod_name_txt);
        TextView price = (TextView) v.findViewById(R.id.mycart_prod_price_txt);
        TextView quantity = (TextView) v.findViewById(R.id.mycart_prod_quantity_txt);
        ImageView prod_img = (ImageView) v.findViewById(R.id.mycart_prod_img);

        HashMap<String, String> result = new HashMap<String, String>();
        result = data.get(position);

        String str_pro_name = result.get(Activity_Orders_Detail.TAG_PROD_NAME);
        String str_pro_weight = result.get(Activity_Orders_Detail.TAG_PROD_WEIGHT);
        String str_price = result.get(Activity_Orders_Detail.TAG_PROD_PRICE);
        String str_quantity = result.get(Activity_Orders_Detail.TAG_PROD_QUANTITY);

        name.setText(str_pro_name+"\t\t"+str_pro_weight);
        price.setText(str_price);
        quantity.setText("Quantity :\t"+str_quantity);

        String impath = result.get(Activity_Orders_Detail.TAG_PROD_IMAGE_URL);

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
