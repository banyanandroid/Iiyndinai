package com.banyan.iiyndinai.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

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

public class StatusActivity extends Activity {

	String TAG = "cart";
	// Session Manager Class
	SessionManager session;
	String str_status;
	String str_id,str_name,str_total_amt;
	int i;

	private ProgressBar mProgressBar;

	ProgressDialog pDialog;
	String status;
	public static RequestQueue queue;

	@Override
	public void onCreate(Bundle bundle) {
		super.onCreate(bundle);
		setContentView(R.layout.activity_status);
		session = new SessionManager(getApplicationContext());

		str_status = "" + session.isLoggedIn();
		isInternetOn();

		Intent mainIntent = getIntent();
		TextView tv4 = (TextView) findViewById(R.id.textView1);
		tv4.setText(mainIntent.getStringExtra("transStatus"));
		System.out.println("STATUSSSSS : " + mainIntent.getStringExtra("transStatus"));

		status = mainIntent.getStringExtra("transStatus");


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
					.getDefaultSharedPreferences(StatusActivity.this);

			str_total_amt = sharedPreferences.getString("totalamt", "totalamt");



			if (status.equalsIgnoreCase("Declined")) {

				try {
					pDialog = new ProgressDialog(StatusActivity.this);
					pDialog.setMessage("Please wait...");
					pDialog.show();
					pDialog.setCancelable(false);
					queue = Volley.newRequestQueue(StatusActivity.this);
					Function_Fail();
					/*Intent ins = new Intent(StatusActivity.this, MainActivity.class);
					startActivity(ins);*/
				} catch (Exception e) {
					// TODO: handle exception
				}

			} else if (status.equalsIgnoreCase("Successful") ) {

				try {
					pDialog = new ProgressDialog(StatusActivity.this);
					pDialog.setMessage("Please wait...");
					pDialog.show();
					pDialog.setCancelable(false);
					queue = Volley.newRequestQueue(StatusActivity.this);
					Function_Success();
					/*Intent ins = new Intent(StatusActivity.this, MainActivity.class);
					startActivity(ins);*/
				} catch (Exception e) {
					// TODO: handle exception
				}

			} else if (status.equalsIgnoreCase("Cancelled")) {

				try {
					pDialog = new ProgressDialog(StatusActivity.this);
					pDialog.setMessage("Please wait...");
					pDialog.show();
					pDialog.setCancelable(false);
					queue = Volley.newRequestQueue(StatusActivity.this);
					Function_Fail();
					/*Intent ins = new Intent(StatusActivity.this, MainActivity.class);
					startActivity(ins);*/
				} catch (Exception e) {
					// TODO: handle exception
				}

			} else {

				try {
					pDialog = new ProgressDialog(StatusActivity.this);
					pDialog.setMessage("Please wait...");
					pDialog.show();
					pDialog.setCancelable(false);
					queue = Volley.newRequestQueue(StatusActivity.this);
					Function_Fail();
					/*Intent ins = new Intent(StatusActivity.this, MainActivity.class);
					startActivity(ins);*/
				} catch (Exception e) {
					// TODO: handle exception
				}

			}

		}else if (str_status.equals("false")){

			Intent login = new Intent(StatusActivity.this, Activity_Login.class);
			startActivity(login);

		}

		System.out.println("CAME STATUS");


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

			new AlertDialog.Builder(StatusActivity.this)
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
									Intent ins = new Intent(StatusActivity.this, StatusActivity.class);
									startActivity(ins);
								}
							}).show();
			//Toast.makeText(this, " Not Connected ", Toast.LENGTH_LONG).show();
			return false;
		}
		return false;
	}

	private void Function_Success() {
		StringRequest request = new StringRequest(Request.Method.POST,
				AppConfig.url_payment_success, new Response.Listener<String>() {

			@Override
			public void onResponse(String response) {
				Log.d(TAG, response.toString());
				Log.d("STATUS ACTIVITY", response.toString());
				try {
					JSONObject obj = new JSONObject(response);
					String respon = obj.getString("response");
					int success = obj.getInt("success");

					System.out.println("Status Activity" + success);

					if (success == 1) {

						i = 1;

						new AlertDialog.Builder(StatusActivity.this)
								.setTitle("Iiyndinai")
								.setMessage("Your Order has been Placed")
								.setIcon(R.mipmap.ic_launcher)
								.setPositiveButton("Done",
										new DialogInterface.OnClickListener() {
											@Override
											public void onClick(DialogInterface dialog,
																int which) {
												// TODO Auto-generated method stub
												// finish();
												Intent ins = new Intent(StatusActivity.this, MainActivity.class);
												startActivity(ins);
												finish();
											}
										}).show();


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

				params.put("userid", str_id);
				params.put("gtotal", str_total_amt);
				params.put("order_id",AppConfig.order_id);

				System.out.println("name" + str_id);
				System.out.println("gtotal" + str_total_amt);

				return params;
			}

		};

		// Adding request to request queue
		queue.add(request);
	}

	private void Function_Fail() {
		StringRequest request = new StringRequest(Request.Method.POST,
				AppConfig.url_payment_fail, new Response.Listener<String>() {

			@Override
			public void onResponse(String response) {
				Log.d(TAG, response.toString());
				Log.d("STATUS ACTIVITY", response.toString());
				try {
					JSONObject obj = new JSONObject(response);
					String respon = obj.getString("response");
					int success = obj.getInt("success");

					System.out.println("Status Activity" + success);

					if (success == 1) {

						i = 1;



						new AlertDialog.Builder(StatusActivity.this)
								.setTitle("Iiyndinai")
								.setMessage("Payment" +status)
								.setIcon(R.mipmap.ic_launcher)
								.setPositiveButton("Done",
										new DialogInterface.OnClickListener() {
											@Override
											public void onClick(DialogInterface dialog,
																int which) {
												// TODO Auto-generated method stub
												// finish();
												Intent ins = new Intent(StatusActivity.this, MainActivity.class);
												startActivity(ins);
											}
										}).show();


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

				params.put("userid", str_id);
				params.put("gtotal", str_total_amt);
				params.put("order_id",AppConfig.order_id);

				System.out.println("name" + str_id);
				System.out.println("gtotal" + str_total_amt);
				System.out.println("order_id" + AppConfig.order_id);

				return params;
			}

		};

		// Adding request to request queue
		queue.add(request);
	}


	public void showToast(String msg) {
		Toast.makeText(this, "Toast: " + msg, Toast.LENGTH_LONG).show();
	}
} 