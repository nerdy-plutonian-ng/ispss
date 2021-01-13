package com.persol.ispss;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.google.gson.JsonArray;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import static com.persol.ispss.Constants.ADD_BENEFICIARIES_TO_SCHEME;
import static com.persol.ispss.Constants.ADD_NEW_BENEFICIARY;
import static com.persol.ispss.Constants.DOMAIN;
import static com.persol.ispss.Constants.ISPSS;

public class CheckBeneficiariesDialog extends DialogFragment {

    private ArrayList<Beneficiary> allBeneficiaries,selectedBeneficiaries;
    private String[] availableBeneficiaries;
    private ISPSSManager ispssManager;
    private DialogFragment dialogFragment;
    private String schemeID;
    private Context context;

    public CheckBeneficiariesDialog(Context context,ArrayList<Beneficiary> allBeneficiaries, String schemeID) {
        this.context = context;
        this.allBeneficiaries = allBeneficiaries;
        this.selectedBeneficiaries = new ArrayList<>();
        availableBeneficiaries = new String[this.allBeneficiaries.size()];
        int count = 0;
        this.schemeID = schemeID;
        for(Beneficiary beneficiary : allBeneficiaries){
            availableBeneficiaries[count] = beneficiary.getFirstName() + " " + beneficiary.getLastName();
            count++;
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        ispssManager = new ISPSSManager(getActivity());
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Set the dialog title
        builder.setTitle(R.string.select_beneficiaries)
                .setMultiChoiceItems(availableBeneficiaries, null,
                        new DialogInterface.OnMultiChoiceClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which,
                                                boolean isChecked) {
                                if (isChecked) {
                                    // If the user checked the item, add it to the selected items
                                    selectedBeneficiaries.add(allBeneficiaries.get(which));
                                } else if (selectedBeneficiaries.contains(selectedBeneficiaries.get(which))) {
                                    // Else, if the item is already in the array, remove it
                                    selectedBeneficiaries.remove(which);
                                }
                            }
                        })
                // Set the action buttons
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        // User clicked OK, so save the selectedItems results somewhere
                        // or return them to the component that opened the dialog
                        Log.d(ISPSS, "onClick: "+selectedBeneficiaries.toString());
                        ((SingleSchemeActivity)context).addToBens(selectedBeneficiaries);

//                        try{
//                            JSONArray jsonArray = new JSONArray();
//                            for(Beneficiary beneficiary : selectedBeneficiaries){
//                                JSONObject jsonObject = new JSONObject();
//                                jsonObject.put("beneficiaryId",beneficiary.getId());
//                                jsonObject.put("schemeId",schemeID);
//                                jsonObject.put("percentage",10.00);
//                                jsonArray.put(jsonObject);
//                            }
//                            registerBeneficiaries(jsonArray);
//                        } catch(Exception e) {
//
//                        }

                    }
                })
                .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        selectedBeneficiaries.clear();
                    }
                });

        return builder.create();
    }

    private void registerBeneficiaries(JSONArray data){

        dialogFragment = new Loader();
        ispssManager.showDialog(dialogFragment, "loader");
        RequestQueue queue = Volley.newRequestQueue(getActivity());
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.POST,
                DOMAIN + ADD_BENEFICIARIES_TO_SCHEME,
                data,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        Log.e("response",response.toString());
                        ispssManager.cancelDialog(dialogFragment);
                        try{
                            if(response.length() > 0){
                                Log.d(ISPSS, "onResponse: worked");
                                //Toast.makeText(getActivity(), "Beneficiary added successfully", Toast.LENGTH_SHORT).show();
                                //((BeneficiaryActivity)getActivity()).refreshBeneficiary();
                            } else {
                                Toast.makeText(getActivity(), "Failed adding beneficiary", Toast.LENGTH_SHORT).show();
                            }
                        } catch (Exception e){
                            Log.d(ISPSS, "onResponse: "+e.toString());
                            Log.e("error", e.toString());
                            //Toast.makeText(getActivity(), "Failed adding beneficiary", Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d(ISPSS, "onErrorResponse: "+error.toString());
                        ispssManager.cancelDialog(dialogFragment);
                        //Toast.makeText(getActivity(), "Failed adding beneficiary", Toast.LENGTH_SHORT).show();

                    }
                }){
            @Override
            protected Response<JSONArray> parseNetworkResponse(NetworkResponse response) {
                try {
                    String json = new String(response.data,
                            HttpHeaderParser.parseCharset(response.headers));
                    Log.d(ISPSS, "parseNetworkResponse: "+json);
                    Gson gson = new Gson();
                    JSONArray jsonArray = new JSONArray();
                    JSONObject jsonObject = new JSONObject(json);
                    jsonArray.put(jsonObject);
                    return Response.success(jsonArray,HttpHeaderParser.parseCacheHeaders(response));
                } catch (Exception e){
                    return Response.success(new JSONArray(),HttpHeaderParser.parseCacheHeaders(response));
                }

            }
        };
        queue.add(jsonArrayRequest);
    }
}


