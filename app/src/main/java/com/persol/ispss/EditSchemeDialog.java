package com.persol.ispss;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
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

import org.json.JSONObject;

import static com.persol.ispss.Constants.DOMAIN;
import static com.persol.ispss.Constants.ISPSS;
import static com.persol.ispss.Constants.UPDATE_SCHEME_CONFIG;

public class EditSchemeDialog extends DialogFragment {

    private String id;
    private String name;
    private double appr;
    private ISPSSManager ispssManager;
    private DialogFragment dialogFragment;

    public EditSchemeDialog(String id, String name, double appr) {
        this.id = id;
        this.name = name;
        this.appr = appr;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Get the layout inflater
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        final View view = inflater.inflate(R.layout.edit_scheme_layout, null);

        ispssManager = new ISPSSManager(getActivity());

        TextView schemeNameTV = view.findViewById(R.id.schemeLabel);
        final TextInputLayout appr_TIL = view.findViewById(R.id.appr_TIL);
        final TextInputEditText appr_ET = view.findViewById(R.id.appr_Et);
        MaterialButton updateButton = view.findViewById(R.id.updateButton);

        schemeNameTV.setText(name);
        appr_ET.setText(Utils.formatMoney(appr));

        updateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(appr_ET.getText().toString().trim().isEmpty()){
                    appr_TIL.setError(getString(R.string.empty_error));
                    return;
                }

                if(Double.parseDouble(appr_ET.getText().toString().trim()) <= 0){
                    appr_TIL.setError(getString(R.string.zero_error));
                    return;
                }

                try{
                    JSONObject data = new JSONObject();
                    data.put("memberId",ispssManager.getContributorID());
                    data.put("schemeId",id);
                    data.put("appr",Utils.removeFormatting(appr_ET.getText().toString().trim()));
                    updateSchemeConfig(data);
                } catch (Exception e){
                    Log.d(ISPSS, "onClick: "+e.toString());
                }

            }
        });

        builder.setTitle("Update Scheme Configuration")
                .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        EditSchemeDialog.this.getDialog().cancel();
                    }
                })
                .setView(view);
        return builder.create();

    }

    private void updateSchemeConfig(JSONObject data){
        final DialogFragment dialogFragment = new Loader();
        ispssManager.showDialog(dialogFragment,"loader");
        RequestQueue queue = Volley.newRequestQueue(getActivity());
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.PUT,
                DOMAIN + UPDATE_SCHEME_CONFIG,
                data,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try{
                            ispssManager.cancelDialog(dialogFragment);
                            if(response.getInt("code") == 0){
                                EditSchemeDialog.this.getDialog().cancel();
                                Toast.makeText(getActivity(), "Updated scheme configuration successfully", Toast.LENGTH_SHORT).show();
                                ((SchemeActivity)(getActivity())).refreshPage();
                                return;
                            }
                            Toast.makeText(getActivity(), "Failed updating scheme configuration", Toast.LENGTH_SHORT).show();
                        } catch (Exception e){
                            ispssManager.cancelDialog(dialogFragment);
                            Log.d(ISPSS, "onResponse: "+e.toString());
                            Toast.makeText(getActivity(), "Failed updating scheme configuration", Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        ispssManager.cancelDialog(dialogFragment);
                        Log.d(ISPSS, "onErrorResponse: "+error.toString());
                        Toast.makeText(getActivity(), "Failed updating scheme configuration", Toast.LENGTH_SHORT).show();
                    }
                });
        queue.add(jsonObjectRequest);
    }
}