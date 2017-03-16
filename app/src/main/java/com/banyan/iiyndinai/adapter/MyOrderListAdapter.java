package com.banyan.iiyndinai.adapter;


import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.banyan.iiyndinai.R;
import com.banyan.iiyndinai.activity.Activity_My_Order;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Jo on 23/8/2016.
 */

public class MyOrderListAdapter extends BaseAdapter {
    private Activity activity;
    private ArrayList<HashMap<String, String>> data;
    private static LayoutInflater inflater = null;
    Context context;
//    ImageLoader imageLoader = AppController.getInstance().getImageLoader();

    public MyOrderListAdapter(Activity a, ArrayList<HashMap<String, String>> d) {
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
            v = inflater.inflate(R.layout.list_myorder, null);

        TextView orderid = (TextView) v.findViewById(R.id.order_txt_order_id);
        TextView date = (TextView) v.findViewById(R.id.order_txt_order_date);
        TextView price = (TextView) v.findViewById(R.id.order_price);
        TextView order_status = (TextView) v.findViewById(R.id.order_txt_order_status);
        TextView order_payment_type = (TextView) v.findViewById(R.id.order_txt_payment_type);

        HashMap<String, String> result = new HashMap<String, String>();
        result = data.get(position);

        String str_id = result.get(Activity_My_Order.TAG_ORDER_NUMBER);
        String str_date = result.get(Activity_My_Order.TAG_ORDER_DATE);
        String str_price = result.get(Activity_My_Order.TAG_TOTAL_AMOUNT);
        String str_status = result.get(Activity_My_Order.TAG_PROD_STATUS);
        String str_payment = result.get(Activity_My_Order.TAG_PROD_PAYMENT_TYPE);

        orderid.setText(str_id);
        date.setText(str_date);
        price.setText("Rs. " + str_price);
        order_payment_type.setText(str_payment);

        order_status.setText(str_status);

        return v;

    }

}
