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
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
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
import com.banyan.iiyndinai.adapter.WishListAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;


public class Activity_WishList extends AppCompatActivity implements FragmentDrawer.FragmentDrawerListener {


    private Toolbar mToolbar;
    private FragmentDrawer drawerFragment;

    String str_count, str_id;

    private static final String TAG = "Wish List Activity";

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
    RelativeLayout notificationCount1,parent_batch;
    TextView tv;
    int i = 0;
    String str_cart;
    String quantity;

    int count = 0;
    int product_list;

    String str_name;
    String str_selected_product, str_selected_opid;

    TextView txt_productname, txt_remove_wish_list;
    ImageView img_add;

    public static RequestQueue queue;

    // Session Manager Class
    SessionManager session;
    String str_status;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wishlist);
        isInternetOn();

        prod_list_arr = new ArrayList<HashMap<String, String>>();

        session = new SessionManager(getApplicationContext());

        str_status = "" + session.isLoggedIn();

        if (str_status.equals("true")){

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
                    .getDefaultSharedPreferences(Activity_WishList.this);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("user_name", str_name); // username


            try {
                pDialog = new ProgressDialog(Activity_WishList.this);
                pDialog.setMessage("Please wait...");
                pDialog.show();
                pDialog.setCancelable(false);
                queue = Volley.newRequestQueue(Activity_WishList.this);
                Wishlist();
            } catch (Exception e) {
                // TODO: handle exception
            }

            //Calling the getData method
            try {
                queue = Volley.newRequestQueue(Activity_WishList.this);
                Function_GET_Count();
            } catch (Exception e) {
                // TODO: handle exception
            }

        }else if (str_status.equals("false")){

            System.out.println("FALSE STATUS");
            // tv.setText("0");

        }


        wishlist_listview = (ListView) findViewById(R.id.wishlist_listview);


        mToolbar = (Toolbar) findViewById(R.id.toolbar);

        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        drawerFragment = (FragmentDrawer)
                getSupportFragmentManager().findFragmentById(R.id.fragment_navigation_drawer);
        drawerFragment.setUp(R.id.fragment_navigation_drawer, (DrawerLayout) findViewById(R.id.drawer_layout), mToolbar);
        drawerFragment.setDrawerListener(Activity_WishList.this);

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
                            pDialog = new ProgressDialog(Activity_WishList.this);
                            pDialog.setMessage("Please wait...");
                            pDialog.show();
                            pDialog.setCancelable(false);
                            queue = Volley.newRequestQueue(Activity_WishList.this);
                            RemoveWishlist();
                        } catch (Exception e) {
                            // TODO: handle exception
                        }
                    }
                });

                txt_productname.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        str_select_id = prod_list_arr.get(product_list).get(TAG_PROD_ID);



                        try {

                            System.out.println("Encypt ID " + str_selected_product);

                            byte[] data = str_select_id.getBytes("UTF-8");
                            String newstr = Base64.encodeToString(data, Base64.DEFAULT);

                            System.out.println("Passed : " + newstr);
                            System.out.println("Passed no : " + str_selected_product);

                            Intent i = new Intent(Activity_WishList.this, Activity_Product_Description.class);
                            i.putExtra("select_id", newstr);
                            i.putExtra("select_opid", str_selected_product);
                            i.putExtra("from", "wishlist");
                            startActivity(i);
                        } catch (Exception e) {
                            // TODO: handle exception
                        }

                    }
                });

                img_add.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        System.out.println("Clicked");
                        str_select_id = prod_list_arr.get(product_list).get(TAG_PROD_ID);

                        try {
                            pDialog = new ProgressDialog(Activity_WishList.this);
                            pDialog.setMessage("Please wait...");
                            pDialog.show();
                            pDialog.setCancelable(false);
                            queue = Volley.newRequestQueue(Activity_WishList.this);
                            AddtoCart();
                        } catch (Exception e) {
                            // TODO: handle exception
                        }


                    }
                });

            }
        });



        // display the first navigation drawer view on app launch
        //displayView(2);
    }

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

            Intent in = new Intent(Activity_WishList.this, Activity_Search.class);
            startActivity(in);
            return true;
        }
        return super.onOptionsItemSelected(item);
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

            new AlertDialog.Builder(Activity_WishList.this)
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
                                    Intent ins = new Intent(Activity_WishList.this, Activity_WishList.class);
                                    startActivity(ins);
                                }
                            }).show();
            Toast.makeText(this, " Not Connected ", Toast.LENGTH_LONG).show();
            return false;
        }
        return false;
    }

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

                        wishlistAdapter = new WishListAdapter(Activity_WishList.this, prod_list_arr);
                        wishlist_listview.setAdapter(wishlistAdapter);
                        wishlistAdapter.notifyDataSetChanged();

                        //Calling the getData method
                        try {
                            queue = Volley.newRequestQueue(Activity_WishList.this);
                            Function_GET_Count();
                        } catch (Exception e) {
                            // TODO: handle exception
                        }

                    } else if (success == 0) {

                        System.out.println("INSIDE 0)");
                        Crouton.makeText(Activity_WishList.this,
                                "No Products in Wishlist",
                                Style.INFO)
                                .show();
                        wishlistAdapter = new WishListAdapter(Activity_WishList.this, prod_list_arr);
                        wishlist_listview.setAdapter(wishlistAdapter);
                        wishlistAdapter.notifyDataSetChanged();

                        try {
                            queue = Volley.newRequestQueue(Activity_WishList.this);
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

                params.put("userid", str_id);
                System.out.println("str_userid " + str_id);
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


    private void RemoveWishlist() {


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

                            Crouton.makeText(Activity_WishList.this,
                                    "Product Sucessfully Removed From Wishlist",
                                    Style.CONFIRM)
                                    .show();

                            try {
                                prod_list_arr.clear();
                                System.out.println("Cleared");
                                queue = Volley.newRequestQueue(Activity_WishList.this);
                                Wishlist();
                            } catch (Exception e) {
                                // TODO: handle exception
                            }

                        } catch (Exception e) {
                            // TODO: handle exception
                        }

                    } else if (success == 0) {

                        Crouton.makeText(Activity_WishList.this,
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
                params.put("userid", str_id);

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

        System.out.println("Inside");

        StringRequest request = new StringRequest(Request.Method.POST,
                AppConfig.url_product_add_to_cart, new Response.Listener<String>() {

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

                        }


                    } else if (success == 0) {

                        Crouton.makeText(Activity_WishList.this,
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

                params.put("id", str_select_id);
                params.put("user_id", str_id);

                System.out.println("product_id" + str_select_id);
                System.out.println("user_id" + str_id);
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

                    Intent hotmain = new Intent(Activity_WishList.this, MainActivity.class);
                    startActivity(hotmain);
                    overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
                    finish();

                } catch (Exception e) {

                }

                break;
            case 1:

                try {

                    Intent hot = new Intent(Activity_WishList.this, Activity_HotDeals.class);
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

                    Intent inm = new Intent(Activity_WishList.this, Activity_WishList.class);
                    startActivity(inm);
                    overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
                    finish();

                } catch (Exception e) {

                }

                break;
            case 3:

                try {

                    Intent ine = new Intent(Activity_WishList.this, Activity_Enquiry.class);
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

                    Intent orders = new Intent(Activity_WishList.this, Activity_My_Order.class);
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

                    Intent my_acc = new Intent(Activity_WishList.this, Activity_MyAccount.class);
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

    /*@Override
    public void onBackPressed() {
        // your code.
        new AlertDialog.Builder(Activity_WishList.this)
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