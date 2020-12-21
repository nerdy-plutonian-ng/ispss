package com.persol.ispss;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Spinner;
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
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import static com.persol.ispss.Constants.DOMAIN;
import static com.persol.ispss.Constants.ISPSS;
import static com.persol.ispss.Constants.NetworkProvidersGlobal;
import static com.persol.ispss.Constants.UPDATE_MOMO_ACCOUNT;


public class EditMomoDialog extends DialogFragment {

    private ISPSSManager ispssManager;
    private DialogFragment dialogFragment;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Get the layout inflater
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        final View view = inflater.inflate(R.layout.momo_edit_card, null);

        final TextInputLayout momoNumberTIL = view.findViewById(R.id.momoNumber_TIL);
        final TextInputLayout momoAccountTIL = view.findViewById(R.id.momoAccountName_TIL);
        final TextInputEditText momoNumberEt = view.findViewById(R.id.momoNumber_ET);
        final TextInputEditText momoAccountEt = view.findViewById(R.id.momoAccountName_ET);
        final Spinner providersSpinner = view.findViewById(R.id.providersSpinner);
        MaterialButton editBtn = view.findViewById(R.id.editButton);

        ispssManager = new ISPSSManager(getActivity());

        editBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(momoNumberEt.getText().toString().trim().isEmpty()){
                    momoNumberTIL.setError(getString(R.string.verify_member_error));
                    return;
                }

                if(momoAccountEt.getText().toString().trim().isEmpty()){
                    momoAccountTIL.setError(getString(R.string.empty_error));
                    return;
                }

                try {
                    dialogFragment = new Loader();
                    ispssManager.showDialog(dialogFragment,"loader");
                    JSONObject data = new JSONObject();
                    data.put("memberID",ispssManager.getContributorID());
                    data.put("accountName",momoAccountEt.getText().toString().trim());
                    data.put("accountNumber",momoNumberEt.getText().toString().trim());
                    data.put("providerId",NetworkProvidersGlobal.get(providersSpinner.getSelectedItemPosition()).getId());
                    Log.d(ISPSS, "onClick: "+data.toString());
                    RequestQueue queue = Volley.newRequestQueue(getActivity());
                    JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                            Request.Method.PUT,
                            DOMAIN + UPDATE_MOMO_ACCOUNT,
                            data,
                            new Response.Listener<JSONObject>() {
                                @Override
                                public void onResponse(JSONObject response) {
                                    try {
                                        if(response.getInt("code") == 0){
                                            JSONObject momo = new JSONObject();
                                            JSONObject body = response.getJSONObject("body");
                                            momo.put("accountName",body.getString("accountName"));
                                            momo.put("accountNumber",body.getString("accountNumber"));
                                            momo.put("providerId",body.getString("providerId"));
                                            ispssManager.setMomoDetails(momo.toString());
                                            Toast.makeText(getActivity(), "Update successful", Toast.LENGTH_SHORT).show();
                                            ispssManager.cancelDialog(dialogFragment);
                                            EditMomoDialog.this.getDialog().cancel();
                                            return;
                                        }
                                        Toast.makeText(getActivity(), "Update failed", Toast.LENGTH_SHORT).show();
                                        ispssManager.cancelDialog(dialogFragment);
                                    } catch (JSONException e) {
                                        Log.d(ISPSS, "onResponse: "+e.toString());
                                        Toast.makeText(getActivity(), "Update failed", Toast.LENGTH_SHORT).show();
                                        ispssManager.cancelDialog(dialogFragment);
                                    }

                                }
                            },
                            new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {
                                    ispssManager.cancelDialog(dialogFragment);
                                    Log.d(ISPSS, "onErrorResponse: "+error.toString());
                                    Toast.makeText(getActivity(), "Update failed", Toast.LENGTH_SHORT).show();
                                }
                            });
                    queue.add(jsonObjectRequest);
                } catch (JSONException e) {
                    Log.e(ISPSS, "onClick: "+e.toString() );
                    ispssManager.cancelDialog(dialogFragment);
                }

            }
        });
        try{
            ArrayAdapter<String> providersArrayAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item,
                    ispssManager.getProviderNames(NetworkProvidersGlobal));
            providersArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            providersSpinner.setAdapter(providersArrayAdapter);
            JSONObject data = ispssManager.getMomoDetails();
            int position = 0;
            String providerID = data.getString("providerId");
            for(NetworkProvider networkProvider: NetworkProvidersGlobal){
                if(networkProvider.getId().equals(providerID)){
                    break;
                }
                position++;
            }
            providersSpinner.setSelection(position);
            momoAccountEt.setText(data.getString("accountName"));
            momoNumberEt.setText(data.getString("accountNumber"));
        } catch (Exception e){
            Log.d(ISPSS, "onCreateDialog: "+e.toString());
        }

        builder.setTitle("Mobile Money Details")
                .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        EditMomoDialog.this.getDialog().cancel();
                    }
                })
                .setView(view);
        return builder.create();
    }
}
