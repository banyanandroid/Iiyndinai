package com.banyan.iiyndinai.activity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.banyan.iiyndinai.R;
import com.banyan.iiyndinai.adapter.CartAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;


public class Activity_Cart1 extends AppCompatActivity implements FragmentDrawer.FragmentDrawerListener {

    int cart_size, product_list;

    private Toolbar mToolbar;
    private FragmentDrawer drawerFragment;
    static ArrayList<HashMap<String, String>> cart_list_arr;
    String str_cart_update_id, str_cart_prod_name, str_cart_weight, str_cart_price, str_cart_image;

    int str_cart_status;
    String str_product_qty;
    String str_grant_total;

    String str_update_id;

    String TAG = "cart";
    public static final String TAG_CART_UPDATE_ID = "update_id";
    public static final String TAG_CART_SUB_TOTAL = "sub_total";
    public static final String TAG_CART_UNIT_PRICE = "unit_price";
    public static final String TAG_CART_QUANTITY = "quantity";
    public static final String TAG_CART_PRODUCT_CODE = "product_code";
    public static final String TAG_CART_PRODUCT_ID = "product_id";
    public static final String TAG_CART_PRODUCT_IMAGE = "product_image";
    public static final String TAG_CART_PRODUCT_NAME = "product_name";
    public static final String TAG_CART_GRAM_NAME = "gram_name";
    public static final String TAG_CART_WEIGHT_NAME = "Weight_name";
    public static final String TAG_CART_PRICE = "price";
    public static final String TAG_CART_GRAME_NAME = "grame_name";
    public static final String TAG_CART_PRODUCT_DESCRIPTION = "product_description";

    public static String TAG_NAME = "User_Name";
    public static String TAG_MOBILE = "User_Mobile";
    public static String TAG_PINCODE = "User_Zip";
    public static String TAG_ADDRESS = "User_Address";
    public static String TAG_EMAIL = "User_Email";
    public static String TAG_STATE = "User_State";
    public static String TAG_CITY = "User_City";

    static ArrayList<HashMap<String, String>> prod_list_arr;

    private CartAdapter cartAdapter;

    ListView cart_listview;
    TextView total_amt;
    TextView cart_value_txt;

    String str_prod_sub_total;

    Button btn_pay_now, cart_btn_cash_on_delivery;

    int total_amount;

    private ProgressDialog pDialog;

    // Cart
    RelativeLayout notificationCount1;
    TextView tv;
    int i = 0;
    Float unit_price = 0f, total = 0f, t_amt;
    String str_cart;
    String quantity;

    // Session Manager Class
    SessionManager session;
    String str_name, str_userid;
    String str_status;

    public static RequestQueue queue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);
        isInternetOn();

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        mToolbar.setNavigationIcon(getResources().getDrawable(R.drawable.ic_action_back));
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent i = new Intent(getApplicationContext(), Activity_Products.class);
                startActivity(i);
                finish();
            }
        });

        prod_list_arr = new ArrayList<HashMap<String, String>>();

        session = new SessionManager(getApplicationContext());

        str_status = "" + session.isLoggedIn();

        if (str_status.equals("true")) {

            System.out.println("FALSE TRUE");

            session.checkLogin();

            // get user data from session
            HashMap<String, String> user = session.getUserDetails();

            // name
            str_name = user.get(SessionManager.KEY_USER);
            str_userid = user.get(SessionManager.KEY_USER_ID);


        } else if (str_status.equals("false")) {

            System.out.println("FALSE STATUS");
            // tv.setText("0");

        }

        cart_listview = (ListView) findViewById(R.id.cart_listview);
        total_amt = (TextView) findViewById(R.id.cart_txt_total_amt);
        btn_pay_now = (Button) findViewById(R.id.cart_btn_checkout);
        cart_btn_cash_on_delivery = (Button) findViewById(R.id.cart_btn_cash_on_delivery);


        try {
            queue = Volley.newRequestQueue(Activity_Cart1.this);
            Function_GET_Count();
        } catch (Exception e) {
            // TODO: handle exception
        }

        cart_btn_cash_on_delivery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (str_status.equals("true")) {

                    SharedPreferences sharedPreferences = PreferenceManager
                            .getDefaultSharedPreferences(Activity_Cart1.this);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("user_name", str_name); // username

                    if (str_cart_status == 0) {

                        new AlertDialog.Builder(Activity_Cart1.this)
                                .setTitle("Iiyndinai")
                                .setMessage("No Products in cart")
                                .setIcon(R.mipmap.ic_launcher)
                                .setNegativeButton("Ok", new DialogInterface.OnClickListener() {

                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        // TODO Auto-generated method stub

                                    }
                                }).show();

                    } else if (str_cart_status == 1) {

                        str_grant_total = total_amt.getText().toString();

                        try {
                            pDialog = new ProgressDialog(Activity_Cart1.this);
                            pDialog.setMessage("Please wait...");
                            pDialog.show();
                            pDialog.setCancelable(false);
                            queue = Volley.newRequestQueue(Activity_Cart1.this);
                            Function_cash();
                        } catch (Exception e) {
                            // TODO: handle exception
                        }

                    }


                } else if (str_status.equals("false")) {

                    new AlertDialog.Builder(Activity_Cart1.this)
                            .setTitle("Iiyndinai")
                            .setMessage("You Should Login to Access this Feature")
                            .setIcon(R.mipmap.ic_launcher)
                            .setNegativeButton("Ok", new DialogInterface.OnClickListener() {

                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    // TODO Auto-generated method stub

                                    Intent i = new Intent(Activity_Cart1.this, Activity_Login.class);
                                    startActivity(i);
                                    finish();

                                }
                            }).show();

                }

            }
        });

        cart_listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    final int position, long id) {


                product_list = position;

                ImageView cart_plus_img = ((ImageView) view.findViewById(R.id.cart_add_img));
                ImageView cart_minus_img = ((ImageView) view.findViewById(R.id.cart_minus_img));
                ImageView cart_cancel_img = ((ImageView) view.findViewById(R.id.cart_cancel_img));
                final TextView cart_item_unitprice = (TextView) view.findViewById(R.id.cart_item_unitprice);
                final TextView cart_price_txt = (TextView) view.findViewById(R.id.cart_price_txt);
                cart_value_txt = ((TextView) view.findViewById(R.id.cart_value_txt));

                //cart_value_txt.setText("0");

                cart_cancel_img.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        str_cart_update_id = prod_list_arr.get(product_list).get(TAG_CART_UPDATE_ID);
                        str_cart_prod_name = prod_list_arr.get(product_list).get(TAG_CART_PRODUCT_NAME);

                        total = Float.valueOf((cart_price_txt.getText().toString()));
                        t_amt = Float.valueOf((total_amt.getText().toString()));

                        Float a = t_amt - total;
                        total_amt.setText("" + a);

                        Remove_Confirmation();

                    }
                });

                cart_plus_img.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        i = Integer.parseInt(cart_value_txt.getText().toString());
                        unit_price = Float.valueOf((cart_item_unitprice.getText().toString()));
                        total = Float.valueOf((cart_price_txt.getText().toString()));
                        t_amt = Float.valueOf((total_amt.getText().toString()));
                        str_cart_update_id = prod_list_arr.get(product_list).get(TAG_CART_UPDATE_ID);

                        if (i > 9) {


                        } else {
                            i = i + 1;
                            cart_value_txt.setText("" + i);
                            str_product_qty = cart_value_txt.getText().toString();

                            Float k = unit_price * i;
                            Float a = t_amt + unit_price;
                            cart_price_txt.setText("" + k);
                            total_amt.setText("" + a);

                            try {
                                pDialog = new ProgressDialog(Activity_Cart1.this);
                                pDialog.setMessage("Please wait...");
                                pDialog.show();
                                pDialog.setCancelable(false);
                                queue = Volley.newRequestQueue(Activity_Cart1.this);
                                UpdateFrom_Cart();
                            } catch (Exception e) {
                                // TODO: handle exception
                            }
                        }

                    }
                });

                cart_minus_img.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        i = Integer.parseInt(cart_value_txt.getText().toString());
                        unit_price = Float.valueOf((cart_item_unitprice.getText().toString()));
                        total = Float.valueOf((cart_price_txt.getText().toString()));
                        t_amt = Float.valueOf((total_amt.getText().toString()));
                        str_cart_update_id = prod_list_arr.get(product_list).get(TAG_CART_UPDATE_ID);

                        if (i <= 0) {


                        } else {
                            i = i - 1;
                            cart_value_txt.setText("" + i);
                            str_product_qty = cart_value_txt.getText().toString();

                            Float k = unit_price * i;
                            Float a = t_amt - unit_price;
                            cart_price_txt.setText("" + k);
                            total_amt.setText("" + a);

                            try {
                                pDialog = new ProgressDialog(Activity_Cart1.this);
                                pDialog.setMessage("Please wait...");
                                pDialog.show();
                                pDialog.setCancelable(false);
                                queue = Volley.newRequestQueue(Activity_Cart1.this);
                                UpdateFrom_Cart();
                            } catch (Exception e) {
                                // TODO: handle exception
                            }
                        }

                    }
                });


            }


        });

        btn_pay_now.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (str_status.equals("true")) {

                    if (str_cart_status == 1) {

                        str_grant_total = total_amt.getText().toString();

                        SharedPreferences sharedPreferences = PreferenceManager
                                .getDefaultSharedPreferences(Activity_Cart1.this);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString("totalamt", str_grant_total);
                        editor.commit();

                        try {
                            pDialog = new ProgressDialog(Activity_Cart1.this);
                            pDialog.setMessage("Please wait...");
                            pDialog.show();
                            pDialog.setCancelable(false);
                            queue = Volley.newRequestQueue(Activity_Cart1.this);
                            Function_Payment();
                        } catch (Exception e) {
                            // TODO: handle exception
                        }

                        try {
                            pDialog = new ProgressDialog(Activity_Cart1.this);
                            pDialog.setMessage("Please wait...");
                            pDialog.show();
                            pDialog.setCancelable(false);
                            queue = Volley.newRequestQueue(Activity_Cart1.this);
                            myAccountVolley();
                        } catch (Exception e) {
                            // TODO: handle exception
                        }

                        Intent intent = new Intent(Activity_Cart1.this, WebViewActivity.class);
                        intent.putExtra("amount", total_amt.getText().toString());
                        System.out.println("AMOUNT SEND" + total_amt.getText().toString());
                        startActivity(intent);

                    } else if (str_cart_status == 0) {

                        new AlertDialog.Builder(Activity_Cart1.this)
                                .setTitle("Iiyndinai")
                                .setMessage("No Products in cart")
                                .setIcon(R.mipmap.ic_launcher)
                                .setNegativeButton("Ok", new DialogInterface.OnClickListener() {

                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        // TODO Auto-generated method stub

                                    }
                                }).show();

                    }


                } else if (str_status.equals("false")) {

                    new AlertDialog.Builder(Activity_Cart1.this)
                            .setTitle("Iiyndinai")
                            .setMessage("You Should Login to Access this Feature")
                            .setIcon(R.mipmap.ic_launcher)
                            .setNegativeButton("Ok", new DialogInterface.OnClickListener() {

                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    // TODO Auto-generated method stub

                                    Intent i = new Intent(Activity_Cart1.this, Activity_Login.class);
                                    startActivity(i);
                                    finish();

                                }
                            }).show();

                }


            }
        });
        drawerFragment = (FragmentDrawer)
                getSupportFragmentManager().findFragmentById(R.id.fragment_navigation_drawer);
        drawerFragment.setUp(R.id.fragment_navigation_drawer, (DrawerLayout) findViewById(R.id.drawer_layout), mToolbar);
        drawerFragment.setDrawerListener(this);

    }

    public final boolean isInternetOn() {

        // get Connectivity Manager object to check connection
        ConnectivityManager connec =
                (ConnectivityManager) getSystemService(getBaseContext().CONNECTIVITY_SERVICE);

        // Check for network connections
        if (connec.getNetworkInfo(0).getState() == android.net.NetworkInfo.State.CONNECTED ||
                connec.getNetworkInfo(0).getState() == android.net.NetworkInfo.State.CONNECTING ||
                connec.getNetworkInfo(1).getState() == android.net.NetworkInfo.State.CONNECTING ||
                connec.getNetworkInfo(1).getState() == android.net.NetworkInfo.State.CONNECTED) {

            // if connected with internet

            //Toast.makeText(this, " Connected ", Toast.LENGTH_LONG).show();
            return true;

        } else if (
                connec.getNetworkInfo(0).getState() == android.net.NetworkInfo.State.DISCONNECTED ||
                        connec.getNetworkInfo(1).getState() == android.net.NetworkInfo.State.DISCONNECTED) {

            new AlertDialog.Builder(Activity_Cart1.this)
                    .setTitle("Iiyndinai")
                    .setMessage("!Oops no internet BuDdY")
                    .setIcon(R.mipmap.ic_launcher)
                    .setPositiveButton("Yes",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog,
                                                    int which) {
                                    // TODO Auto-generated method stub
                                    // finish();
                                    Intent ins = new Intent(Activity_Cart1.this, Activity_Cart1.class);
                                    startActivity(ins);
                                }
                            }).show();
            Toast.makeText(this, " Not Connected ", Toast.LENGTH_LONG).show();
            return false;
        }
        return false;
    }

    /***********************************
     * GET PRODUCTS COUNT
     **********************************/

    private void Function_GET_Count() {

        System.out.println("COUNT CALLED");
        StringRequest request = new StringRequest(Request.Method.POST,
                AppConfig.url_cart_count, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d(" ", response.toString());
                try {
                    JSONObject obj = new JSONObject(response);

                    int success = obj.getInt("success");

                    if (success == 1) {

                        i = 1;
                        JSONArray arr;
                        arr = obj.getJSONArray("Product_count");

                        for (int i = 0; arr.length() > i; i++) {
                            JSONObject obj1 = arr.getJSONObject(i);
                            quantity = obj1.getString("Name");
                            tv.setText(quantity);

                            try {
                                pDialog = new ProgressDialog(Activity_Cart1.this);
                                pDialog.setMessage("Please wait...");
                                pDialog.show();
                                System.out.println("CALLING");
                                pDialog.setCancelable(false);
                                queue = Volley.newRequestQueue(Activity_Cart1.this);
                                Get_Cart_Items();
                            } catch (Exception e) {
                                // TODO: handle exception
                            }

                            System.out.println("Quantity : " + quantity);
                        }

                    } else {
                        i = 0;
                        tv.setText("0");

                        try {
                            pDialog = new ProgressDialog(Activity_Cart1.this);
                            pDialog.setMessage("Please wait...");
                            pDialog.show();
                            pDialog.setCancelable(false);
                            queue = Volley.newRequestQueue(Activity_Cart1.this);
                            Get_Cart_Items();
                        } catch (Exception e) {
                            // TODO: handle exception
                        }

                        Crouton.makeText(Activity_Cart1.this,
                                "No Products In Cart!",
                                Style.ALERT)
                                .show();


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

                params.put("user_id", str_userid);
                return params;
            }

        };

        // Adding request to request queue
        queue.add(request);
    }

    /**********************************
     * Get Products by Show Cart Item
     ********************************/

    public void Get_Cart_Items() {

        System.out.println("CALLING 1");

        StringRequest request = new StringRequest(Request.Method.POST,
                AppConfig.url_cart_showlist, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d("CART", response.toString());

                try {
                    JSONObject obj = new JSONObject(response);
                    JSONObject respon = obj.getJSONObject("response");
                    int success = respon.getInt("success");

                    if (success == 1) {

                        JSONArray details_arr = respon.getJSONArray("details");

                        str_cart_status = 1;

                        if (details_arr.length() != 0) {

                            for (int j = 0; details_arr.length() > j; j++) {
                                JSONObject obj1 = details_arr.getJSONObject(j);

                                String str_prod_update_id = obj1.getString(TAG_CART_UPDATE_ID);
                                str_prod_sub_total = obj1.getString(TAG_CART_SUB_TOTAL);
                                String str_prod_unit_price = obj1.getString(TAG_CART_UNIT_PRICE);
                                String str_prod_quntity = obj1.getString(TAG_CART_QUANTITY);
                                String str_prod_product_code = obj1.getString(TAG_CART_PRODUCT_CODE);
                                String str_prod_product_id = obj1.getString(TAG_CART_PRODUCT_ID);
                                String str_prod_product_image = obj1.getString(TAG_CART_PRODUCT_IMAGE);
                                String str_prod_product_name = obj1.getString(TAG_CART_PRODUCT_NAME);
                                String str_prod_gram_name = obj1.getString(TAG_CART_GRAM_NAME);
                                String str_prod_weight_name = obj1.getString(TAG_CART_WEIGHT_NAME);
                                String str_prod_price = obj1.getString(TAG_CART_PRICE);
                                String str_prod_grame_name = obj1.getString(TAG_CART_GRAME_NAME);
                                String str_prod_product_description = obj1.getString(TAG_CART_PRODUCT_DESCRIPTION);


                                HashMap<String, String> map = new HashMap<>();
                                map.put(TAG_CART_UPDATE_ID, str_prod_update_id);
                                map.put(TAG_CART_SUB_TOTAL, str_prod_sub_total);
                                map.put(TAG_CART_UNIT_PRICE, str_prod_unit_price);
                                map.put(TAG_CART_QUANTITY, str_prod_quntity);
                                map.put(TAG_CART_PRODUCT_CODE, str_prod_product_code);
                                map.put(TAG_CART_PRODUCT_ID, str_prod_product_id);
                                map.put(TAG_CART_PRODUCT_IMAGE, str_prod_product_image);
                                map.put(TAG_CART_PRODUCT_NAME, str_prod_product_name);
                                map.put(TAG_CART_GRAM_NAME, str_prod_gram_name);
                                map.put(TAG_CART_WEIGHT_NAME, str_prod_weight_name);
                                map.put(TAG_CART_PRICE, str_prod_price);
                                map.put(TAG_CART_GRAME_NAME, str_prod_grame_name);
                                map.put(TAG_CART_PRODUCT_DESCRIPTION, str_prod_product_description);

                                int amt = Integer.parseInt(str_prod_sub_total);

                                total_amount += amt;

                                total_amt.setText("" + total_amount);

                                prod_list_arr.add(map);

                                System.out.println("TOTAL AMOUNT" + " : " + str_prod_sub_total);
                                System.out.println("TAG_CART_UNIT_PRICE" + " : " + str_prod_unit_price);
                                System.out.println("TAG_CART_UNIT_PRICE" + " : " + str_prod_unit_price);

                            }

                        } else {

                            Toast.makeText(getApplicationContext(), "Sorry NO Products Available", Toast.LENGTH_SHORT).show();

                        }
                        cartAdapter = new CartAdapter(Activity_Cart1.this, prod_list_arr);
                        cart_listview.setAdapter(cartAdapter);


                    } else if (success == 0) {

                        tv.setText("0");
                        str_cart_status = 0;

                        Crouton.makeText(Activity_Cart1.this,
                                "No Products in Cart",
                                Style.INFO)
                                .show();

                        cartAdapter = new CartAdapter(Activity_Cart1.this, prod_list_arr);
                        cart_listview.setAdapter(cartAdapter);

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

                params.put("user_id", str_userid);
                System.out.println("user_id " + str_userid);
                return params;
            }

        };

        // Adding request to request queue
        queue.add(request);
    }

    /**********************************
     * Get Products by Update Cart Item
     ********************************/

    public void UpdateFrom_Cart() {

        StringRequest request = new StringRequest(Request.Method.POST,
                AppConfig.url_update_cart, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d("CART", response.toString());

                try {
                    JSONObject obj = new JSONObject(response);
                    JSONObject respon = obj.getJSONObject("response");
                    int success = respon.getInt("success");

                    if (success == 1) {

                        Crouton.makeText(Activity_Cart1.this,
                                "Product Updated Successfully",
                                Style.CONFIRM)
                                .show();

                    } else if (success == 0) {

                        Crouton.makeText(Activity_Cart1.this,
                                "Internal Error Try After Sometime",
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

                //String str_qty = cart_value_txt.getText().toString();

                params.put("user_id", str_userid);
                params.put("val", str_product_qty);
                params.put("id", str_cart_update_id);


                System.out.println("user_id " + str_userid);
                System.out.println("val " + str_product_qty);
                System.out.println("id " + str_cart_update_id);
                return params;
            }

        };

        // Adding request to request queue
        queue.add(request);
    }


    // Function Remove Alert
    public void Remove_Confirmation() {

        new AlertDialog.Builder(Activity_Cart1.this)
                .setTitle("Iiyndinai")
                .setMessage("Want to Romve " + str_cart_prod_name + " From Your Cart?")
                .setIcon(R.mipmap.ic_launcher)
                .setNegativeButton("No", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // TODO Auto-generated method stub

                    }
                })
                .setPositiveButton("Yes",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog,
                                                int which) {
                                // TODO Auto-generated method stub
                                // finish();

                                try {
                                    pDialog = new ProgressDialog(Activity_Cart1.this);
                                    pDialog.setMessage("Please wait...");
                                    pDialog.show();
                                    pDialog.setCancelable(false);
                                    queue = Volley.newRequestQueue(Activity_Cart1.this);
                                    RemoveFrom_Cart();
                                } catch (Exception e) {
                                    // TODO: handle exception
                                }


                            }
                        }).show();
    }


    /**********************************
     * Get Products by Show Cart Item
     ********************************/

    public void RemoveFrom_Cart() {

        StringRequest request = new StringRequest(Request.Method.POST,
                AppConfig.url_cart_Remove_Cart_item, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d("CART", response.toString());

                try {
                    JSONObject obj = new JSONObject(response);
                    JSONObject respon = obj.getJSONObject("response");
                    int success = respon.getInt("success");

                    if (success == 1) {

                        Crouton.makeText(Activity_Cart1.this,
                                "Product Successfully Removed From Cart",
                                Style.CONFIRM)
                                .show();

                        total_amount = 0;

                        try {
                            prod_list_arr.clear();
                            queue = Volley.newRequestQueue(Activity_Cart1.this);
                            Function_GET_Count();

                        } catch (Exception e) {

                        }


                    } else if (success == 0) {

                        Crouton.makeText(Activity_Cart1.this,
                                "No Products in Cart",
                                Style.ALERT)
                                .show();

                        try {
                            prod_list_arr.clear();
                            queue = Volley.newRequestQueue(Activity_Cart1.this);
                            Function_GET_Count();

                        } catch (Exception e) {

                        }


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

                params.put("sid", str_cart_update_id);
                System.out.println("update id " + str_userid);
                return params;
            }

        };

        // Adding request to request queue
        queue.add(request);
    }

    private void Function_cash() {
        StringRequest request = new StringRequest(Request.Method.POST,
                AppConfig.url_status_offline, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d("Cash", response.toString());
                Log.d("STATUS ACTIVITY", response.toString());
                try {
                    JSONObject obj = new JSONObject(response);
                    JSONObject respon = obj.getJSONObject("response");
                    int success = respon.getInt("success");

                    System.out.println("Status Activity" + success);

                    if (success == 1) {

                        i = 1;

                        new AlertDialog.Builder(Activity_Cart1.this)
                                .setTitle("Iiyndinai")
                                .setMessage("Your Order has been Placed")
                                .setIcon(R.mipmap.ic_launcher)
                                .setPositiveButton("Done",
                                        new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog,
                                                                int which) {
                                                // TODO Auto-generated method stub
                                                // finish();
                                                Intent ins = new Intent(Activity_Cart1.this, MainActivity.class);
                                                startActivity(ins);
                                            }
                                        }).show();


                    } else {

                        new AlertDialog.Builder(Activity_Cart1.this)
                                .setTitle("Iiyndinai")
                                .setMessage("Internal Error Please Try Again !")
                                .setIcon(R.mipmap.ic_launcher)
                                .setPositiveButton("Done",
                                        new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog,
                                                                int which) {
                                                // TODO Auto-generated method stub
                                                // finish();
                                                Intent ins = new Intent(Activity_Cart1.this, MainActivity.class);
                                                startActivity(ins);
                                            }
                                        }).show();

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
                pDialog.hide();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();

                params.put("userid", str_userid);
                params.put("gtotal", str_grant_total);

                System.out.println("name" + str_userid);
                System.out.println("gtotal" + str_grant_total);

                return params;
            }

        };

        // Adding request to request queue
        queue.add(request);
    }

    private void Function_Payment() {
        StringRequest request = new StringRequest(Request.Method.POST,
                AppConfig.url_payment, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d(TAG, response.toString());
                Log.d("STATUS ACTIVITY", response.toString());
                try {
                    JSONObject obj = new JSONObject(response);
                    JSONObject respon = obj.getJSONObject("response");
                    int success = respon.getInt("success");
                    String order_id = respon.getString("order_id");
                    AppConfig.order_id = order_id;

                    System.out.println("Cart" + success);
                    System.out.println("order_id_Cart" + order_id);

                    if (success == 1) {

                        i = 1;

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
                pDialog.hide();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();

                params.put("userid", str_userid);
                params.put("gtotal", str_grant_total);

                System.out.println("name" + str_userid);
                System.out.println("gtotal" + str_grant_total);

                return params;
            }

        };

        // Adding request to request queue
        queue.add(request);
    }

    /**********************************
     * Get Account Details
     ********************************/

    public void myAccountVolley() {

        StringRequest request = new StringRequest(Request.Method.POST,
                AppConfig.url_edit_myaccount, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d(TAG, response.toString());
                Log.d("USER_REGISTER", response.toString());
                try {
                    JSONObject obj = new JSONObject(response);
                    JSONObject respon = obj.getJSONObject("response");

                    JSONArray detail = respon.getJSONArray("details");
                    JSONObject obj2 = detail.getJSONObject(0);

                    String User_Name = obj2.getString(TAG_NAME);
                    String User_Mobile = obj2.getString(TAG_MOBILE);
                    String User_Address = obj2.getString(TAG_ADDRESS);
                    String User_City = obj2.getString(TAG_CITY);
                    String User_State = obj2.getString(TAG_STATE);
                    String User_Zip = obj2.getString(TAG_PINCODE);
                    String User_Email = obj2.getString(TAG_EMAIL);


                    SharedPreferences sharedPreferences = PreferenceManager
                            .getDefaultSharedPreferences(Activity_Cart1.this);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("User_Name", User_Name);
                    editor.putString("User_Mobile", User_Mobile);
                    editor.putString("User_Address", User_Address);
                    editor.putString("User_City", User_City);
                    editor.putString("User_State", User_State);
                    editor.putString("User_Zip", User_Zip);
                    editor.putString("User_Email", User_Email);;
                    editor.commit();

                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                pDialog.hide();
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                pDialog.hide();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("userid", str_userid);

                System.out.println("userid" + str_userid);
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
        tv.setText("0");
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.actionbar_search) {

            Intent in = new Intent(Activity_Cart1.this, Activity_Search.class);
            startActivity(in);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        // your code.
        Intent i = new Intent(getApplicationContext(), Activity_Products.class);
        startActivity(i);
        finish();
    }

    @Override
    public void onDrawerItemSelected(View view, int position) {
        displayView(position);
    }

    private void displayView(int position) {
        Fragment fragment = null;
        Intent in = null;
        String title = getString(R.string.app_name);
        switch (position) {
            case 0:
                try {

                    Intent hotmain = new Intent(Activity_Cart1.this, MainActivity.class);
                    startActivity(hotmain);
                    overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
                    finish();

                } catch (Exception e) {

                }

                break;
            case 1:

                try {

                    Intent hot = new Intent(Activity_Cart1.this, Activity_HotDeals.class);
                    startActivity(hot);
                    //push from bottom to top
                    overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
                    finish();
                } catch (Exception e) {

                }

                /*fragment = new HotDealsFragment();
                title = getString(R.string.title_hotdeals);*/
                break;
            case 2:

                try {

                    Intent inm = new Intent(Activity_Cart1.this, Activity_WishList.class);
                    startActivity(inm);
                    overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
                    finish();

                } catch (Exception e) {

                }

                break;
            case 3:

                try {

                    Intent ine = new Intent(Activity_Cart1.this, Activity_Enquiry.class);
                    startActivity(ine);
                    overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
                    finish();


                } catch (Exception e) {

                }

                /*fragment = new EnquiryFragment();
                title = getString(R.string.title_enquiry);*/
                break;
            case 4:

                try {

                    Intent notification = new Intent(Activity_Cart1.this, Activity_My_Order.class);
                    startActivity(notification);
                    overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
                    finish();

                } catch (Exception e) {

                }

                break;
            case 5:

                try {

                    Intent orders = new Intent(Activity_Cart1.this, Activity_MyAccount.class);
                    startActivity(orders);
                    overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
                    finish();

                } catch (Exception e) {

                }

                /*fragment = new EnquiryFragment();
                title = getString(R.string.title_enquiry);*/
                break;

            case 6:
                try {
                    session.logoutUser();
                } catch (Exception e) {

                }
                break;

            default:
                break;
        }


        if (fragment != null) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.container_body, fragment);
            fragmentTransaction.commit();

            // set the toolbar title
            getSupportActionBar().setTitle(title);
        }
    }
}
