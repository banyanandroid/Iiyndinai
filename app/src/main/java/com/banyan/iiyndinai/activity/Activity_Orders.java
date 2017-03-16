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
import com.banyan.iiyndinai.adapter.MyordersAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;


public class Activity_Orders extends AppCompatActivity implements FragmentDrawer.FragmentDrawerListener {

    private static String TAG = "Myorders";

    //Tag values to read from json
    public static final String TAG_PROD_ID = "product_id";
    public static final String TAG_PROD_IMAGE_URL = "product_image";
    public static final String TAG_PROD_NAME = "product_name";
    public static final String TAG_PROD_QUANTITY = "quantity";
    public static final String TAG_PROD_WEIGHT = "total_size_weight";
    public static final String TAG_PROD_PRICE = "price";

    String str_prod_name, str_prod_id, str_prod_image, str_prod_weight, str_prod_price, str_prod_quantity, str_prod_weight_type;

    String str_select_id, str_select_name, str_select_price, str_select_weight, str_select_img;

    private Toolbar mToolbar;
    private FragmentDrawer drawerFragment;
    private MyordersAdapter myordersAdapter;
    static ArrayList<HashMap<String, String>> prod_list_arr;

    // CART
    RelativeLayout notificationCount1,parent_batch;
    TextView tv;
    int i = 0;
    String str_count, str_name, str_userid;
    String quantity;


    ProgressDialog pDialog;
    public static RequestQueue queue;

    // Session Manager Class
    SessionManager session;
    String str_status;
    ListView myorder_list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_orders);
        isInternetOn();

        session = new SessionManager(getApplicationContext());
        myorder_list = (ListView) findViewById(R.id.myorder_list);
        prod_list_arr = new ArrayList<HashMap<String, String>>();

        str_status = "" + session.isLoggedIn();

        if (str_status.equals("true")) {

            System.out.println("FALSE TRUE");

            session.checkLogin();

            // get user data from session
            HashMap<String, String> user = session.getUserDetails();

            // name
            str_name = user.get(SessionManager.KEY_USER);
            str_userid = user.get(SessionManager.KEY_USER_ID);

            SharedPreferences sharedPreferences = PreferenceManager
                    .getDefaultSharedPreferences(Activity_Orders.this);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("user_name", str_name); // username

            //Calling the getData method
            try {
                queue = Volley.newRequestQueue(Activity_Orders.this);
                Function_GET_Count();
            } catch (Exception e) {
                // TODO: handle exception
            }


            try {
                pDialog = new ProgressDialog(Activity_Orders.this);
                pDialog.setMessage("Please wait...");
                pDialog.show();
                pDialog.setCancelable(false);
                queue = Volley.newRequestQueue(Activity_Orders.this);
                jsonMyorder();
            } catch (Exception e) {
                // TODO: handle exception
            }



        } else if (str_status.equals("false")) {

            System.out.println("FALSE STATUS");
            // tv.setText("0");
            new AlertDialog.Builder(Activity_Orders.this)
                    .setTitle("Iiyndinai")
                    .setMessage("Login to see your orders")
                    .setIcon(R.mipmap.ic_launcher)
                    .setNegativeButton("No", new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // TODO Auto-generated method stub
                            Intent main = new Intent(Activity_Orders.this, MainActivity.class);
                            startActivity(main);

                        }
                    })
                    .setPositiveButton("Yes",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog,
                                                    int which) {
                                    // TODO Auto-generated method stub
                                    Intent login = new Intent(Activity_Orders.this, Activity_Login.class);
                                    startActivity(login);
                                    finish();
                                    finishAffinity();
                                }
                            }).show();

        }


        mToolbar = (Toolbar) findViewById(R.id.toolbar);

        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

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

            new AlertDialog.Builder(Activity_Orders.this)
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
                                    Intent ins = new Intent(Activity_Orders.this, Activity_Orders.class);
                                    startActivity(ins);
                                }
                            }).show();
            Toast.makeText(this, " Not Connected ", Toast.LENGTH_LONG).show();
            return false;
        }
        return false;
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
        parent_batch = (RelativeLayout) MenuItemCompat.getActionView(item1);
        tv = (TextView) notificationCount1.findViewById(R.id.badge_notification_2);
        tv.setText("0");
        //str_cart = Integer.toString(count);
        //tv.setText("" + cart_size);

        parent_batch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent i = new Intent(getApplicationContext(), Activity_Cart1.class);
                startActivity(i);
            }
        });
        tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent i = new Intent(getApplicationContext(), Activity_Cart1.class);
                startActivity(i);
            }
        });

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.actionbar_cart) {

            Intent i = new Intent(getApplicationContext(), Activity_Cart.class);
            startActivity(i);

            return true;
        }

        if (id == R.id.actionbar_search) {

            Intent in = new Intent(Activity_Orders.this, Activity_Search.class);
            startActivity(in);
            return true;
        }
        return super.onOptionsItemSelected(item);

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

                    Intent hotmain = new Intent(Activity_Orders.this, MainActivity.class);
                    startActivity(hotmain);
                    overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
                    finish();

                } catch (Exception e) {

                }

                break;
            case 1:

                try {

                    Intent hot = new Intent(Activity_Orders.this, Activity_HotDeals.class);
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

                    Intent inm = new Intent(Activity_Orders.this, Activity_WishList.class);
                    startActivity(inm);
                    overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
                    finish();

                } catch (Exception e) {

                }

                break;
            case 3:

                try {

                    Intent ine = new Intent(Activity_Orders.this, Activity_Enquiry.class);
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

                    Intent notification = new Intent(Activity_Orders.this, Activity_Orders.class);
                    startActivity(notification);
                    overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
                    finish();

                } catch (Exception e) {

                }

                break;
            case 5:

                try {

                    Intent orders = new Intent(Activity_Orders.this, Activity_MyAccount.class);
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

                    } else {
                        tv.setText("0");
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

    public void jsonMyorder() {

        System.out.println("called wishlist");

        StringRequest request = new StringRequest(Request.Method.POST,
                AppConfig.url_myorder, new Response.Listener<String>() {

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
                            str_prod_quantity = obj1.getString(TAG_PROD_QUANTITY);


                            HashMap<String, String> map = new HashMap<>();
                            map.put(TAG_PROD_ID, str_prod_id);
                            map.put(TAG_PROD_NAME, str_prod_name);
                            map.put(TAG_PROD_IMAGE_URL, str_prod_image);
                            map.put(TAG_PROD_WEIGHT, str_prod_weight);
                            map.put(TAG_PROD_PRICE, str_prod_price);
                            map.put(TAG_PROD_QUANTITY, str_prod_quantity);


                            prod_list_arr.add(map);

                        }

                        myordersAdapter = new MyordersAdapter(Activity_Orders.this, prod_list_arr);
                        myorder_list.setAdapter(myordersAdapter);
                        myordersAdapter.notifyDataSetChanged();

                        //Calling the getData method
                        try {
                            queue = Volley.newRequestQueue(Activity_Orders.this);
                            Function_GET_Count();
                        } catch (Exception e) {
                            // TODO: handle exception
                        }

                    } else if (success == 0) {

                        System.out.println("INSIDE 0)");
                        Crouton.makeText(Activity_Orders.this,
                                "No Orders Found",
                                Style.ALERT)
                                .show();

                        try {
                            queue = Volley.newRequestQueue(Activity_Orders.this);
                            Function_GET_Count();
                        } catch (Exception e) {
                            // TODO: handle exception
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

                params.put("user_id", str_userid);
                System.out.println("str_userid " + str_userid);
                return params;
            }

        };

        // Adding request to request queue
        queue.add(request);
    }

    /*@Override
    public void onBackPressed() {
        // your code.
        new AlertDialog.Builder(Activity_HotDeals.this)
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
    }
*/

}