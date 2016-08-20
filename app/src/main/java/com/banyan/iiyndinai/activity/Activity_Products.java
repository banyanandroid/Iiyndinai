package com.banyan.iiyndinai.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.banyan.iiyndinai.R;
import com.banyan.iiyndinai.adapter.ProductAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Activity_Products extends AppCompatActivity {

    public static final String CID_URL = "http://iiyndinai.com/android/products.php";

    //Tag values to read from json
    public static final String TAG = "ProductList";
    public static final String TAG_PROD_IMAGE_URL = "product_image";
    public static final String TAG_PROD_NAME = "product_name";
    public static final String TAG_PROD_ID = "product_id";
    public static final String TAG_PROD_WEIGHT = "Weight_name";
    public static final String TAG_PROD_PRICE = "price";
    public static final String TAG_PROD_GRAM_NAME = "gram_name";
    public static final String TAG_PROD_GRAME_NAME = "grame_name";

    public static final String TAG_Sub_NAME = "sub_category";
    public static final String TAG_Sub_ID = "subcategory_list";

    String str_prod_name, str_prod_id, str_prod_image, str_prod_weight, str_prod_price, str_prod_gram, str_prod_grame_name;

    String str_select_id, str_select_name, str_select_price, str_select_weight;

    TinyDB tinydb;

    private ProductAdapter productAdapter;
    static ArrayList<HashMap<String, String>> prod_list_arr;
    static ArrayList<HashMap<String, String>> sub_category;
    static ArrayList cart_id_arr;
    static ArrayList cart_name_arr;
    static ArrayList cart_weight_arr;
    static ArrayList cart_price_arr;

    List<String> sub_cat;
    LinearLayout lin_sub_prod;
    ListView prod_listview;
    Spinner spinner;
    private ProgressBar mProgressBar;
    private Toolbar mToolbar;

    // Cart
    RelativeLayout notificationCount1;
    TextView tv;
    int i = 0;
    String str_cart;

    int count = 0;
    int product_list;

    String str_sid;
    int spinner_position, spinner_count;

    public static RequestQueue queue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_products);

        prod_list_arr = new ArrayList<HashMap<String, String>>();
        sub_category = new ArrayList<HashMap<String, String>>();
        cart_id_arr = new ArrayList();
        cart_name_arr = new ArrayList();
        cart_weight_arr = new ArrayList();
        cart_price_arr = new ArrayList();

        sub_cat = new ArrayList<String>();
        sub_cat.add("Select Category");

        tinydb = new TinyDB(Activity_Products.this);

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        lin_sub_prod = (LinearLayout) findViewById(R.id.lin_sub_prod);
        prod_listview = (ListView) findViewById(R.id.product_listview);
        spinner = (Spinner) findViewById(R.id.spinner);

        setSupportActionBar(mToolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        mToolbar.setNavigationIcon(getResources().getDrawable(R.drawable.ic_action_back));
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent i = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(i);
                finish();
            }
        });

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                spinner_position = i;
                spinner_count = i - 1;

                if (spinner_count < 0) {

                } else {
                    str_sid = sub_category.get(spinner_count).get(TAG_Sub_ID);
                    try {
                        queue = Volley.newRequestQueue(Activity_Products.this);
                        prod_list_arr.clear();
                        getDataAid();
                    } catch (Exception e) {
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        //Calling the getData method
        try {
            queue = Volley.newRequestQueue(Activity_Products.this);
            getDataCid();
        } catch (Exception e) {
            // TODO: handle exception
        }

        prod_listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {

                product_list = position;

                System.out.println("inside click listener");
                ImageView img = ((ImageView) view.findViewById(R.id.prod_plus_img));
                img.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        count = count + 1;
                        str_cart = Integer.toString(count);
                        tv.setText("" + str_cart);
                        productAdapter.notifyDataSetChanged();

                        str_select_id = prod_list_arr.get(product_list).get(TAG_PROD_ID);
                        str_select_name = prod_list_arr.get(product_list).get(TAG_PROD_NAME);
                        str_select_weight = prod_list_arr.get(product_list).get(TAG_PROD_WEIGHT);
                        str_select_price = prod_list_arr.get(product_list).get(TAG_PROD_PRICE);

                        System.out.println("Select ID" + str_select_id);
                        System.out.println("Select NAME" + str_select_name);
                        System.out.println("Select WEIGHT" + str_select_weight);
                        System.out.println("Select PRICE" + str_select_price);

                        cart_id_arr.add(str_select_id);
                        cart_name_arr.add(str_select_name);
                        cart_weight_arr.add(str_select_weight);
                        cart_price_arr.add(str_select_price);

                        HashMap<String, String> map1 = new HashMap<>();
                        map1.put(TAG_PROD_ID, str_select_id);
                        map1.put(TAG_PROD_NAME, str_select_name);
                        map1.put(TAG_PROD_WEIGHT, str_select_weight);
                        map1.put(TAG_PROD_PRICE, str_select_price);

                        //cart_arr.add(map1);
                        tinydb.putListString("Id",cart_id_arr);
                        tinydb.putListString("Name",cart_name_arr);
                        tinydb.putListString("Weight",cart_weight_arr);
                        tinydb.putListString("Price",cart_price_arr);


                        ArrayList<String> tiny_data = tinydb.getListString("Name");

                        System.out.println("Cart_Array" + cart_price_arr);
                        System.out.println("Cart_Array_db" + tiny_data);

                    }
                });
            }


        });
    }


    public void getDataCid() {


        System.out.println("CAME");

        StringRequest request = new StringRequest(Request.Method.POST,
                CID_URL, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d(TAG, response.toString());
                System.out.println("finding res" + response.toString());
                try {
                    JSONObject obj = new JSONObject(response);
                    JSONObject respon = obj.getJSONObject("response");
                    JSONArray subcate_arr = respon.getJSONArray("subcate");
                    JSONArray details_arr = respon.getJSONArray("details");


                    if (subcate_arr.length() != 0 && details_arr.length() != 0) {


                        for (int i = 0; subcate_arr.length() > i; i++) {
                            JSONObject obj1 = subcate_arr.getJSONObject(i);

                            String sub_catid = obj1.getString("sub_category");
                            String subcat_name = obj1.getString("subcategory_list");

                            sub_cat.add(subcat_name);

                            /*** Load Array to Spinner ***/

                            ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(Activity_Products.this, android.R.layout.simple_spinner_item, sub_cat);
                            dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                            spinner.setAdapter(dataAdapter);

                            /*** Hashmap Array for Sub category ***/

                            HashMap<String, String> map1 = new HashMap<>();
                            map1.put(TAG_Sub_ID, sub_catid);
                            map1.put(TAG_Sub_NAME, subcat_name);

                            sub_category.add(map1);
                        }


                        for (int j = 0; details_arr.length() > j; j++) {
                            JSONObject obj1 = details_arr.getJSONObject(j);

                            str_prod_name = obj1.getString(TAG_PROD_NAME);
                            str_prod_id = obj1.getString(TAG_PROD_ID);
                            str_prod_image = obj1.getString(TAG_PROD_IMAGE_URL);
                            str_prod_weight = obj1.getString(TAG_PROD_WEIGHT);
                            str_prod_price = obj1.getString(TAG_PROD_PRICE);
                            str_prod_gram = obj1.getString(TAG_PROD_GRAM_NAME);
                            str_prod_grame_name = obj1.getString(TAG_PROD_GRAME_NAME);


                            HashMap<String, String> map = new HashMap<>();
                            map.put(TAG_PROD_ID, str_prod_id);
                            map.put(TAG_PROD_NAME, str_prod_name);
                            map.put(TAG_PROD_IMAGE_URL, str_prod_image);
                            map.put(TAG_PROD_WEIGHT, str_prod_weight);
                            map.put(TAG_PROD_PRICE, str_prod_price);
                            map.put(TAG_PROD_GRAM_NAME, str_prod_gram);
                            map.put(TAG_PROD_GRAME_NAME, str_prod_grame_name);


                            prod_list_arr.add(map);

                        }

                    } else if (details_arr.length() != 0) {
                        lin_sub_prod.setVisibility(View.GONE);

                        for (int j = 0; details_arr.length() > j; j++) {
                            JSONObject obj1 = details_arr.getJSONObject(j);

                            str_prod_name = obj1.getString(TAG_PROD_NAME);
                            str_prod_id = obj1.getString(TAG_PROD_ID);
                            str_prod_image = obj1.getString(TAG_PROD_IMAGE_URL);
                            str_prod_weight = obj1.getString(TAG_PROD_WEIGHT);
                            str_prod_price = obj1.getString(TAG_PROD_PRICE);
                            str_prod_gram = obj1.getString(TAG_PROD_GRAM_NAME);
                            str_prod_grame_name = obj1.getString(TAG_PROD_GRAME_NAME);


                            HashMap<String, String> map = new HashMap<>();
                            map.put(TAG_PROD_ID, str_prod_id);
                            map.put(TAG_PROD_NAME, str_prod_name);
                            map.put(TAG_PROD_IMAGE_URL, str_prod_image);
                            map.put(TAG_PROD_WEIGHT, str_prod_weight);
                            map.put(TAG_PROD_PRICE, str_prod_price);
                            map.put(TAG_PROD_GRAM_NAME, str_prod_gram);
                            map.put(TAG_PROD_GRAME_NAME, str_prod_grame_name);

                            System.out.println("image url" + str_prod_image);


                            prod_list_arr.add(map);
                            System.out.println("Array" + prod_list_arr);

                        }

                    } else {

                        lin_sub_prod.setVisibility(View.GONE);
                        Toast.makeText(getApplicationContext(), "Sorry NO Products Available", Toast.LENGTH_SHORT).show();

                    }

                    productAdapter = new ProductAdapter(Activity_Products.this, prod_list_arr);
                    prod_listview.setAdapter(productAdapter);
                    //Hide progressbar
//                    mProgressBar.setVisibility(View.GONE);

                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                //  pDialog.hide();
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();

                params.put("cid", AppConfig.cid);
                System.out.println("CID " + AppConfig.cid);
                return params;
            }

        };

        // Adding request to request queue
        queue.add(request);
    }

    public void getDataAid() {


        System.out.println("CAMEAid");

        StringRequest request = new StringRequest(Request.Method.POST,
                CID_URL, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d(TAG, response.toString());
                System.out.println("finding res" + response.toString());
                try {
                    JSONObject obj = new JSONObject(response);
                    JSONObject respon = obj.getJSONObject("response");
                    JSONArray details_arr = respon.getJSONArray("details");


                    if (details_arr.length() != 0) {

                        for (int j = 0; details_arr.length() > j; j++) {
                            JSONObject obj1 = details_arr.getJSONObject(j);

                            str_prod_name = obj1.getString(TAG_PROD_NAME);
                            str_prod_id = obj1.getString(TAG_PROD_ID);
                            str_prod_image = obj1.getString(TAG_PROD_IMAGE_URL);
                            str_prod_weight = obj1.getString(TAG_PROD_WEIGHT);
                            str_prod_price = obj1.getString(TAG_PROD_PRICE);
                            str_prod_gram = obj1.getString(TAG_PROD_GRAM_NAME);
                            str_prod_grame_name = obj1.getString(TAG_PROD_GRAME_NAME);


                            HashMap<String, String> map = new HashMap<>();
                            map.put(TAG_PROD_ID, str_prod_id);
                            map.put(TAG_PROD_NAME, str_prod_name);
                            map.put(TAG_PROD_IMAGE_URL, str_prod_image);
                            map.put(TAG_PROD_WEIGHT, str_prod_weight);
                            map.put(TAG_PROD_PRICE, str_prod_price);
                            map.put(TAG_PROD_GRAM_NAME, str_prod_gram);
                            map.put(TAG_PROD_GRAME_NAME, str_prod_grame_name);


                            prod_list_arr.add(map);

                            System.out.println("Array Spinner" + prod_list_arr);
                        }

                    } else {

                        lin_sub_prod.setVisibility(View.GONE);
                        Toast.makeText(getApplicationContext(), "Sorry NO Products Available", Toast.LENGTH_SHORT).show();

                    }
                    productAdapter = new ProductAdapter(Activity_Products.this, prod_list_arr);
                    prod_listview.setAdapter(productAdapter);
                    //Hide progressbar
//                    mProgressBar.setVisibility(View.GONE);

                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                //  pDialog.hide();
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();

                params.put("sid", str_sid);
                System.out.println("SID " + str_sid);
                return params;
            }

        };

        // Adding request to request queue
        queue.add(request);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);

        MenuItem item1 = menu.findItem(R.id.actionbar_cart);
        MenuItemCompat.setActionView(item1, R.layout.notification_update_count_layout);
        notificationCount1 = (RelativeLayout) MenuItemCompat.getActionView(item1);
        tv = (TextView) notificationCount1.findViewById(R.id.badge_notification_2);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        MenuItemCompat.setActionView(item, R.layout.notification_update_count_layout);
        notificationCount1 = (RelativeLayout) MenuItemCompat.getActionView(item);
        tv = (TextView) notificationCount1.findViewById(R.id.badge_notification_2);

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        } else if (id == R.id.actionbar_cart) {


        }

        return super.onOptionsItemSelected(item);
    }
}
