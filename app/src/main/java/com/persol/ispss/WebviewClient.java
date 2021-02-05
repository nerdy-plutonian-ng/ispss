package com.persol.ispss;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.fragment.app.DialogFragment;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.JsonObject;

import org.json.JSONObject;

import java.net.URL;
import java.util.Set;

import static com.persol.ispss.Constants.DOMAIN;
import static com.persol.ispss.Constants.HOME_EXTRA;
import static com.persol.ispss.Constants.ISPSS;
import static com.persol.ispss.Constants.ISPSS_HOST;
import static com.persol.ispss.Constants.MEMBERID;
import static com.persol.ispss.Constants.NAME;
import static com.persol.ispss.Constants.PAYMENT_INVOICE_UPDATE;

public class WebviewClient extends WebViewClient {

    private Context context;
    private ISPSSManager ispssManager;
    private String memberId;
    private String name;

    public WebviewClient(Context context, String memberId, String name) {
        this.context = context;
        ispssManager = new ISPSSManager(this.context);
        this.memberId = memberId;
        this.name = name;
    }

    @Override
    public boolean shouldOverrideUrlLoading(WebView view, String url)
    {
        view.loadUrl(url);
        return true;
    }


    @Override
    public void onPageFinished(WebView view, String url) {
        super.onPageFinished(view, url);

        Uri uri = Uri.parse(view.getUrl());

        if(uri.getHost().equals(ISPSS_HOST)){
            switch (Integer.parseInt(uri.getQueryParameter("status_code"))){
                case 0:
                    Log.d(ISPSS, "Failed");
                    Toast.makeText(context, "Your transaction failed", Toast.LENGTH_SHORT).show();
                    ((WebViewPaymentActivity)context).finish();
                    break;
                case 1:
                    try{
                        JSONObject data = new JSONObject();
                        data.put("status_code",Integer.parseInt(uri.getQueryParameter("status_code")));
                        data.put("status_message",uri.getQueryParameter("status_message"));
                        data.put("trans_ref_no",uri.getQueryParameter("trans_ref_no"));
                        data.put("order_id",uri.getQueryParameter("order_id"));
                        data.put("signature",uri.getQueryParameter("signature"));
                        Log.d(ISPSS, "onPageFinished: "+data.toString());
                        postResult(data);
                    } catch (Exception e){

                    }
                    Log.d(ISPSS, "Return of the Mac");
                    break;
                case 3:
                    Log.d(ISPSS, "Cancelled");
                    ((WebViewPaymentActivity)context).finish();
                    Toast.makeText(context, "You cancelled the transaction", Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    }

    private void postResult(JSONObject data){
        RequestQueue queue = Volley.newRequestQueue(context);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.PUT,
                DOMAIN + PAYMENT_INVOICE_UPDATE,
                data,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        ((WebViewPaymentActivity)context).finish();
                        Toast.makeText(context, "Transaction Successful", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(context,HomeActivity.class);
                        intent.putExtra(HOME_EXTRA,true);
                        intent.putExtra(MEMBERID,memberId);
                        intent.putExtra(NAME, name);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        context.startActivity(intent);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        ((WebViewPaymentActivity)context).finish();
                        Log.d(ISPSS, "onErrorResponse: "+error.toString());
                        Toast.makeText(context, "Your transaction failed", Toast.LENGTH_SHORT).show();
                    }
                }
        );
        queue.add(jsonObjectRequest);
    }
}
