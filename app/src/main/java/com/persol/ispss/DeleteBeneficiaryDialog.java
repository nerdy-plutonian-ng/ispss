package com.persol.ispss;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import static com.persol.ispss.Constants.Beneficiaries;
import static com.persol.ispss.Constants.DELETE_BENEFICIARY;
import static com.persol.ispss.Constants.DOMAIN;
import static com.persol.ispss.Constants.GET_ALL_BENEFICIARIES;
import static com.persol.ispss.Constants.ISPSS;

public class DeleteBeneficiaryDialog extends DialogFragment {

    private final Context context;
    private final String beneficiaryId;
    private ISPSSManager ispssManager;

    public DeleteBeneficiaryDialog(Context context, String beneficiaryId) {
        this.context = context;
        this.beneficiaryId = beneficiaryId;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        ispssManager = new ISPSSManager(getActivity());
        builder.setMessage(R.string.confirm_remove_beneficiary)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // FIRE ZE MISSILES!
                        deleteBeneficiary();
                    }
                })
                .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User cancelled the dialog
                    }
                });
        // Create the AlertDialog object and return it
        return builder.create();
    }

    private void deleteBeneficiary(){
        final DialogFragment dialogFragment = new Loader();
        ispssManager.showDialog(dialogFragment,"loader");
        RequestQueue queue = Volley.newRequestQueue(context);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.DELETE,
                DOMAIN + DELETE_BENEFICIARY + beneficiaryId,
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            ispssManager.cancelDialog(dialogFragment);
                            if(response.getInt("code") == 0) {
                                ((BeneficiaryActivity)context).refreshBeneficiary();
                                Log.d(ISPSS, "onResponse: "+response.toString());
                                Toast.makeText(context, "Successfully removed beneficiary", Toast.LENGTH_SHORT).show();
                                return;
                            }
                            Toast.makeText(context, "Failed deleting beneficiary", Toast.LENGTH_SHORT).show();
                        } catch (Exception e) {
                            Log.d(ISPSS, "onResponse: "+e.toString());
                            ispssManager.cancelDialog(dialogFragment);
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        ispssManager.cancelDialog(dialogFragment);
                        Log.d(ISPSS, "onErrorResponse: "+error);
                    }
                });
        queue.add(jsonObjectRequest);
    }
}