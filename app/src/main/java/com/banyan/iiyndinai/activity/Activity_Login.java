package com.banyan.iiyndinai.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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

import java.util.HashMap;
import java.util.Map;

import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;


public class Activity_Login extends Activity {

    private ProgressDialog pDialog;
    int i;
    private static final String TAG = "Login";
    private static final String TAG_NAME = "user_name";
    private static final String TAG_ID = "user_id";

    public static RequestQueue queue;

    TextView txt_title, txt_forgot_password, txt_register, txt_skip;
    String mobile;
    EditText edt_username, edt_password;
    EditText edt1;
    Button btn_login;
    String str_username, str_password = "";
    String str_name, str_user_id;

    // Session Manager Class
    SessionManager session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Session Manager
        session = new SessionManager(getApplicationContext());

        txt_forgot_password = (TextView) findViewById(R.id.linkForgotpsw);
        edt_username = (EditText) findViewById(R.id.editTextEmail);
        edt_password = (EditText) findViewById(R.id.editTextPassword);
        btn_login = (Button) findViewById(R.id.buttonLogin);
        txt_register = (TextView) findViewById(R.id.linkSignup);

        txt_skip = (TextView) findViewById(R.id.login_txt_skip);
        txt_skip.setVisibility(View.VISIBLE);


        // Toast.makeText(getApplicationContext(), "User Login Status: " + session.isLoggedIn(), Toast.LENGTH_LONG).show();

        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                str_username = edt_username.getText().toString();
                str_password = edt_password.getText().toString();

                if (str_username.equals("")) {
                    edt_username.setError("Enter Email");
                } else if (str_password.equals("")) {
                    edt_password.setError("Enter Password");
                } else {
                    try {
                        pDialog = new ProgressDialog(Activity_Login.this);
                        pDialog.setMessage("Please wait...");
                        pDialog.show();
                        pDialog.setCancelable(false);
                        queue = Volley.newRequestQueue(Activity_Login.this);
                        Function_Login();

                    } catch (Exception e) {
                        // TODO: handle exception
                    }
                }
            }
        });

        txt_skip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent i = new Intent(Activity_Login.this, MainActivity.class);
                startActivity(i);
                finish();

            }
        });

        /*txt_forgot_password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Forgotpassword();

            }
        });*/

        txt_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("REG Clickked");

                Intent reg = new Intent(getApplicationContext(), Activity_Register.class);
                startActivity(reg);
                finish();
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

            return true;

        } else if (
                connec.getNetworkInfo(0).getState() == android.net.NetworkInfo.State.DISCONNECTED ||
                        connec.getNetworkInfo(1).getState() == android.net.NetworkInfo.State.DISCONNECTED  ) {

            new AlertDialog.Builder(Activity_Login.this)
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
                                    Intent ins = new Intent(Activity_Login.this, Activity_Login.class);
                                    startActivity(ins);
                                }
                            }).show();
            Toast.makeText(this, " Not Connected ", Toast.LENGTH_LONG).show();
            return false;
        }
        return false;
    }


    private void Function_Login() {


        StringRequest request = new StringRequest(Request.Method.POST,
                AppConfig.url_login, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d(TAG, response.toString());
                try {
                    JSONObject obj = new JSONObject(response);
//                    JSONObject respon = obj.getJSONObject("response");

                    int success = obj.getInt("success");

                    if (success == 1) {

                        i = 1;
                        JSONArray arr;
                        arr = obj.getJSONArray("login_user");

                        for (int i = 0; arr.length() > i; i++) {
                            JSONObject obj1 = arr.getJSONObject(i);

                            str_name = obj1.getString(TAG_NAME);
                            str_user_id = obj1.getString(TAG_ID);
                        }
                        System.out.println(str_name);
                        System.out.println(str_user_id);

                        session.createLoginSession(str_name, str_user_id);

                        Intent i = new Intent(getApplicationContext(), MainActivity.class);
                        startActivity(i);
                        finish();


                    } else {
                        i = 0;

                        Crouton.makeText(Activity_Login.this,
                                "Login Failed Please Try Again",
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

                params.put("username", str_username);
                params.put("password", str_password);

                System.out.println("username" + str_username);
                System.out.println("password" + str_password);

                return params;
            }

        };

        // Adding request to request queue
        queue.add(request);
    }


    @Override
    public void onBackPressed() {
        // your code.
        new AlertDialog.Builder(Activity_Login.this)
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

