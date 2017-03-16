package com.banyan.iiyndinai.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.banyan.iiyndinai.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;


public class EnquiryFragment extends Fragment {

    public EnquiryFragment() {
    }

    EditText edt_name, edt_email, edt_mobile, edt_city, edt_address, edt_message;
    String str_name, str_email, str_mobile, str_city, str_address, str_msg;

    Button btn_submit, btn_clear;

    ProgressDialog pDialog;
    public static RequestQueue queue;

    String TAG = "enquiry";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_enquiry, container, false);


        edt_name = (EditText) rootView.findViewById(R.id.enq_edt_name);
        edt_email = (EditText) rootView.findViewById(R.id.enq_edt_email);
        edt_mobile = (EditText) rootView.findViewById(R.id.enq_edt_mobile);
        edt_city = (EditText) rootView.findViewById(R.id.enq_edt_city);
        edt_address = (EditText) rootView.findViewById(R.id.enq_edt_address);
        edt_message = (EditText) rootView.findViewById(R.id.enq_edt_message);

        btn_clear = (Button) rootView.findViewById(R.id.enq_btn_clear);
        btn_submit = (Button) rootView.findViewById(R.id.enq_btn_register);


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
                } else {
                    pDialog = new ProgressDialog(getActivity());
                    pDialog.setMessage("Please wait...");
                    pDialog.show();
                    pDialog.setCancelable(false);
                    queue = Volley.newRequestQueue(getActivity());
                    Function_Send_Enquiry();
                }

            }
        });

        // Inflate the layout for this fragment
        return rootView;
    }

    /******************************
     * Send Enquiry
     *****************************/

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

                        Crouton.makeText(getActivity(),
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
                        Crouton.makeText(getActivity(),
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

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }
}
