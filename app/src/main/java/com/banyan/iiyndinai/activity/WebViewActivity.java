package com.banyan.iiyndinai.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.banyan.iiyndinai.R;
import com.banyan.iiyndinai.utility.AvenuesParams;
import com.banyan.iiyndinai.utility.Constants;
import com.banyan.iiyndinai.utility.RSAUtility;
import com.banyan.iiyndinai.utility.ServiceHandler;
import com.banyan.iiyndinai.utility.ServiceUtility;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EncodingUtils;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

public class WebViewActivity extends Activity {
    private ProgressDialog dialog;
    String html, encVal;

    private String access_code = "AVAF66DH35AD50FADA";
    private String merchant_id = "105608";
    private String currency = "INR";
    private String redirect_url = "http://iiyndinai.com/android/payment/ccavResponseHandler.php";
    private String cancel_url = "http://iiyndinai.com/android/payment/ccavResponseHandler.php";
    private String rsa_url = "http://iiyndinai.com/android/payment/GetRSA.php";
    private String billing_country = "India";
    private String amount = null;
    private String order_id = null;

    private String billing_name,billing_address,billing_zipcode,billing_city,billing_state,billing_mobile,billing_email;


    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_webview);

        try {
            // Get a selected id from product
            Bundle extras = getIntent().getExtras();
            if (extras != null) {
                amount = extras.getString("amount");
                System.out.println("WEB AMT " + amount);
            }

            SharedPreferences sharedPreferences = PreferenceManager
                    .getDefaultSharedPreferences(WebViewActivity.this);

            billing_name = sharedPreferences.getString("User_Name", "User_Name");
            billing_address = sharedPreferences.getString("User_Address", "User_Address");
            billing_zipcode = sharedPreferences.getString("User_Zip", "User_Zip");
            billing_city = sharedPreferences.getString("User_City", "User_City");
            billing_state = sharedPreferences.getString("User_State", "User_State");
            billing_mobile = sharedPreferences.getString("User_Mobile", "User_Mobile");
            billing_email = sharedPreferences.getString("User_Email", "User_Email");
            /*

            ;*/

            System.out.println("billing_email "+billing_email);
            System.out.println("billing_mobile "+billing_mobile);

        } catch (Exception e) {

        }

        //amount = getIntent().getStringExtra("amount");

        // Calling async task to get display content
        new RenderView().execute();
    }

    /**
     * Async task class to get json by making HTTP call
     */
    private class RenderView extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Showing progress dialog
            dialog = new ProgressDialog(WebViewActivity.this);
            dialog.setMessage("Please wait...");
            dialog.setCancelable(false);
            dialog.show();
        }

        @Override
        protected Void doInBackground(Void... arg0) {
            // Creating service handler class instance
            ServiceHandler sh = new ServiceHandler();

            Integer randomNum = ServiceUtility.randInt(0, 9999999);
            order_id = randomNum.toString();

            // Making a request to url and getting response
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair(AvenuesParams.ACCESS_CODE, access_code));
            params.add(new BasicNameValuePair(AvenuesParams.ORDER_ID, order_id));
            params.add(new BasicNameValuePair(AvenuesParams.BILLING_COUNTRY, billing_country));
            params.add(new BasicNameValuePair(AvenuesParams.BILLING_NAME, billing_name));
            params.add(new BasicNameValuePair(AvenuesParams.BILLING_ADDRESS, billing_address));
            params.add(new BasicNameValuePair(AvenuesParams.BILLING_ZIP, billing_zipcode));
            params.add(new BasicNameValuePair(AvenuesParams.BILLING_STATE, billing_state));
            params.add(new BasicNameValuePair(AvenuesParams.BILLING_CITY, billing_city));
            params.add(new BasicNameValuePair(AvenuesParams.BILLING_TEL, billing_mobile));
            params.add(new BasicNameValuePair(AvenuesParams.BILLING_EMAIL, billing_email));


            /*

            */

            String vResponse = sh.makeServiceCall(rsa_url, ServiceHandler.POST, params);
            System.out.println(vResponse);
            System.out.println("RASPONCE : " + vResponse);
            if (!ServiceUtility.chkNull(vResponse).equals("")
                    && ServiceUtility.chkNull(vResponse).toString().indexOf("ERROR") == -1) {
                StringBuffer vEncVal = new StringBuffer("");
                vEncVal.append(ServiceUtility.addToPostParams(AvenuesParams.AMOUNT, amount));
                vEncVal.append(ServiceUtility.addToPostParams(AvenuesParams.CURRENCY, currency));
                vEncVal.append(ServiceUtility.addToPostParams(AvenuesParams.BILLING_COUNTRY, billing_country));
                vEncVal.append(ServiceUtility.addToPostParams(AvenuesParams.BILLING_NAME, billing_name));
                vEncVal.append(ServiceUtility.addToPostParams(AvenuesParams.BILLING_ADDRESS, billing_address));
                vEncVal.append(ServiceUtility.addToPostParams(AvenuesParams.BILLING_ZIP, billing_zipcode));
                vEncVal.append(ServiceUtility.addToPostParams(AvenuesParams.BILLING_STATE, billing_state));
                vEncVal.append(ServiceUtility.addToPostParams(AvenuesParams.BILLING_CITY, billing_city));
                vEncVal.append(ServiceUtility.addToPostParams(AvenuesParams.BILLING_TEL, billing_mobile));
                vEncVal.append(ServiceUtility.addToPostParams(AvenuesParams.BILLING_EMAIL, billing_email));

               /*

                */
                encVal = RSAUtility.encrypt(vEncVal.substring(0, vEncVal.length() - 1), vResponse);
                System.out.println("ENC :" + encVal);
                System.out.println("AMOUNT FUN : " + amount);
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);

            class MyJavaScriptInterface {
                @JavascriptInterface
                public void processHTML(String html) {
                    // process the html as needed by the app
                    String status = null;
                    if (html.indexOf("Failure") != -1) {
                        status = "Declined";
                    } else if (html.indexOf("Success") != -1) {
                        status = "Successful";
                    } else if (html.indexOf("Aborted") != -1) {
                        status = "Cancelled";
                    } else {
                        status = "unknown";
                    }
                    Toast.makeText(getApplicationContext(), status, Toast.LENGTH_SHORT).show();

                    Intent intent = new Intent(getApplicationContext(), StatusActivity.class);
                    intent.putExtra("transStatus", status);
                    startActivity(intent);
                    finish();
                }
            }

            final WebView webview = (WebView) findViewById(R.id.webview);
            webview.getSettings().setJavaScriptEnabled(true);
            webview.addJavascriptInterface(new MyJavaScriptInterface(), "HTMLOUT");
            webview.setWebViewClient(new WebViewClient() {
                @Override
                public void onPageFinished(WebView view, String url) {
                    super.onPageFinished(webview, url);
                    // Dismiss the progress dialog
                    if (dialog.isShowing())
                        dialog.dismiss();
                    if (url.indexOf("/ccavResponseHandler.php") != -1) {
                        webview.loadUrl("javascript:window.HTMLOUT.processHTML('<head>'+document.getElementsByTagName('html')[0].innerHTML+'</head>');");
                    }
                }

                @Override
                public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                    Toast.makeText(getApplicationContext(), "Oh no! " + description, Toast.LENGTH_SHORT).show();
                }
            });

			/* An instance of this class will be registered as a JavaScript interface */
            StringBuffer params = new StringBuffer();
            params.append(ServiceUtility.addToPostParams(AvenuesParams.ACCESS_CODE, access_code));
            params.append(ServiceUtility.addToPostParams(AvenuesParams.MERCHANT_ID, merchant_id));
            params.append(ServiceUtility.addToPostParams(AvenuesParams.ORDER_ID, order_id));
            params.append(ServiceUtility.addToPostParams(AvenuesParams.REDIRECT_URL, redirect_url));
            params.append(ServiceUtility.addToPostParams(AvenuesParams.CANCEL_URL, cancel_url));
            params.append(ServiceUtility.addToPostParams(AvenuesParams.ENC_VAL, URLEncoder.encode(encVal)));


            String vPostParams = params.substring(0, params.length() - 1);
            try {
                webview.postUrl(Constants.TRANS_URL, EncodingUtils.getBytes(vPostParams, "UTF-8"));
            } catch (Exception e) {
                showToast("Exception occured while opening webview.");
            }
        }
    }

    public void showToast(String msg) {
        Toast.makeText(this, "Toast: " + msg, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onBackPressed() {

    }


} 