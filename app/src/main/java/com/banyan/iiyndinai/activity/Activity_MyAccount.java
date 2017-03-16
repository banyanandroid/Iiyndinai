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
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
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

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;


public class Activity_MyAccount extends AppCompatActivity implements FragmentDrawer.FragmentDrawerListener {

    // Session Manager Class
    SessionManager session;
    String str_status;
    String str_id;
    String str_selected_product, str_selected_opid;

    private FragmentDrawer drawerFragment;

    private ProgressBar mProgressBar;

    EditText edt_name, edt_mobilenumber, edt_city;
    EditText edt_address, edt_country, edt_state, edt_pincode, edt_landmark;
    String str_name,str_userid, str_mobile, str_city;
    String str_pincode, str_address, str_state, str_country, str_landmark, str_date;

    public static String TAG_NAME = "User_Name";
    public static String TAG_MOBILE = "User_Mobile";
    public static String TAG_PINCODE = "User_Zip";
    public static String TAG_ADDRESS = "User_Address";
    public static String TAG_COUNTRY = "User_Country";
    public static String TAG_STATE = "User_State";
    public static String TAG_CITY = "User_City";
    public static String TAG_DATE = "user_dt";
    public static String TAG_ID = "id";

    Button btn_clear, btn_update;

    private Toolbar mToolbar;
    // Cart
    RelativeLayout notificationCount1;
    TextView tv;
    int i = 0;
    String str_cart;
    String quantity;

    ProgressDialog pDialog;
    public static RequestQueue queue;

    String TAG = "reg";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_myaccount);

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

            System.out.println("USER ID MAIN : " + str_id);
            System.out.println("USER NAME MAIN : " + str_name);

            SharedPreferences sharedPreferences = PreferenceManager
                    .getDefaultSharedPreferences(Activity_MyAccount.this);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("user_name", str_name); // username


            try {
                pDialog = new ProgressDialog(Activity_MyAccount.this);
                pDialog.setMessage("Please wait...");
                pDialog.show();
                pDialog.setCancelable(false);
                queue = Volley.newRequestQueue(Activity_MyAccount.this);
                myAccountVolley();
            } catch (Exception e) {
                // TODO: handle exception
            }

        } else if (str_status.equals("false")) {

            Intent login = new Intent(Activity_MyAccount.this, Activity_Login.class);
            startActivity(login);

        }

        SharedPreferences sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(Activity_MyAccount.this);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("user_name", str_name); // username

        edt_name = (EditText) findViewById(R.id.reg_edt_name);
        edt_mobilenumber = (EditText) findViewById(R.id.reg_edt_mobilenumber);

        edt_city = (EditText) findViewById(R.id.reg_edt_city);

        edt_pincode = (EditText) findViewById(R.id.reg_edt_pincode);
        edt_address = (EditText) findViewById(R.id.reg_edt_address);

        edt_state = (EditText) findViewById(R.id.reg_edt_state);
        edt_country = (EditText) findViewById(R.id.reg_edt_country);
        edt_landmark = (EditText) findViewById(R.id.reg_edt_landmark);

        btn_clear = (Button) findViewById(R.id.reg_btn_clear);
        btn_update = (Button) findViewById(R.id.reg_btn_update);

        //Calling the getData method
        try {
            queue = Volley.newRequestQueue(Activity_MyAccount.this);
            Function_GET_Count();
        } catch (Exception e) {
            // TODO: handle exception
        }

        btn_update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                str_name = edt_name.getText().toString();
                str_mobile = edt_mobilenumber.getText().toString();
                str_city = edt_city.getText().toString();
                str_address = edt_address.getText().toString();
                str_pincode = edt_pincode.getText().toString();
                str_country = edt_country.getText().toString();
                str_state = edt_state.getText().toString().trim();
                str_landmark = edt_landmark.getText().toString().trim();

                str_date = new SimpleDateFormat("yyyy-MM-dd").format(new Date());

                if (str_name.equals("")) {
                    edt_name.setError("Enter User Name");
                } else if (str_mobile.equals("")) {
                    edt_mobilenumber.setError("Enter Mobile Number");
                } else if (str_mobile.length() != 10) {
                    edt_mobilenumber.setError("Enter valid Mobile Number");
                } else if (str_city.equals("")) {
                    edt_city.setError("Enter City");
                } else if (str_address.equals("")) {
                    edt_address.setError("Enter Address");
                } else if (str_country.equals("")) {
                    edt_country.setError("Enter Country");
                } else if (str_state.equals("")) {
                    edt_state.setError("Enter State");
                } else {
                    pDialog = new ProgressDialog(Activity_MyAccount.this);
                    pDialog.setMessage("Please wait...");
                    pDialog.show();
                    pDialog.setCancelable(false);
                    queue = Volley.newRequestQueue(Activity_MyAccount.this);
                    Function_Update();

                }
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

            new AlertDialog.Builder(Activity_MyAccount.this)
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
                                    Intent ins = new Intent(Activity_MyAccount.this, Activity_MyAccount.class);
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

            Intent in = new Intent(Activity_MyAccount.this, Activity_Search.class);
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

                    Intent hotmain = new Intent(Activity_MyAccount.this, MainActivity.class);
                    startActivity(hotmain);
                    overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
                    finish();

                } catch (Exception e) {

                }

                break;
            case 1:

                try {

                    Intent hot = new Intent(Activity_MyAccount.this, Activity_HotDeals.class);
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

                    Intent inm = new Intent(Activity_MyAccount.this, Activity_WishList.class);
                    startActivity(inm);
                    overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
                    finish();

                } catch (Exception e) {

                }

                break;
            case 3:

                try {

                    Intent ine = new Intent(Activity_MyAccount.this, Activity_Enquiry.class);
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

                    Intent orders = new Intent(Activity_MyAccount.this, Activity_My_Order.class);
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

                    Intent my_acc = new Intent(Activity_MyAccount.this, Activity_MyAccount.class);
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
                    String User_Country = obj2.getString(TAG_COUNTRY);
                    String User_Landmark = obj2.getString("User_Landmark");

                    edt_name.setText(User_Name);
                    edt_mobilenumber.setText(User_Mobile);
                    edt_city.setText(User_City);
                    edt_address.setText(User_Address);
                    edt_country.setText(User_Country);
                    edt_landmark.setText(User_Landmark);
                    edt_pincode.setText(User_Zip);
                    edt_state.setText(User_State);


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
                params.put("userid", str_id);

                System.out.println("userid" + str_id);
                return params;
            }

        };

        // Adding request to request queue
        queue.add(request);

    }

    private void Function_Update() {
        StringRequest request = new StringRequest(Request.Method.POST,
                AppConfig.url_update_myaccount, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d(TAG, response.toString());
                Log.d("USER_REGISTER", response.toString());
                try {
                    JSONObject obj = new JSONObject(response);
                    JSONObject respon = obj.getJSONObject("response");
                    int success = respon.getInt("success");

                    System.out.println("REG MSG" + success);

                    if (success == 1) {

                        i = 1;

                        try {
                            Update_Success();
                        } catch (Exception e) {

                        }


                    } else if (success == 2) {

                        Crouton.makeText(Activity_MyAccount.this,
                                "Email Already Registered !",
                                Style.ALERT)
                                .show();

                    } else {
                        i = 0;

                        Crouton.makeText(Activity_MyAccount.this,
                                "Register Failed Please Try Again",
                                Style.ALERT)
                                .show();

                        System.out.println("FAILED");
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

                params.put("user_id", str_id);
                params.put("user_name", str_name);
                params.put("user_mobile", str_mobile);
                params.put("user_address", str_address);
                params.put("user_city", str_city);
                params.put("user_state", str_state);
                params.put("user_zip", str_pincode);
                params.put("user_country", str_country);
                params.put("user_landmark", str_landmark);
                params.put("user_dt", str_date);


                System.out.println("name" + str_name);
                System.out.println("mobile" + str_mobile);
                System.out.println("address" + str_city);
                System.out.println("usertype" + str_address);
                System.out.println("str_state" + str_state);
                System.out.println("str_pincode" + str_pincode);
                System.out.println("str_country" + str_country);
                System.out.println("str_landmark" + str_landmark);
                System.out.println("str_date" + str_date);

                return params;
            }

        };

        // Adding request to request queue
        queue.add(request);
    }

    private void Update_Success() {
        new AlertDialog.Builder(Activity_MyAccount.this)
                .setTitle("Iiyndinai")
                .setMessage("User Update Successful.")
                .setIcon(R.mipmap.ic_launcher)
                .setPositiveButton("Done",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog,
                                                int which) {
                                // TODO Auto-generated method stub
                                Intent i = new Intent(Activity_MyAccount.this, Activity_MyAccount.class);
                                startActivity(i);
                                finish();

                            }
                        }).show();
    }

    /*@Override
    public void onBackPressed() {
        // your code.
        new AlertDialog.Builder(Activity_MyAccount.this)
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