package com.banyan.iiyndinai.activity;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
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

    public static RequestQueue queue;

    TextView txt_title, txt_forgot_password, txt_register;
    String mobile;
    EditText edt_username, edt_password;
    EditText edt1;
    Button btn_login;
    String str_username, str_password = "";


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


    private void Function_Login() {


        StringRequest request = new StringRequest(Request.Method.POST,
                AppConfig.url_login, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d(TAG, response.toString());
                try {
                    JSONObject obj = new JSONObject(response);
                    int success = obj.getInt("success");

                    if (success == 1) {

                        i = 1;
                        Crouton.makeText(Activity_Login.this,
                                "Login Successfully",
                                Style.CONFIRM)
                                .show();

                        session.createLoginSession(str_username);

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


    // End forgot pasword email fnction
    @Override
    public void onBackPressed() {
        // your code.
        new AlertDialog.Builder(Activity_Login.this)
                .setTitle("MSS Travels")
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
                            @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
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

