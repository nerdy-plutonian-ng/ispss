package com.persol.ispss;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.JsonObject;

import org.json.JSONObject;

import static com.persol.ispss.Constants.ADD_TO_FAVOURITES;
import static com.persol.ispss.Constants.DOMAIN;
import static com.persol.ispss.Constants.HOME_EXTRA;
import static com.persol.ispss.Constants.ISPSS;
import static com.persol.ispss.Constants.MEMBERID;
import static com.persol.ispss.Constants.NAME;
import static com.persol.ispss.Constants.PAYMENT_INVOICE_UPDATE;

public class AddAsFavouriteDialog extends DialogFragment {

    private ISPSSManager ispssManager;
    private String memberID;
    private String name;
    private Context context;

    public AddAsFavouriteDialog(Context context,String memberID, String name) {
        this.memberID = memberID;
        this.name = name;
        this.context = context;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        ispssManager = new ISPSSManager(getActivity());
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Add as a Favourite").
                setMessage("Add "+name)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // FIRE ZE MISSILES!
                        addFavourite(memberID);
                    }
                })
                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User cancelled the dialog
                        Toast.makeText(getActivity(), name+" was not added!", Toast.LENGTH_SHORT).show();
                    }
                });
        // Create the AlertDialog object and return it
        return builder.create();
    }

    private void addFavourite(String memberID){
        try {
            JSONObject data = new JSONObject();
            data.put("memberID",ispssManager.getContributorID());
            data.put("favouriteID",memberID);
            Log.d(ISPSS, "addFavourite: "+data.toString());
            RequestQueue queue = Volley.newRequestQueue(getActivity());
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST,
                    DOMAIN + ADD_TO_FAVOURITES,
                    data,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                if(response.getInt("code") == 0){
                                    Toast.makeText(context, name + " was successfully added to your favourites!", Toast.LENGTH_LONG).show();
                                } else {
                                    Toast.makeText(context, response.getString("status"), Toast.LENGTH_LONG).show();
                                }

                            } catch (Exception e){
                                Toast.makeText(context, "Failed adding to favourites", Toast.LENGTH_SHORT).show();
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Log.d(ISPSS, "onErrorResponse: "+error.toString());
                            Toast.makeText(context, "Failed adding to favourites", Toast.LENGTH_SHORT).show();
                        }
                    }
            );
            queue.add(jsonObjectRequest);
        } catch (Exception e){
            Toast.makeText(getActivity(), "Your transaction failed", Toast.LENGTH_SHORT).show();
        }

    }
}
