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
import com.banyan.iiyndinai.activity.Activity_WishList;
import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Jo on 23/8/2016.
 */

public class WishListAdapter extends BaseAdapter {
    private Activity activity;
    private ArrayList<HashMap<String, String>> data;
    private static LayoutInflater inflater = null;
    Context context;
//    ImageLoader imageLoader = AppController.getInstance().getImageLoader();

    public WishListAdapter(Activity a, ArrayList<HashMap<String, String>> d) {
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
            v = inflater.inflate(R.layout.list_item_wishlist, null);

        TextView name = (TextView) v.findViewById(R.id.wish_list_prod_name_txt);
        TextView price = (TextView) v.findViewById(R.id.wishlist_prod_price_txt);
        TextView remove = (TextView) v.findViewById(R.id.wishlist_product_txt_wishlist);
        ImageView prod_img = (ImageView) v.findViewById(R.id.wish_list_prod_img);

        HashMap<String, String> result = new HashMap<String, String>();
        result = data.get(position);

        String str_pro_name = result.get(Activity_WishList.TAG_PROD_NAME);
        String str_pro_weight = result.get(Activity_WishList.TAG_PROD_WEIGHT);
        String str_gram = result.get(Activity_WishList.TAG_PROD_GRAM_NAME);
        String str_weight_type = result.get(Activity_WishList.TAG_PROD_WEIGHT_TYPE);

        name.setText(str_pro_name + " " + str_gram + " " + str_pro_weight +str_weight_type);
        price.setText(result.get(Activity_WishList.TAG_PROD_PRICE));

        String impath = result.get(Activity_WishList.TAG_PROD_IMAGE_URL);

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
