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
import com.banyan.iiyndinai.adapter.MyOrderListAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;


public class Activity_My_Order extends AppCompatActivity implements FragmentDrawer.FragmentDrawerListener {

    private static String TAG = "Myorders";

    //Tag values to read from json
    public static final String TAG_ORDER_ID = "order_id";
    public static final String TAG_ORDER_NUMBER = "order_number";
    public static final String TAG_TOTAL_AMOUNT = "total_amount";
    public static final String TAG_ORDER_DATE = "order_date";
    public static final String TAG_PROD_STATUS = "status";
    public static final String TAG_PROD_PAYMENT_TYPE = "payment_status";

    private Toolbar mToolbar;
    private FragmentDrawer drawerFragment;
    private MyOrderListAdapter myordersAdapter;
    static ArrayList<HashMap<String, String>> prod_list_arr;

    // CART
    RelativeLayout notificationCount1;
    TextView tv;
    int i = 0;
    String str_count, str_name, str_userid;
    String quantity;

    // Tiny DB

    TinyDB tinydb;
    ArrayList<String> tiny_id;
    int cart_size;

    ProgressDialog pDialog;
    public static RequestQueue queue;

    // Session Manager Class
    SessionManager session;
    String str_status;
    ListView myorder_list;

    String str_order_id, str_order_number, str_total_amount, str_order_date,str_order_status,str_order_payment_type;

    String str_selected_order_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_orders);
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


            //Calling the getData method
            try {
                queue = Volley.newRequestQueue(Activity_My_Order.this);
                Function_GET_Count();
            } catch (Exception e) {
                // TODO: handle exception
            }


            try {
                pDialog = new ProgressDialog(Activity_My_Order.this);
                pDialog.setMessage("Please wait...");
                pDialog.show();
                pDialog.setCancelable(false);
                queue = Volley.newRequestQueue(Activity_My_Order.this);
                jsonMyorder();
            } catch (Exception e) {
                // TODO: handle exception
            }


        } else if (str_status.equals("false")) {

            System.out.println("FALSE STATUS");
            // tv.setText("0");
            new AlertDialog.Builder(Activity_My_Order.this)
                    .setTitle("Iiyndinai")
                    .setMessage("Login to see your orders")
                    .setIcon(R.mipmap.ic_launcher)
                    .setNegativeButton("No", new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // TODO Auto-generated method stub
                            Intent main = new Intent(Activity_My_Order.this, MainActivity.class);
                            startActivity(main);

                        }
                    })
                    .setPositiveButton("Yes",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog,
                                                    int which) {
                                    // TODO Auto-generated method stub
                                    Intent login = new Intent(Activity_My_Order.this, Activity_Login.class);
                                    startActivity(login);
                                    finish();
                                    finishAffinity();
                                }
                            }).show();

        }

        myorder_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {

                str_selected_order_id = prod_list_arr.get(position).get(TAG_ORDER_ID);

                SharedPreferences sharedPreferences = PreferenceManager
                        .getDefaultSharedPreferences(Activity_My_Order.this);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("order_id", str_selected_order_id);
                editor.commit();

                Intent i = new Intent(Activity_My_Order.this, Activity_Orders_Detail.class);
                startActivity(i);
                finish();
            }


        });


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

            new AlertDialog.Builder(Activity_My_Order.this)
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
                                    Intent ins = new Intent(Activity_My_Order.this, Activity_My_Order.class);
                                    startActivity(ins);
                                }
                            }).show();
            Toast.makeText(this, " Not Connected ", Toast.LENGTH_LONG).show();
            return false;
        }
        return false;
    }

    /**************************************
     * Previous Orders List
     *************************************/

    public void jsonMyorder() {

        System.out.println("called wishlist");

        StringRequest request = new StringRequest(Request.Method.POST,
                AppConfig.url_order_list, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d(TAG, response.toString());

                try {
                    JSONObject obj = new JSONObject(response);
                    int success = obj.getInt("success");

                    if (success == 1) {

                        JSONArray arr;

                        arr = obj.getJSONArray("response");

                        for (int i = 0; arr.length() > i; i++) {
                            JSONObject obj1 = arr.getJSONObject(i);

                            str_order_id = obj1.getString(TAG_ORDER_ID);
                            str_order_number = obj1.getString(TAG_ORDER_NUMBER);
                            str_total_amount = obj1.getString(TAG_TOTAL_AMOUNT);
                            str_order_date = obj1.getString(TAG_ORDER_DATE);
                            str_order_status = obj1.getString(TAG_PROD_STATUS);
                            str_order_payment_type = obj1.getString(TAG_PROD_PAYMENT_TYPE);

                            HashMap<String, String> map = new HashMap<>();
                            map.put(TAG_ORDER_ID, str_order_id);
                            map.put(TAG_ORDER_NUMBER, str_order_number);
                            map.put(TAG_TOTAL_AMOUNT, str_total_amount);
                            map.put(TAG_ORDER_DATE, str_order_date);
                            map.put(TAG_PROD_STATUS, str_order_status);
                            map.put(TAG_PROD_PAYMENT_TYPE, str_order_payment_type);

                            prod_list_arr.add(map);

                        }
                        myordersAdapter = new MyOrderListAdapter(Activity_My_Order.this, prod_list_arr);
                        myorder_list.setAdapter(myordersAdapter);
                        myordersAdapter.notifyDataSetChanged();

                        //Calling the getData method
                        try {
                            queue = Volley.newRequestQueue(Activity_My_Order.this);
                            Function_GET_Count();
                        } catch (Exception e) {
                            // TODO: handle exception
                        }

                    } else if (success == 0) {

                        System.out.println("INSIDE 0)");
                        Crouton.makeText(Activity_My_Order.this,
                                "No Orders Found",
                                Style.ALERT)
                                .show();

                        try {
                            queue = Volley.newRequestQueue(Activity_My_Order.this);
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

                params.put("userid", str_userid);
                System.out.println("str_userid " + str_userid);
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
        tv.setText("0");

        notificationCount1.setOnClickListener(new View.OnClickListener() {
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

        if (id == R.id.actionbar_search) {

            Intent in = new Intent(Activity_My_Order.this, Activity_Search.class);
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

                    Intent hotmain = new Intent(Activity_My_Order.this, MainActivity.class);
                    startActivity(hotmain);
                    overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
                    finish();

                } catch (Exception e) {

                }

                break;
            case 1:

                try {

                    Intent hot = new Intent(Activity_My_Order.this, Activity_HotDeals.class);
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

                    Intent inm = new Intent(Activity_My_Order.this, Activity_WishList.class);
                    startActivity(inm);
                    overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
                    finish();

                } catch (Exception e) {

                }

                break;
            case 3:

                try {

                    Intent ine = new Intent(Activity_My_Order.this, Activity_Enquiry.class);
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

                    Intent notification = new Intent(Activity_My_Order.this, Activity_My_Order.class);
                    startActivity(notification);
                    overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
                    finish();

                } catch (Exception e) {

                }

                break;
            case 5:

                try {

                    Intent orders = new Intent(Activity_My_Order.this, Activity_MyAccount.class);
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