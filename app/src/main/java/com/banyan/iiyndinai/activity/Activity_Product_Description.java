package com.banyan.iiyndinai.activity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
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
import com.bumptech.glide.Glide;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;

/**
 * Created by Jo on 8/20/2016.
 */
public class Activity_Product_Description extends AppCompatActivity {

    //Tag values to read from json
    public static final String TAG = "ProductDeatails";
    public static final String TAG_PRODUCT_CODE = "product_code";
    public static final String TAG_PRODUCT_ID = "product_id";
    public static final String TAG_PRODUCT_NAME = "product_name";
    public static final String TAG_PRODUCT_IMAGE = "product_image";
    public static final String TAG_GRAM_NAME = "gram_name";
    public static final String TAG_WEIGHT_NAME = "Weight_name";
    public static final String TAG_PRICE = "price";
    public static final String TAG_GRAME_NAME = "grame_name";
    public static final String TAG_PRODUCT_DESCRIPTION = "product_description";

    String str_product_code, str_product_id, str_product_name, str_product_image, str_gram_name, str_weight_name, str_price, str_grame_name, str_des;
    String str_selected_id, str_select_opid, str_from;

    TextView txt_product_name, txt_product_code, txt_price, txt_description, txt_minus, txt_qty, txt_plus, txt_delivery_status;

    ImageView img_product_image,img_share_image;

    Button btn_addtocart, btn_pin_check;
    EditText edt_pincode;
    String str_pincode;

    private ProgressDialog pDialog;

    private Toolbar mToolbar;

    public static RequestQueue queue;

    // Session Manager Class
    SessionManager session;
    String str_status;

    String str_name, str_userid;

    String str_quantity;

    // Cart
    RelativeLayout notificationCount1;
    TextView tv;
    int i = 0;
    String str_cart;
    String quantity;

    int count = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_description);
        isInternetOn();

        mToolbar = (Toolbar) findViewById(R.id.toolbar);

        txt_product_name = (TextView) findViewById(R.id.proddes_txt_details);
        txt_product_code = (TextView) findViewById(R.id.proddes_txt_product_id);
        txt_price = (TextView) findViewById(R.id.proddes_txt_price);
        txt_description = (TextView) findViewById(R.id.proddes_txt_description);

        txt_minus = (TextView) findViewById(R.id.proddes_txt_minus);
        txt_qty = (TextView) findViewById(R.id.proddes_txt_count);
        txt_plus = (TextView) findViewById(R.id.proddes_txt_add);

        txt_delivery_status = (TextView) findViewById(R.id.proddes_txt_delivery_details);

        img_product_image = (ImageView) findViewById(R.id.proddes_img_image);

        btn_addtocart = (Button) findViewById(R.id.proddes_btn_add_cart);
        btn_pin_check = (Button) findViewById(R.id.proddes_btn_check);

        edt_pincode = (EditText) findViewById(R.id.proddes_edt_pincode);
        img_share_image = (ImageView) findViewById(R.id.proddes_img_share);

        img_share_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String txt_desc = txt_description.getText().toString();
                String prod_name = txt_product_name.getText().toString();

                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_TEXT, prod_name + "\n" +txt_desc);
                sendIntent.setType("text/plain");

                startActivity(sendIntent);
            }
        });

        setSupportActionBar(mToolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

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

        try {
            // Get a selected id from product
            Bundle extras = getIntent().getExtras();
            if (extras != null) {
                str_selected_id = extras.getString("select_id");
                str_select_opid = extras.getString("select_opid");
                str_from = extras.getString("from");
            }
        } catch (Exception e) {

        }

        mToolbar.setNavigationIcon(getResources().getDrawable(R.drawable.ic_action_back));
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("FROM Clicked");
                System.out.println("FROM : " + str_from);

               if (str_from.equalsIgnoreCase("wishlist")){

                   Intent i = new Intent(Activity_Product_Description.this, Activity_WishList.class);
                   startActivity(i);
                   finish();

               }else if (str_from.equalsIgnoreCase("product")){

                   Intent i = new Intent(Activity_Product_Description.this, Activity_Products.class);
                   startActivity(i);
                   finish();
               }

            }
        });


        try {
            pDialog = new ProgressDialog(Activity_Product_Description.this);
            pDialog.setMessage("Please wait...");
            pDialog.show();
            pDialog.setCancelable(false);
            queue = Volley.newRequestQueue(Activity_Product_Description.this);
            Function_ProductDes();
            Function_GET_Count();
        } catch (Exception e) {
            // TODO: handle exception
        }


        txt_minus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                i = Integer.parseInt(txt_qty.getText().toString());

                if (i <= 0) {


                } else {
                    i = i - 1;
                    txt_qty.setText("" + i);
                }

            }
        });

        txt_plus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                i = Integer.parseInt(txt_qty.getText().toString());

                if (i > 9) {


                } else {
                    i = i + 1;
                    txt_qty.setText("" + i);
                }

            }
        });

        btn_addtocart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                str_status = "" + session.isLoggedIn();

                str_quantity = txt_qty.getText().toString();

                if (str_status.equals("true")) {

                    if (str_quantity.equals("0")){
                        str_quantity = "1";
                    }else {
                        try {
                            pDialog = new ProgressDialog(Activity_Product_Description.this);
                            pDialog.setMessage("Please wait...");
                            pDialog.show();
                            pDialog.setCancelable(false);
                            queue = Volley.newRequestQueue(Activity_Product_Description.this);
                            AddtoCart();
                        } catch (Exception e) {
                            // TODO: handle exception
                        }
                    }



                } else if (str_status.equals("false")) {

                    new AlertDialog.Builder(Activity_Product_Description.this)
                            .setTitle("Iiyndinai")
                            .setMessage("You Should Login to Access this Feature")
                            .setIcon(R.mipmap.ic_launcher)
                            .setNegativeButton("Ok", new DialogInterface.OnClickListener() {

                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    // TODO Auto-generated method stub

                                    Intent i = new Intent(Activity_Product_Description.this, Activity_Login.class);
                                    startActivity(i);
                                    finish();

                                }
                            }).show();

                }


            }
        });

        btn_pin_check.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                str_pincode = edt_pincode.getText().toString();

                if (str_pincode.equals("")) {

                    edt_pincode.setError("Enter Pincode !");

                } else {

                    try {
                        pDialog = new ProgressDialog(Activity_Product_Description.this);
                        pDialog.setMessage("Please wait...");
                        pDialog.show();
                        pDialog.setCancelable(false);
                        queue = Volley.newRequestQueue(Activity_Product_Description.this);
                        txt_delivery_status.setVisibility(View.VISIBLE);
                        Function_GET_DeliveryStatus();
                    } catch (Exception e) {
                        // TODO: handle exception
                    }

                }

            }
        });

    }

    public final boolean isInternetOn() {

        // get Connectivity Manager object to check connection
        ConnectivityManager connec =
                (ConnectivityManager)getSystemService(getBaseContext().CONNECTIVITY_SERVICE);

        // Check for network connections
        if ( connec.getNetworkInfo(0).getState() == android.net.NetworkInfo.State.CONNECTED ||
                connec.getNetworkInfo(0).getState() == android.net.NetworkInfo.State.CONNECTING ||
                connec.getNetworkInfo(1).getState() == android.net.NetworkInfo.State.CONNECTING ||
                connec.getNetworkInfo(1).getState() == android.net.NetworkInfo.State.CONNECTED ) {

            // if connected with internet

            //Toast.makeText(this, " Connected ", Toast.LENGTH_LONG).show();
            return true;

        } else if (
                connec.getNetworkInfo(0).getState() == android.net.NetworkInfo.State.DISCONNECTED ||
                        connec.getNetworkInfo(1).getState() == android.net.NetworkInfo.State.DISCONNECTED  ) {

            new AlertDialog.Builder(Activity_Product_Description.this)
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
                                    Intent ins = new Intent(Activity_Product_Description.this, Activity_Product_Description.class);
                                    startActivity(ins);
                                }
                            }).show();
            Toast.makeText(this, " Not Connected ", Toast.LENGTH_LONG).show();
            return false;
        }
        return false;
    }

    private void Function_ProductDes() {


        StringRequest request = new StringRequest(Request.Method.POST,
                AppConfig.url_product_description, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d(TAG, response.toString());
                try {
                    JSONObject obj = new JSONObject(response);
                    JSONObject respon = obj.getJSONObject("response");
                    JSONArray details_arr = respon.getJSONArray("details");

                    if (details_arr.length() != 0) {

                        for (int j = 0; details_arr.length() > j; j++) {
                            JSONObject obj1 = details_arr.getJSONObject(j);

                            str_product_code = obj1.getString(TAG_PRODUCT_CODE);
                            str_product_id = obj1.getString(TAG_PRODUCT_ID);
                            str_product_name = obj1.getString(TAG_PRODUCT_NAME);
                            str_product_image = obj1.getString(TAG_PRODUCT_IMAGE);
                            str_gram_name = obj1.getString(TAG_GRAM_NAME);
                            str_weight_name = obj1.getString(TAG_WEIGHT_NAME);
                            str_price = obj1.getString(TAG_PRICE);
                            str_grame_name = obj1.getString(TAG_GRAME_NAME);
                            str_des = obj1.getString(TAG_PRODUCT_DESCRIPTION);

                            try {

                                txt_product_name.setText(str_product_name + " " + str_gram_name + " " + str_weight_name);
                                txt_product_code.setText("Product Code : " + str_product_code);
                                txt_price.setText("Price : " + str_price);

                                if (str_des.equals("null")) {

                                    txt_description.setText(" ");

                                } else {
                                    txt_description.setText(str_des);
                                }
                                if (str_product_image != null) {
                                    Glide.with(Activity_Product_Description.this).load(str_product_image).into(img_product_image);
                                } else {
                                    img_product_image.setVisibility(View.GONE);
                                }

                            } catch (Exception e) {

                            }
                        }

                    } else {

                        Toast.makeText(getApplicationContext(), "Sorry NO Description Available", Toast.LENGTH_SHORT).show();

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

                params.put("pid", str_selected_id);

                System.out.println("DES ID" + str_selected_id);

                return params;
            }

        };

        // Adding request to request queue
        queue.add(request);
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
                Log.d(TAG, response.toString());
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

                            System.out.println("Quantity : " + quantity);
                        }


                        pDialog.hide();

                    } else {
                        i = 0;
                        pDialog.hide();

                        tv.setText("0");


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

                params.put("user_id", str_userid);
                return params;
            }

        };

        // Adding request to request queue
        queue.add(request);
    }


    /**********************************
     * Insert a Products into Cart
     ********************************/

    public void AddtoCart() {

        StringRequest request = new StringRequest(Request.Method.POST,
                AppConfig.url_description_add_to_cart, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d(TAG, response.toString());

                try {
                    JSONObject obj = new JSONObject(response);
                    JSONObject respon = obj.getJSONObject("response");
                    int success = respon.getInt("success");

                    System.out.println("success : MSG : " + success);

                    if (success == 1) {

                        JSONArray detail_arr = respon.getJSONArray("details");

                        for (int i = 0; detail_arr.length() > i; i++) {
                            JSONObject obj1 = detail_arr.getJSONObject(i);

                            quantity = obj1.getString("quantity");
                            String total_price = obj1.getString("total_price");
                            tv.setText(quantity);
                            System.out.println("quantity" + quantity);
                        }

                        Crouton.makeText(Activity_Product_Description.this,
                                "Product Successfully Added into Cart",
                                Style.CONFIRM)
                                .show();


                    } else if (success == 0) {

                        Crouton.makeText(Activity_Product_Description.this,
                                "Products Not Added Into a Cart",
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

                params.put("id", str_select_opid);
                params.put("user_id", str_userid);
                params.put("quantity", str_quantity);

                System.out.println("id" + str_select_opid);
                System.out.println("user_id" + str_userid);
                System.out.println("quantity" + str_quantity);
                return params;
            }

        };

        // Adding request to request queue
        queue.add(request);
    }

    /***********************************
     * GET DELIVERY STATUS
     **********************************/

    private void Function_GET_DeliveryStatus() {

        StringRequest request = new StringRequest(Request.Method.POST,
                AppConfig.url_delivery_Status, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d(TAG, response.toString());
                try {
                    JSONObject obj = new JSONObject(response);
                    JSONObject respon = obj.getJSONObject("response");
                    int success = respon.getInt("success");

                    if (success == 1) {

                        txt_delivery_status.setText("Delivery Available");
                        txt_delivery_status.setTextColor(Color.parseColor("#4CAF50"));

                    } else {
                        i = 0;
                        txt_delivery_status.setText("Delivery Not Available");
                        txt_delivery_status.setTextColor(Color.parseColor("#F44336"));
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

                params.put("pincode", str_pincode);
                return params;
            }

        };

        // Adding request to request queue
        queue.add(request);
    }


    /**********************************
     * Main Menu
     *********************************/
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);

        MenuItem item1 = menu.findItem(R.id.actionbar_cart);
        MenuItemCompat.setActionView(item1, R.layout.notification_update_count_layout);
        notificationCount1 = (RelativeLayout) MenuItemCompat.getActionView(item1);
        tv = (TextView) notificationCount1.findViewById(R.id.badge_notification_2);


        notificationCount1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent i = new Intent(getApplicationContext(), Activity_Cart.class);
                startActivity(i);

            }
        });

        tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent i = new Intent(getApplicationContext(), Activity_Cart.class);
                startActivity(i);
            }
        });

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        return super.onOptionsItemSelected(item);
    }


    /*@Override
    public void onBackPressed() {
        // your code.
        new AlertDialog.Builder(Activity_Product_Description.this)
                .setTitle("Iiyndinai")
                .setMessage("Want to Exit ?")
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
                                finishAffinity();
                            }
                        }).show();
    }*/

}
