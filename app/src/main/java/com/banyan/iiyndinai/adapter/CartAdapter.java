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
import com.banyan.iiyndinai.activity.Activity_Cart;
import com.banyan.iiyndinai.activity.Activity_Products;
import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Jo on 5/25/2016.
 */

public class CartAdapter extends BaseAdapter {
    private Activity activity;
    private ArrayList<HashMap<String, String>> data;
    HashMap<String, String> result;
    int pos;
    private static LayoutInflater inflater = null;
    ViewHolder holder = null;
    Context context;
//    ImageLoader imageLoader = AppController.getInstance().getImageLoader();

    public CartAdapter(Activity a, ArrayList<HashMap<String, String>> d) {
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

    public View getView(final int position, View convertView, ViewGroup parent) {
        View v = convertView;
        if (convertView == null)
            v = inflater.inflate(R.layout.list_cart_prod, null);

        holder = new ViewHolder();
        pos = position;

        holder.cart_item_name = (TextView) v.findViewById(R.id.cart_item_name);
        holder.cart_weight = (TextView) v.findViewById(R.id.cart_weight_txt);
        holder.cart_price = (TextView) v.findViewById(R.id.cart_price_txt);
        holder.cart_item_unitprice = (TextView) v.findViewById(R.id.cart_item_unitprice);
        holder.cart_quantity = (TextView) v.findViewById(R.id.cart_value_txt);
        holder.prod_kg_txt = (TextView) v.findViewById(R.id.prod_kg_txt);
        holder.cart_item_img = (ImageView) v.findViewById(R.id.cart_list_image);
        holder.cart_delet_img = (ImageView) v.findViewById(R.id.cart_cancel_img);
        holder.plus_img = (ImageView) v.findViewById(R.id.cart_add_img);
        holder.minus_img = (ImageView) v.findViewById(R.id.cart_minus_img);

        result = new HashMap<String, String>();
        result = data.get(position);

        // no.setText(result.get(ResultFragment.TAG_TEAM_POSITION));
        holder.cart_item_name.setText(result.get(Activity_Cart.TAG_CART_PRODUCT_NAME));
        holder.cart_weight.setText(result.get(Activity_Cart.TAG_CART_WEIGHT_NAME));
        holder.cart_price.setText(result.get(Activity_Cart.TAG_CART_SUB_TOTAL));
        holder.cart_item_unitprice.setText(result.get(Activity_Cart.TAG_CART_UNIT_PRICE));
        holder.prod_kg_txt.setText(result.get(Activity_Cart.TAG_CART_GRAME_NAME));
        holder.cart_quantity.setText(result.get(Activity_Cart.TAG_CART_QUANTITY));
        String impath = result.get(Activity_Cart.TAG_CART_PRODUCT_IMAGE);

        // Checking for null feed url
        if (result.get(Activity_Products.TAG_PROD_IMAGE_URL) != null) {

            Glide.with(activity)
                    .load(impath)
                    .placeholder(R.mipmap.ic_launcher)
                    .into(holder.cart_item_img);

        } else {
            // url is null, remove from the view
            System.out.println("SUCCESS"
                    + result.get(Activity_Products.TAG_PROD_IMAGE_URL));
        }

        return v;

    }



    private class ViewHolder {
        // public TextView textViewItem;
        public ImageView cart_item_img,cart_delet_img;
        public ImageView plus_img,minus_img;
        public TextView cart_price,cart_weight,cart_item_name, cart_quantity,cart_item_unitprice,prod_kg_txt;
    }

}
