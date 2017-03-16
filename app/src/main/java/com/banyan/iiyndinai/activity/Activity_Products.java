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

import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;

public class Activity_Products extends AppCompatActivity implements FragmentDrawer.FragmentDrawerListener  {

    //Tag values to read from json
    public static final String TAG = "ProductList";
    public static final String TAG_PROD_OPID = "product_opid";
    public static final String TAG_PROD_IMAGE_URL = "product_image";
    public static final String TAG_PROD_NAME = "product_name";
    public static final String TAG_PROD_ID = "product_id";
    public static final String TAG_PROD_WEIGHT = "Weight_name";
    public static final String TAG_PROD_PRICE = "price";
    public static final String TAG_PROD_GRAM_NAME = "gram_name";
    public static final String TAG_PROD_GRAME_NAME = "grame_name";

    public static final String TAG_Sub_NAME = "sub_category";
    public static final String TAG_Sub_ID = "subcategory_list";

    String str_prod_name, str_prod_opid, str_prod_id, str_prod_image, str_prod_weight, str_prod_price, str_prod_gram, str_prod_grame_name;

    String str_select_id, str_select_opid, str_select_name, str_select_price, str_select_weight, str_select_img;

    private ProductAdapter productAdapter;
    static ArrayList<HashMap<String, String>> prod_list_arr;
    static ArrayList<HashMap<String, String>> sub_category;

    List<String> sub_cat;
    LinearLayout lin_sub_prod;
    ListView prod_listview;
    Spinner spinner;
    private ProgressBar mProgressBar;
    private Toolbar mToolbar;
    private FragmentDrawer drawerFragment;
    private ProgressDialog pDialog;

    // Cart
    RelativeLayout notificationCount1;
    TextView tv;
    int i = 0;
    String str_cart;
    String quantity;
    String str_cid;

    int count = 0;
    int product_list;

    String str_sid;
    String str_selected_product, str_selected_opid;
    int spinner_position, spinner_count;

    String str_name, str_userid;

    // Tiny DB

    TinyDB tinydb;
    ArrayList<String> tiny_id;
    int cart_size;

    // Session Manager Class
    SessionManager session;
    String str_status;

    public static RequestQueue queue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_products);
        isInternetOn();

        prod_list_arr = new ArrayList<HashMap<String, String>>();
        sub_category = new ArrayList<HashMap<String, String>>();
        sub_cat = new ArrayList<String>();
        sub_cat.add("Select Category");

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

        tinydb = new TinyDB(Activity_Products.this);
        tiny_id = tinydb.getListString("Id");
        cart_size = tiny_id.size();

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        lin_sub_prod = (LinearLayout) findViewById(R.id.lin_sub_prod);
        prod_listview = (ListView) findViewById(R.id.product_listview);

        spinner = (Spinner) findViewById(R.id.spinner);

        setSupportActionBar(mToolbar);

        getSupportActionBar().setDisplayShowHomeEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        /*mToolbar.setNavigationIcon(getResources().getDrawable(R.drawable.ic_action_back));
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent i = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(i);
                finish();
            }
        });
*/
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                spinner_position = i;
                spinner_count = i - 1;

                if (spinner_count < 0) {

                } else {
                    str_sid = sub_category.get(spinner_count).get(TAG_Sub_ID);
                    try {
                        pDialog = new ProgressDialog(Activity_Products.this);
                        pDialog.setMessage("Please wait...");
                        pDialog.show();
                        pDialog.setCancelable(false);
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

        SharedPreferences sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(Activity_Products.this);

        str_cid = sharedPreferences.getString("cid", "cid");


        //Calling the getData method
        try {
            pDialog = new ProgressDialog(Activity_Products.this);
            pDialog.setMessage("Please wait...");
            pDialog.show();
            pDialog.setCancelable(false);
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

                str_selected_product = prod_list_arr.get(product_list).get(TAG_PROD_ID);

                ImageView img = ((ImageView) view.findViewById(R.id.prod_plus_img));
                img.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        str_select_id = prod_list_arr.get(product_list).get(TAG_PROD_ID);
                        str_select_opid = prod_list_arr.get(product_list).get(TAG_PROD_OPID);
                        str_select_name = prod_list_arr.get(product_list).get(TAG_PROD_NAME);
                        str_select_weight = prod_list_arr.get(product_list).get(TAG_PROD_WEIGHT);
                        str_select_price = prod_list_arr.get(product_list).get(TAG_PROD_PRICE);
                        str_select_img = prod_list_arr.get(product_list).get(TAG_PROD_IMAGE_URL);

                        str_status = "" + session.isLoggedIn();

                        if (str_status.equals("true")) {

                            try {
                                pDialog = new ProgressDialog(Activity_Products.this);
                                pDialog.setMessage("Please wait...");
                                pDialog.show();
                                pDialog.setCancelable(false);
                                queue = Volley.newRequestQueue(Activity_Products.this);
                                AddtoCart();
                            } catch (Exception e) {
                                // TODO: handle exception
                            }

                        } else if (str_status.equals("false")) {

                            new AlertDialog.Builder(Activity_Products.this)
                                    .setTitle("Iiyndinai")
                                    .setMessage("You Should Login to Access this Feature")
                                    .setIcon(R.mipmap.ic_launcher)
                                    .setNegativeButton("Ok", new DialogInterface.OnClickListener() {

                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            // TODO Auto-generated method stub

                                            Intent i = new Intent(Activity_Products.this, Activity_Login.class);
                                            startActivity(i);
                                            finish();

                                        }
                                    }).show();
                        }

                    }
                });


                ImageView txt_add_wish_list = ((ImageView) view.findViewById(R.id.product_img_wishlist));
                txt_add_wish_list.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        str_selected_opid = prod_list_arr.get(product_list).get(TAG_PROD_OPID);

                        str_status = "" + session.isLoggedIn();

                        if (str_status.equals("true")) {


                            try {
                                pDialog = new ProgressDialog(Activity_Products.this);
                                pDialog.setMessage("Please wait...");
                                pDialog.show();
                                pDialog.setCancelable(false);
                                queue = Volley.newRequestQueue(Activity_Products.this);
                                AddWishlist();
                            } catch (Exception e) {
                                // TODO: handle exception
                            }

                        } else if (str_status.equals("false")) {

                            new AlertDialog.Builder(Activity_Products.this)
                                    .setTitle("Iiyndinai")
                                    .setMessage("You Should Login to Access this Feature")
                                    .setIcon(R.mipmap.ic_launcher)
                                    .setNegativeButton("Ok", new DialogInterface.OnClickListener() {

                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            // TODO Auto-generated method stub

                                            Intent i = new Intent(Activity_Products.this, Activity_Login.class);
                                            startActivity(i);
                                            finish();

                                        }
                                    }).show();

                        }

                    }
                });

                TextView txt_productname = ((TextView) view.findViewById(R.id.prod_name_txt));
                txt_productname.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        str_select_opid = prod_list_arr.get(product_list).get(TAG_PROD_OPID);

                        Intent i = new Intent(Activity_Products.this, Activity_Product_Description.class);
                        i.putExtra("select_id", str_selected_product);
                        i.putExtra("select_opid", str_select_opid);
                        i.putExtra("from", "product");
                        startActivity(i);
                        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
                    }
                });

                TextView txt_productprice = ((TextView) view.findViewById(R.id.prod_price_txt));

                txt_productprice.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        str_select_opid = prod_list_arr.get(product_list).get(TAG_PROD_OPID);

                        Intent i = new Intent(Activity_Products.this, Activity_Product_Description.class);
                        i.putExtra("select_id", str_selected_product);
                        i.putExtra("select_opid", str_select_opid);
                        i.putExtra("from", "product");
                        startActivity(i);
                        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
                    }
                });

                ImageView img_product_img = ((ImageView) view.findViewById(R.id.prod_img));

                img_product_img.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        str_select_opid = prod_list_arr.get(product_list).get(TAG_PROD_OPID);

                        Intent i = new Intent(Activity_Products.this, Activity_Product_Description.class);
                        i.putExtra("select_id", str_selected_product);
                        i.putExtra("select_opid", str_select_opid);
                        i.putExtra("from", "product");
                        startActivity(i);
                        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
                    }
                });
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

            new AlertDialog.Builder(Activity_Products.this)
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
                                    Intent ins = new Intent(Activity_Products.this, Activity_Products.class);
                                    startActivity(ins);
                                }
                            }).show();
            Toast.makeText(this, " Not Connected ", Toast.LENGTH_LONG).show();
            return false;
        }
        return false;
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

                    Intent hotmain = new Intent(Activity_Products.this, MainActivity.class);
                    startActivity(hotmain);
                    overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
                    finish();

                } catch (Exception e) {

                }

                break;
            case 1:

                try {

                    Intent hot = new Intent(Activity_Products.this, Activity_HotDeals.class);
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

                    Intent inm = new Intent(Activity_Products.this, Activity_WishList.class);
                    startActivity(inm);
                    overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
                    finish();

                } catch (Exception e) {

                }

                break;
            case 3:

                try {

                    Intent ine = new Intent(Activity_Products.this, Activity_Enquiry.class);
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

                    Intent notification = new Intent(Activity_Products.this, Activity_My_Order.class);
                    startActivity(notification);
                    overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
                    finish();

                } catch (Exception e) {

                }

                break;
            case 5:

                try {

                    Intent orders = new Intent(Activity_Products.this, Activity_MyAccount.class);
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

    /**************************************
     * Get Products by Category
     ***********************************/

    public void getDataCid() {

        StringRequest request = new StringRequest(Request.Method.POST,
                AppConfig.url_products, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d(TAG, response.toString());

                try {
                    JSONObject obj = new JSONObject(response);
                    JSONObject respon = obj.getJSONObject("response");
                    int success = respon.getInt("success");

                    if (success == 1) {

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
                                str_prod_opid = obj1.getString(TAG_PROD_OPID);
                                str_prod_image = obj1.getString(TAG_PROD_IMAGE_URL);
                                str_prod_weight = obj1.getString(TAG_PROD_WEIGHT);
                                str_prod_price = obj1.getString(TAG_PROD_PRICE);
                                str_prod_gram = obj1.getString(TAG_PROD_GRAM_NAME);
                                str_prod_grame_name = obj1.getString(TAG_PROD_GRAME_NAME);


                                HashMap<String, String> map = new HashMap<>();
                                map.put(TAG_PROD_ID, str_prod_id);
                                map.put(TAG_PROD_OPID, str_prod_opid);
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
                                str_prod_opid = obj1.getString(TAG_PROD_OPID);
                                str_prod_image = obj1.getString(TAG_PROD_IMAGE_URL);
                                str_prod_weight = obj1.getString(TAG_PROD_WEIGHT);
                                str_prod_price = obj1.getString(TAG_PROD_PRICE);
                                str_prod_gram = obj1.getString(TAG_PROD_GRAM_NAME);
                                str_prod_grame_name = obj1.getString(TAG_PROD_GRAME_NAME);


                                HashMap<String, String> map = new HashMap<>();
                                map.put(TAG_PROD_ID, str_prod_id);
                                map.put(TAG_PROD_OPID, str_prod_opid);
                                map.put(TAG_PROD_NAME, str_prod_name);
                                map.put(TAG_PROD_IMAGE_URL, str_prod_image);
                                map.put(TAG_PROD_WEIGHT, str_prod_weight);
                                map.put(TAG_PROD_PRICE, str_prod_price);
                                map.put(TAG_PROD_GRAM_NAME, str_prod_gram);
                                map.put(TAG_PROD_GRAME_NAME, str_prod_grame_name);

                                System.out.println("image url" + str_prod_image);


                                prod_list_arr.add(map);


                            }

                        } else {

                            lin_sub_prod.setVisibility(View.GONE);
                            Toast.makeText(getApplicationContext(), "Sorry NO Products Available", Toast.LENGTH_SHORT).show();

                        }

                        productAdapter = new ProductAdapter(Activity_Products.this, prod_list_arr);
                        prod_listview.setAdapter(productAdapter);

                        try {
                            queue = Volley.newRequestQueue(Activity_Products.this);
                            Function_GET_Count();
                        } catch (Exception e) {
                            // TODO: handle exception
                        }


                    } else if (success == 0) {

                        Crouton.makeText(Activity_Products.this,
                                "No Products in this Category",
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

                params.put("cid", str_cid);
                System.out.println("CID " + str_cid);
                return params;
            }

        };

        // Adding request to request queue
        queue.add(request);
    }

    /**********************************
     * Get Products by Sub Category
     ********************************/

    public void getDataAid() {

        StringRequest request = new StringRequest(Request.Method.POST,
                AppConfig.url_products, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d(TAG, response.toString());

                try {
                    JSONObject obj = new JSONObject(response);
                    JSONObject respon = obj.getJSONObject("response");
                    int success = respon.getInt("success");

                    if (success == 1) {

                        JSONArray details_arr = respon.getJSONArray("details");


                        if (details_arr.length() != 0) {

                            for (int j = 0; details_arr.length() > j; j++) {
                                JSONObject obj1 = details_arr.getJSONObject(j);

                                str_prod_name = obj1.getString(TAG_PROD_NAME);
                                str_prod_id = obj1.getString(TAG_PROD_ID);
                                str_prod_opid = obj1.getString(TAG_PROD_OPID);
                                str_prod_image = obj1.getString(TAG_PROD_IMAGE_URL);
                                str_prod_weight = obj1.getString(TAG_PROD_WEIGHT);
                                str_prod_price = obj1.getString(TAG_PROD_PRICE);
                                str_prod_gram = obj1.getString(TAG_PROD_GRAM_NAME);
                                str_prod_grame_name = obj1.getString(TAG_PROD_GRAME_NAME);


                                HashMap<String, String> map = new HashMap<>();
                                map.put(TAG_PROD_ID, str_prod_id);
                                map.put(TAG_PROD_OPID, str_prod_opid);
                                map.put(TAG_PROD_NAME, str_prod_name);
                                map.put(TAG_PROD_IMAGE_URL, str_prod_image);
                                map.put(TAG_PROD_WEIGHT, str_prod_weight);
                                map.put(TAG_PROD_PRICE, str_prod_price);
                                map.put(TAG_PROD_GRAM_NAME, str_prod_gram);
                                map.put(TAG_PROD_GRAME_NAME, str_prod_grame_name);


                                prod_list_arr.add(map);

                            }

                        } else {

                            Toast.makeText(getApplicationContext(), "Sorry NO Products Available", Toast.LENGTH_SHORT).show();

                        }
                        productAdapter = new ProductAdapter(Activity_Products.this, prod_list_arr);
                        prod_listview.setAdapter(productAdapter);

                    } else if (success == 0) {

                        Crouton.makeText(Activity_Products.this,
                                "No Products in this Category",
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

                params.put("sid", str_sid);
                System.out.println("SID " + str_sid);
                return params;
            }

        };

        // Adding request to request queue
        queue.add(request);
    }

    /**********************************
     * Insert a Products into wishlist
     ********************************/

    private void AddWishlist() {

        String tag_json_obj = "json_obj_req";

        StringRequest request = new StringRequest(Request.Method.POST,
                AppConfig.url_product_add_wishlist, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d(TAG, response.toString());
                try {
                    JSONObject obj = new JSONObject(response);
                    JSONObject respon = obj.getJSONObject("response");

                    System.out.println("RES" + respon);

                    int success = respon.getInt("Success");

                    if (success == 1) {
                        try {

                            Crouton.makeText(Activity_Products.this,
                                    "Product Sucessfully Added into Wishlist",
                                    Style.CONFIRM)
                                    .show();

                        } catch (Exception e) {
                            // TODO: handle exception
                        }

                    } else if (success == 2) {
                        Crouton.makeText(Activity_Products.this,
                                "Product Already Added into Wishlist",
                                Style.INFO)
                                .show();
                    } else {

                        Crouton.makeText(Activity_Products.this,
                                "Internal Error !",
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

                params.put("oproid", str_selected_opid);
                params.put("userid", str_userid);

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

                            Crouton.makeText(Activity_Products.this,
                                    "Product Sucessfully Added into Cart",
                                    Style.CONFIRM)
                                    .show();
                        }


                    } else if (success == 0) {

                        Crouton.makeText(Activity_Products.this,
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

                System.out.println("id" + str_select_opid);
                System.out.println("user_id" + str_userid);
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

        if(id == R.id.actionbar_search){

            Intent in = new Intent(Activity_Products.this, Activity_Search.class);
            startActivity(in);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /*@Override
    public void onBackPressed() {
        // your code.
        new AlertDialog.Builder(Activity_Products.this)
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

    /*// Method to manually check connection status
    private void checkConnection() {
        boolean isConnected = ConnectivityReceiver.isConnected();
        showSnack(isConnected);
    }

    // Showing the status in Snackbar
    private void showSnack(boolean isConnected) {
        String message;
        int color;
        if (isConnected) {
            message = "Good! Connected to Internet";
            Toast.makeText(getApplicationContext(),message,Toast.LENGTH_SHORT).show();
            color = Color.WHITE;
        } else {
            message = "Sorry! Not connected to internet";
            Toast.makeText(getApplicationContext(),message,Toast.LENGTH_SHORT).show();
            Log.d("Internet",message);
            color = Color.RED;
        }

    }

    @Override
    protected void onResume() {
        super.onResume();

        // register connection status listener
        MyApplication.getInstance().setConnectivityListener(Activity_Products.this);
    }

    *//**
     * Callback will be triggered when there is change in
     * network connection
     *//*
    @Override
    public void onNetworkConnectionChanged(boolean isConnected) {
        showSnack(isConnected);
    }*/

}
