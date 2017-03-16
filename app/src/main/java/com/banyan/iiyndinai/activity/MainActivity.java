package com.banyan.iiyndinai.activity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements FragmentDrawer.FragmentDrawerListener {

    private static String TAG = MainActivity.class.getSimpleName();

    private Toolbar mToolbar;
    private FragmentDrawer drawerFragment;
    private ProgressDialog pDialog;
    public static RequestQueue queue;

    // CART
    RelativeLayout notificationCount1, parent_batch;
    TextView tv;
    int i = 0;
    String str_count, str_name, str_id;
    String quantity;
    String str_status;

    // Tiny DB
    TinyDB tinydb;
    ArrayList<String> tiny_id;
    int cart_size;

    // Session Manager Class
    SessionManager session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        session = new SessionManager(getApplicationContext());

        str_status = "" + session.isLoggedIn();
        isInternetOn();

        if (str_status.equals("true")) {

            System.out.println("FALSE TRUE");

            session.checkLogin();

            // get user data from session
            HashMap<String, String> user = session.getUserDetails();

            // name
            str_name = user.get(SessionManager.KEY_USER);
            str_id = user.get(SessionManager.KEY_USER_ID);

            Toast.makeText(getApplicationContext(),str_id,Toast.LENGTH_SHORT);

            System.out.println("USER ID MAIN : " + str_id);
            System.out.println("USER NAME MAIN : " + str_name);

            SharedPreferences sharedPreferences = PreferenceManager
                    .getDefaultSharedPreferences(MainActivity.this);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("user_name", str_name); // username

        } else if (str_status.equals("false")) {

            System.out.println("FALSE STATUS");
            // tv.setText("0");

        }

        tinydb = new TinyDB(MainActivity.this);
        tiny_id = tinydb.getListString("Id");
        cart_size = tiny_id.size();

        //Calling the getData method
        try {
            queue = Volley.newRequestQueue(MainActivity.this);
            Function_GET_Count();
        } catch (Exception e) {
            // TODO: handle exception
        }

        mToolbar = (Toolbar) findViewById(R.id.toolbar);

        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        drawerFragment = (FragmentDrawer)
                getSupportFragmentManager().findFragmentById(R.id.fragment_navigation_drawer);
        drawerFragment.setUp(R.id.fragment_navigation_drawer, (DrawerLayout) findViewById(R.id.drawer_layout), mToolbar);
        drawerFragment.setDrawerListener(this);

        // display the first navigation drawer view on app launch
        displayView(0);

    }

    public final boolean isInternetOn() {

        // get Connectivity Manager object to check connection
        ConnectivityManager connec =
                (ConnectivityManager) getSystemService(getBaseContext().CONNECTIVITY_SERVICE);

        // Check for network connections
        if (connec.getNetworkInfo(0).getState() == NetworkInfo.State.CONNECTED ||
                connec.getNetworkInfo(0).getState() == NetworkInfo.State.CONNECTING ||
                connec.getNetworkInfo(1).getState() == NetworkInfo.State.CONNECTING ||
                connec.getNetworkInfo(1).getState() == NetworkInfo.State.CONNECTED) {

            // if connected with internet

            //Toast.makeText(this, " Connected ", Toast.LENGTH_LONG).show();
            return true;

        } else if (
                connec.getNetworkInfo(0).getState() == NetworkInfo.State.DISCONNECTED ||
                        connec.getNetworkInfo(1).getState() == NetworkInfo.State.DISCONNECTED) {

            new AlertDialog.Builder(MainActivity.this)
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
                                    finishAffinity();
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

            Intent in = new Intent(MainActivity.this, Activity_Search.class);
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

                    fragment = new HomeFragment();
                    title = getString(R.string.title_home);

                } catch (Exception e) {

                }

                break;
            case 1:

                try {

                    Intent hot = new Intent(MainActivity.this, Activity_HotDeals.class);
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

                    Intent inm = new Intent(MainActivity.this, Activity_WishList.class);
                    startActivity(inm);
                    overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
                    finish();

                } catch (Exception e) {

                }

                break;
            case 3:

                try {

                    Intent ine = new Intent(MainActivity.this, Activity_Enquiry.class);
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

                    Intent orders = new Intent(MainActivity.this, Activity_My_Order.class);
                    startActivity(orders);
                    overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
                    finish();

                } catch (Exception e) {

                }

                /*fragment = new EnquiryFragment();
                title = getString(R.string.title_enquiry);*/
                break;
            case 5:

                try {

                    Intent my_acc = new Intent(MainActivity.this, Activity_MyAccount.class);
                    startActivity(my_acc);
                    overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
                    finish();

                } catch (Exception e) {

                }

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

        @Override
        public void onBackPressed() {
            // your code.
            new AlertDialog.Builder(MainActivity.this)
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
                    JSONObject obj = null;
                    try {
                        obj = new JSONObject(response);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

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

                params.put("user_id", str_id);
                return params;
            }

        };

        // Adding request to request queue
        queue.add(request);
    }


}