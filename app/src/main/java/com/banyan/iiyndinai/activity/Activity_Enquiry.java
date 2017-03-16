package com.banyan.iiyndinai.activity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Bundle;
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
import android.widget.Button;
import android.widget.EditText;
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

import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;


public class Activity_Enquiry extends AppCompatActivity implements FragmentDrawer.FragmentDrawerListener {

    private Toolbar mToolbar;
    private FragmentDrawer drawerFragment;

    // CART
    RelativeLayout notificationCount1;
    TextView tv;
    int i = 0;
    String str_count, str_id;
    String quantity;

    // Tiny DB

    TinyDB tinydb;
    ArrayList<String> tiny_id;
    int cart_size;

    EditText edt_name, edt_email, edt_mobile, edt_city, edt_address, edt_message;
    String str_name, str_email, str_mobile, str_city, str_address, str_msg;

    Button btn_submit, btn_clear;

    ProgressDialog pDialog;
    public static RequestQueue queue;

    String TAG = "enquiry";

    // Session Manager Class
    SessionManager session;
    String str_status;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enquiry);
        isInternetOn();

        session = new SessionManager(getApplicationContext());

        str_status = "" + session.isLoggedIn();

        if (str_status.equals("true")) {

            System.out.println("FALSE TRUE");

            session.checkLogin();

            // get user data from session
            HashMap<String, String> user = session.getUserDetails();

            // name
            str_name = user.get(SessionManager.KEY_USER);
            str_id = user.get(SessionManager.KEY_USER_ID);


        } else if (str_status.equals("false")) {

            System.out.println("FALSE STATUS");
            // tv.setText("0");

        }

        tinydb = new TinyDB(Activity_Enquiry.this);
        tiny_id = tinydb.getListString("Id");
        cart_size = tiny_id.size();


        //Calling the getData method
        try {
            queue = Volley.newRequestQueue(Activity_Enquiry.this);
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

        edt_name = (EditText) findViewById(R.id.enq_edt_name);
        edt_email = (EditText) findViewById(R.id.enq_edt_email);
        edt_mobile = (EditText) findViewById(R.id.enq_edt_mobile);
        edt_city = (EditText) findViewById(R.id.enq_edt_city);
        edt_address = (EditText) findViewById(R.id.enq_edt_address);
        edt_message = (EditText) findViewById(R.id.enq_edt_message);

        btn_clear = (Button) findViewById(R.id.enq_btn_clear);
        btn_submit = (Button) findViewById(R.id.enq_btn_register);


        btn_clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                edt_name.setText("");
                edt_email.setText("");
                edt_mobile.setText("");
                edt_city.setText("");
                edt_address.setText("");
                edt_message.setText("");
            }
        });

        btn_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                str_name = edt_name.getText().toString();
                str_email = edt_email.getText().toString();
                str_mobile = edt_mobile.getText().toString();
                str_city = edt_city.getText().toString();
                str_address = edt_address.getText().toString();
                str_msg = edt_message.getText().toString();

                String email = edt_email.getText().toString().trim();
                String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";

                if (str_name.equals("")) {
                    edt_name.setError("Enter User Name Here");
                } else if (str_email.equals("")) {
                    edt_email.setError("Enter Email Here");
                } else if (str_mobile.length() != 10) {
                    edt_mobile.setError("Enter valid Mobile Number");
                } else if (str_city.equals("")) {
                    edt_city.setError("Enter City Here");
                } else if (str_address.equals("")) {
                    edt_address.setError("Enter Address Here");
                } else if (str_msg.equals("")) {
                    edt_email.setError("Enter Message Here");
                } else if (str_status.equals("false")) {
                    Toast.makeText(getApplicationContext(), "You Should Login to Access this", Toast.LENGTH_SHORT).show();
                }else {
                    pDialog = new ProgressDialog(Activity_Enquiry.this);
                    pDialog.setMessage("Please wait...");
                    pDialog.show();
                    pDialog.setCancelable(false);
                    queue = Volley.newRequestQueue(Activity_Enquiry.this);
                    Function_Send_Enquiry();
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

            new AlertDialog.Builder(Activity_Enquiry.this)
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
                                    Intent ins = new Intent(Activity_Enquiry.this, Activity_Enquiry.class);
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
                        i = 0;
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

    /***********************************
     * GET Send Enquiry
     **********************************/

    private void Function_Send_Enquiry() {
        StringRequest request = new StringRequest(Request.Method.POST,
                AppConfig.url_product_send_enquiry, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d(TAG, response.toString());
                try {
                    JSONObject obj = new JSONObject(response);
                    int success = obj.getInt("success");

                    if (success == 1) {

                        Crouton.makeText(Activity_Enquiry.this,
                                "Enquiry Successfully",
                                Style.CONFIRM)
                                .show();

                        edt_name.setText("");
                        edt_email.setText("");
                        edt_mobile.setText("");
                        edt_city.setText("");
                        edt_address.setText("");
                        edt_message.setText("");

                    } else {
                        Crouton.makeText(Activity_Enquiry.this,
                                "Enquiry Send Failed Please Try Again",
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
                pDialog.hide();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();

                params.put("name", str_name);
                params.put("email", str_email);
                params.put("mobile", str_mobile);
                params.put("city", str_city);
                params.put("address", str_address);
                params.put("message", str_msg);

                System.out.println("name" + str_name);
                System.out.println("email" + str_email);
                System.out.println("mobile" + str_mobile);
                System.out.println("city" + str_city);
                System.out.println("address" + str_address);
                System.out.println("message" + str_msg);

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

        if(id == R.id.actionbar_search){

            Intent in = new Intent(Activity_Enquiry.this, Activity_Search.class);
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

                    Intent hotmain = new Intent(Activity_Enquiry.this, MainActivity.class);
                    startActivity(hotmain);
                    overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
                    finish();

                } catch (Exception e) {

                }

                break;
            case 1:

                try {

                    Intent hot = new Intent(Activity_Enquiry.this, Activity_HotDeals.class);
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

                    Intent inm = new Intent(Activity_Enquiry.this, Activity_WishList.class);
                    startActivity(inm);
                    overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
                    finish();

                } catch (Exception e) {

                }

                break;
            case 3:

                try {

                    Intent ine = new Intent(Activity_Enquiry.this, Activity_Enquiry.class);
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

                    Intent orders = new Intent(Activity_Enquiry.this, Activity_My_Order.class);
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

                    Intent my_acc = new Intent(Activity_Enquiry.this, Activity_MyAccount.class);
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


   /* @Override
    public void onBackPressed() {
        // your code.
        new AlertDialog.Builder(Activity_Enquiry.this)
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