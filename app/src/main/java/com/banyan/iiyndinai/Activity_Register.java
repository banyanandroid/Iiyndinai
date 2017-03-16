package com.banyan.iiyndinai;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.banyan.iiyndinai.activity.Activity_Login;
import com.banyan.iiyndinai.activity.Activity_Products;
import com.google.android.gcm.GCMRegistrar;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;

public class Activity_Register extends AppCompatActivity {

    EditText edt_name, edt_mobilenumber, edt_email, edt_city, edt_password, edt_retypepassword;
    EditText edt_address, edt_country, edt_state, edt_pincode, edt_landmark;
    String str_name, str_mobile, str_email, str_city, str_password, str_retype_password;
    String str_pincode, str_address, str_state, str_country, str_landmark, str_date;
    int i;

    public static String TAG_NAME = "user_name";
    public static String TAG_EMAIL = "user_email";
    public static String TAG_MOBILE = "user_mobile";
    public static String TAG_PASS = "user_pass";
    public static String TAG_PINCODE = "user_zip";
    public static String TAG_ADDRESS = "user_address";
    public static String TAG_COUNTRY = "user_country";
    public static String TAG_STATE = "user_state";
    public static String TAG_CITY = "user_city";
    public static String TAG_DATE = "user_dt";

    static String url_register = "http://iiyndinai.com/android/user_registration.php";

    Button btn_clear, btn_register, reg_btn_login;

    private Toolbar mToolbar;

    ProgressDialog pDialog;
    public static RequestQueue queue;

    String TAG = "reg";

    String regid = null;
    String GcmId = null;

    static final String SENDER_ID = "756178858073";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        edt_name = (EditText) findViewById(R.id.reg_edt_name);
        edt_mobilenumber = (EditText) findViewById(R.id.reg_edt_mobilenumber);
        edt_email = (EditText) findViewById(R.id.reg_edt_email);
        edt_city = (EditText) findViewById(R.id.reg_edt_city);
        edt_password = (EditText) findViewById(R.id.reg_edt_password);
        edt_retypepassword = (EditText) findViewById(R.id.reg_edt_re_password);

        edt_pincode = (EditText) findViewById(R.id.reg_edt_pincode);
        edt_address = (EditText) findViewById(R.id.reg_edt_address);

        edt_state = (EditText) findViewById(R.id.reg_edt_state);
        edt_country = (EditText) findViewById(R.id.reg_edt_country);
        edt_landmark = (EditText) findViewById(R.id.reg_edt_landmark);

        btn_clear = (Button) findViewById(R.id.reg_btn_clear);
        btn_register = (Button) findViewById(R.id.reg_btn_register);
        reg_btn_login = (Button) findViewById(R.id.reg_btn_login);

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

        btn_clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                edt_name.setText("");
                edt_mobilenumber.setText("");
                edt_email.setText("");
                edt_city.setText("");
                edt_password.setText("");
                edt_retypepassword.setText("");
                edt_email.setText("");
                edt_address.setText("");
                edt_country.setText("");
                edt_state.setText("");

                edt_pincode.setText("");
                edt_landmark.setText("");

            }
        });

        reg_btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Activity_Register.this, Activity_Login.class);
                startActivity(intent);
            }
        });

        btn_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                str_name = edt_name.getText().toString();
                str_mobile = edt_mobilenumber.getText().toString();
                str_email = edt_email.getText().toString();
                str_city = edt_city.getText().toString();
                str_password = edt_password.getText().toString();
                str_retype_password = edt_retypepassword.getText().toString();
                str_address = edt_address.getText().toString();
                str_pincode = edt_pincode.getText().toString();
                str_country = edt_country.getText().toString();
                str_state = edt_state.getText().toString().trim();
                str_landmark = edt_landmark.getText().toString().trim();

                String email = edt_email.getText().toString().trim();
                String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";

                str_date = new SimpleDateFormat("yyyy-MM-dd").format(new Date());

                if (str_name.equals("")) {
                    edt_name.setError("Enter User Name");
                } else if (str_mobile.equals("")) {
                    edt_mobilenumber.setError("Enter Mobile Number");
                } else if (str_mobile.length() != 10) {
                    edt_mobilenumber.setError("Enter valid Mobile Number");
                } else if (str_email.equals("")) {
                    edt_email.setError("Enter Email");
                } else if (!email.matches(emailPattern)) {
                    edt_email.setError("Enter Valid email address");
                } else if (str_city.equals("")) {
                    edt_city.setError("Enter City");
                } else if (str_address.equals("")) {
                    edt_address.setError("Enter Address");
                } else if (str_country.equals("")) {
                    edt_country.setError("Enter Country");
                } else if (str_state.equals("")) {
                    edt_state.setError("Enter State");
                } else if (str_password.equals("")) {
                    edt_password.setError("Enter Password");
                } else if (str_retype_password.equals("")) {
                    edt_retypepassword.setError("Enter Re-Password");
                } else if ((str_password.length() < 4) || (str_retype_password.length() < 4)) {
                    Crouton.makeText(Activity_Register.this,
                            "Enter Minimum 4 digit password",
                            Style.INFO)
                            .show();
                } else if (!(str_password.equals(str_retype_password))) {
                    Crouton.makeText(Activity_Register.this,
                            "Password not matched",
                            Style.INFO)
                            .show();
                } else {
                    Get_GCMID();
                }

            }
        });


    }

    private void Get_GCMID() {

        GcmId = GCMRegistrar.getRegistrationId(Activity_Register.this);

        if (GcmId.isEmpty()) {

            System.out.println("IaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaD Empty");
            GCMRegistrar.register(Activity_Register.this, SENDER_ID);
            // System.out.println("IaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaD Empty"+GCMIntentService.regid);

            GcmId = GCMRegistrar.getRegistrationId(this);
            System.out.println("IaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaD Empty" + GcmId);
            Crouton.makeText(Activity_Register.this,
                    "Internal ERROR try Again !",
                    Style.INFO)
                    .show();

        } else {
            Toast.makeText(getApplicationContext(), GcmId, Toast.LENGTH_LONG)
                    .show();
            System.out.println("neeeeeeeeeeeeeeeeeeeeeeeeeeeeewwwwwww" + GcmId);

            pDialog = new ProgressDialog(Activity_Register.this);
            pDialog.setMessage("Please wait...");
            pDialog.show();
            pDialog.setCancelable(false);
            queue = Volley.newRequestQueue(Activity_Register.this);
            Function_Register();

            if (GCMRegistrar.isRegisteredOnServer(Activity_Register.this)) {
                // Skips registration.

            }
        }
    }

    private void Function_Register() {
        StringRequest request = new StringRequest(Request.Method.POST,
                url_register, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d(TAG, response.toString());
                Log.d("USER_REGISTER", response.toString());
                try {
                    JSONObject obj = new JSONObject(response);
                    JSONObject respon = obj.getJSONObject("response");
                    int success = respon.getInt("success");

                    System.out.println("REG" + success);

                    if (success == 1) {

                        i = 1;

                        try {
                            Reg_Success();
                        } catch (Exception e) {

                        }

                    }else if (success == 2){

                        Crouton.makeText(Activity_Register.this,
                                "Email Already Registered !",
                                Style.ALERT)
                                .show();

                    }else {
                        i = 0;
                        Crouton.makeText(Activity_Register.this,
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

                params.put("user_name", str_name);
                params.put("user_email", str_email);
                params.put("user_pass", str_password);
                params.put("user_mobile", str_mobile);
                params.put("user_phone", str_mobile);
                params.put("user_address", str_address);
                params.put("user_city", str_city);
                params.put("user_state", str_state);
                params.put("user_zip", str_pincode);
                params.put("user_country", str_country);
                params.put("user_landmark", str_landmark);
                params.put("user_dt", str_date);
                params.put("gcm", GcmId);


                System.out.println("name" + str_name);
                System.out.println("mobile" + str_mobile);
                System.out.println("email" + str_email);
                System.out.println("address" + str_city);
                System.out.println("usertype" + str_address);
                System.out.println("password" + str_password);
                System.out.println("str_state" + str_state);
                System.out.println("str_pincode" + str_pincode);
                System.out.println("str_country" + str_country);
                System.out.println("str_landmark" + str_landmark);
                System.out.println("str_date" + str_date);
                System.out.println("gcm" + GcmId);

                return params;
            }

        };

        // Adding request to request queue
        queue.add(request);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        hidePDialog();
    }

    private void hidePDialog() {
        if (pDialog != null) {
            pDialog.dismiss();
            pDialog = null;
        }
    }

    private void Reg_Success() {
        new android.support.v7.app.AlertDialog.Builder(Activity_Register.this)
                .setTitle("Iiyndinai")
                .setMessage("User Registration Successful.")
                .setIcon(R.mipmap.ic_launcher)
                .setPositiveButton("Done",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog,
                                                int which) {
                                // TODO Auto-generated method stub

                                Intent i = new Intent(Activity_Register.this, Activity_Login.class);
                                startActivity(i);
                                finish();

                            }
                        }).show();
    }

    @Override
    public void onBackPressed() {
        // your code.
        new android.support.v7.app.AlertDialog.Builder(Activity_Register.this)
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


}
