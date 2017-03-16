package com.banyan.iiyndinai.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.banyan.iiyndinai.R;
import com.banyan.iiyndinai.adapter.WishListAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;


public class WishlistFragment extends Fragment {

    private static final String TAG = MainActivity.class.getSimpleName();

    //Tag values to read from json
    public static final String TAG_PROD_ID = "product_id";
    public static final String TAG_PROD_IMAGE_URL = "product_image";
    public static final String TAG_PROD_NAME = "product_name";
    public static final String TAG_PROD_GRAM_NAME = "gram_name";
    public static final String TAG_PROD_WEIGHT = "Weight_name";
    public static final String TAG_PROD_PRICE = "price";
    public static final String TAG_PROD_WEIGHT_TYPE = "weight_type";

    String str_prod_name, str_prod_id, str_prod_image, str_prod_weight, str_prod_price, str_prod_gram, str_prod_weight_type;

    String str_select_id, str_select_name, str_select_price, str_select_weight, str_select_img;

    private WishListAdapter wishlistAdapter;
    static ArrayList<HashMap<String, String>> prod_list_arr;

    ListView wishlist_listview;
    Spinner spinner;
    private ProgressBar mProgressBar;
    private ProgressDialog pDialog;

    // Cart
    RelativeLayout notificationCount1;
    TextView tv;
    int i = 0;
    String str_cart;

    int count = 0;
    int product_list;

    String str_name, str_userid;
    String str_selected_product, str_selected_opid;

    TextView txt_productname, txt_remove_wish_list;
    ImageView img_add;

    // Session Manager Class
    SessionManager session;

    // Tiny DB

    TinyDB tinydb;
    ArrayList<String> tiny_id;
    int cart_size;

    public static RequestQueue queue;

    public WishlistFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_wishlist, container, false);

        prod_list_arr = new ArrayList<HashMap<String, String>>();

        session = new SessionManager(getActivity());

        session.checkLogin();

        // get user data from session
        HashMap<String, String> user = session.getUserDetails();

        // name
        str_name = user.get(SessionManager.KEY_USER);
        str_userid = user.get(SessionManager.KEY_USER_ID);

        tinydb = new TinyDB(getActivity());
        tiny_id = tinydb.getListString("Id");
        cart_size = tiny_id.size();

        wishlist_listview = (ListView) rootView.findViewById(R.id.wishlist_listview);
        tv = (TextView) getActivity().findViewById(R.id.badge_notification_2);


        wishlist_listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {

                product_list = position;

                str_selected_product = prod_list_arr.get(product_list).get(TAG_PROD_ID);

                txt_remove_wish_list = (TextView) arg1.findViewById(R.id.wishlist_product_txt_wishlist);
                txt_productname = (TextView) arg1.findViewById(R.id.wish_list_prod_name_txt);
                img_add = (ImageView) arg1.findViewById(R.id.wishlist_prod_plus_img);

                txt_remove_wish_list.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        System.out.println("CLICKEDDDDD !!!");

                        str_select_id = prod_list_arr.get(product_list).get(TAG_PROD_ID);

                        try {
                            pDialog = new ProgressDialog(getActivity());
                            pDialog.setMessage("Please wait...");
                            pDialog.show();
                            pDialog.setCancelable(false);
                            queue = Volley.newRequestQueue(getActivity());
                            RemoveWishlist();
                        } catch (Exception e) {
                            // TODO: handle exception
                        }
                    }
                });

                txt_productname.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        System.out.println("CLICKEDDDDD !!!");

                        str_select_id = prod_list_arr.get(product_list).get(TAG_PROD_ID);

                        try {
                            Intent i = new Intent(getActivity(), Activity_Product_Description.class);
                            i.putExtra("select_id", str_selected_product);
                            startActivity(i);
                        } catch (Exception e) {
                            // TODO: handle exception
                        }
                    }
                });

                img_add.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        try {
                            count = count + 1;
                            str_cart = Integer.toString(count);
                            tv.setText("" + str_cart);
                        } catch (Exception e) {

                        }

                        str_select_id = prod_list_arr.get(product_list).get(TAG_PROD_ID);
                        str_select_name = prod_list_arr.get(product_list).get(TAG_PROD_NAME);
                        str_select_weight = prod_list_arr.get(product_list).get(TAG_PROD_WEIGHT);
                        str_select_price = prod_list_arr.get(product_list).get(TAG_PROD_PRICE);
                        str_select_img = prod_list_arr.get(product_list).get(TAG_PROD_IMAGE_URL);

                        AppConfig.cart_id_arr.add(str_select_id);
                        AppConfig.cart_name_arr.add(str_select_name);
                        AppConfig.cart_weight_arr.add(str_select_weight);
                        AppConfig.cart_price_arr.add(str_select_price);
                        AppConfig.cart_image_arr.add(str_select_img);

                        HashMap<String, String> map1 = new HashMap<>();
                        map1.put(TAG_PROD_ID, str_select_id);
                        map1.put(TAG_PROD_NAME, str_select_name);
                        map1.put(TAG_PROD_WEIGHT, str_select_weight);
                        map1.put(TAG_PROD_PRICE, str_select_price);

                        //cart_arr.add(map1);
                        tinydb.putListString("Id", AppConfig.cart_id_arr);
                        tinydb.putListString("Name", AppConfig.cart_name_arr);
                        tinydb.putListString("Weight", AppConfig.cart_weight_arr);
                        tinydb.putListString("Price", AppConfig.cart_price_arr);
                        tinydb.putListString("Image", AppConfig.cart_image_arr);


                        ArrayList<String> tiny_data = tinydb.getListString("Name");
                        cart_size = tiny_data.size();
                        //str_cart = Integer.toString(count);
                        tv.setText("" + cart_size);

                        System.out.println("Cart_Array" + AppConfig.cart_price_arr);
                        System.out.println("Cart_Array_db" + tiny_data);

                    }
                });


            }
        });


        /*wishlist_listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {

                product_list = position;

                str_selected_product = prod_list_arr.get(product_list).get(TAG_PROD_ID);

                ImageView img = ((ImageView) getActivity().findViewById(R.id.prod_plus_img));
                img.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        count = count + 1;
                        str_cart = Integer.toString(count);
                        tv.setText("" + str_cart);
                        wishlistAdapter.notifyDataSetChanged();

                        str_select_id = prod_list_arr.get(product_list).get(TAG_PROD_ID);
                        str_select_name = prod_list_arr.get(product_list).get(TAG_PROD_NAME);
                        str_select_weight = prod_list_arr.get(product_list).get(TAG_PROD_WEIGHT);
                        str_select_price = prod_list_arr.get(product_list).get(TAG_PROD_PRICE);


                    }
                });



                txt_remove_wish_list.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        System.out.println("CLICKEDDDDD !!!");

                        str_select_id = prod_list_arr.get(product_list).get(TAG_PROD_ID);

                        try {
                            pDialog = new ProgressDialog(getActivity());
                            pDialog.setMessage("Please wait...");
                            pDialog.show();
                            pDialog.setCancelable(false);
                            queue = Volley.newRequestQueue(getActivity());
                            RemoveWishlist();
                        } catch (Exception e) {
                            // TODO: handle exception
                        }
                    }
                });

                txt_productname = ((TextView) view.findViewById(R.id.prod_name_txt));
                txt_productname.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        Toast.makeText(getActivity(), "prod_name_txt", Toast.LENGTH_SHORT).show();

                        Intent i = new Intent(getActivity(), Activity_Product_Description.class);
                        i.putExtra("select_id", str_selected_product);
                        startActivity(i);
                    }
                });
            }


        });*/

        //Calling the getData method
        try {
            pDialog = new ProgressDialog(getActivity());
            pDialog.setMessage("Please wait...");
            pDialog.show();
            pDialog.setCancelable(false);
            queue = Volley.newRequestQueue(getActivity());
            Wishlist();
        } catch (Exception e) {
            // TODO: handle exception
        }


        // Inflate the layout for this fragment
        return rootView;
    }

    /**********************************
     * Get Wishlist Products
     ********************************/

    public void Wishlist() {

        System.out.println("called wishlist");

        StringRequest request = new StringRequest(Request.Method.POST,
                AppConfig.url_product_wishlist_list, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d(TAG, response.toString());

                try {
                    JSONObject obj = new JSONObject(response);
                    JSONObject respon = obj.getJSONObject("response");

                    int success = respon.getInt("success");

                    if (success == 1) {

                        JSONArray details_arr = respon.getJSONArray("details");

                        for (int j = 0; details_arr.length() > j; j++) {
                            JSONObject obj1 = details_arr.getJSONObject(j);

                            str_prod_name = obj1.getString(TAG_PROD_NAME);
                            str_prod_id = obj1.getString(TAG_PROD_ID);
                            str_prod_image = obj1.getString(TAG_PROD_IMAGE_URL);
                            str_prod_weight = obj1.getString(TAG_PROD_WEIGHT);
                            str_prod_price = obj1.getString(TAG_PROD_PRICE);
                            str_prod_gram = obj1.getString(TAG_PROD_GRAM_NAME);
                            str_prod_weight_type = obj1.getString(TAG_PROD_WEIGHT_TYPE);


                            HashMap<String, String> map = new HashMap<>();
                            map.put(TAG_PROD_ID, str_prod_id);
                            map.put(TAG_PROD_NAME, str_prod_name);
                            map.put(TAG_PROD_IMAGE_URL, str_prod_image);
                            map.put(TAG_PROD_WEIGHT, str_prod_weight);
                            map.put(TAG_PROD_PRICE, str_prod_price);
                            map.put(TAG_PROD_GRAM_NAME, str_prod_gram);
                            map.put(TAG_PROD_WEIGHT_TYPE, str_prod_weight_type);


                            prod_list_arr.add(map);

                        }

                        wishlistAdapter = new WishListAdapter(getActivity(), prod_list_arr);
                        wishlist_listview.setAdapter(wishlistAdapter);
                        wishlistAdapter.notifyDataSetChanged();

                    } else if (success == 0) {

                        System.out.println("INSIDE 0)");
                        Crouton.makeText(getActivity(),
                                "No Products in Wishlist",
                                Style.INFO)
                                .show();

                    }

                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                pDialog.hide();
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();

                params.put("userid", str_userid);
                System.out.println("str_userid " + str_userid);
                return params;
            }

        };

        // Adding request to request queue
        queue.add(request);
    }

    /**********************************
     * Remove Wishlist Products
     ********************************/

    private void RemoveWishlist() {

        String tag_json_obj = "json_obj_req";

        StringRequest request = new StringRequest(Request.Method.POST,
                AppConfig.url_product_wishlist_remove, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d(TAG, response.toString());
                try {
                    JSONObject obj = new JSONObject(response);
                    JSONObject respon = obj.getJSONObject("response");

                    int success = respon.getInt("Success");

                    if (success == 1) {
                        try {

                            Crouton.makeText(getActivity(),
                                    "Product Sucessfully Removed From Wishlist",
                                    Style.CONFIRM)
                                    .show();

                            try {
                                prod_list_arr.clear();
                                System.out.println("Cleared");
                                queue = Volley.newRequestQueue(getActivity());
                                Wishlist();
                            } catch (Exception e) {
                                // TODO: handle exception
                            }

                        } catch (Exception e) {
                            // TODO: handle exception
                        }

                    } else if (success == 0){

                        Crouton.makeText(getActivity(),
                                "Product Not Removed try Again",
                                Style.ALERT)
                                .show();

                    }
                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                pDialog.hide();
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();

                params.put("oproid", str_select_id);
                params.put("userid", str_userid);

                return params;
            }

        };

        // Adding request to request queue
        queue.add(request);
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
