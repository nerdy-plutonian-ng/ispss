package com.persol.ispss;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
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

import java.util.ArrayList;

import static com.persol.ispss.Constants.APP_ID;
import static com.persol.ispss.Constants.APP_KEY;
import static com.persol.ispss.Constants.DOMAIN;
import static com.persol.ispss.Constants.EMERGENT_REDIRECT;
import static com.persol.ispss.Constants.GENERATE_CONTRIBUTION_INVOICE;
import static com.persol.ispss.Constants.PAY_URL_TEST;

public class ContributeDialog extends DialogFragment {

    private boolean payCLicked = false;
    private ArrayList<Scheme> schemes;
    private DialogFragment dialogFragment;
    private ISPSSManager ispss_manager;

    public ContributeDialog(ArrayList<Scheme> schemes) {
        this.schemes = schemes;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Get the layout inflater
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        final View view = inflater.inflate(R.layout.contribute_layout, null);

        ispss_manager = new ISPSSManager(getActivity());

        final CheckBox momoCheckbox = view.findViewById(R.id.momoCheckbox);
        final Spinner schemesSpinner = view.findViewById(R.id.schemesSpinner);
        final TextInputLayout amount_TIL = view.findViewById(R.id.amount_TIL);
        final TextInputLayout momo_TIL = view.findViewById(R.id.momo_TIL);
        final TextInputEditText amount_Et = view.findViewById(R.id.amount_Et);
        final TextInputEditText momo_Et = view.findViewById(R.id.momo_Et);
        final MaterialButton contributeBtn = view.findViewById(R.id.contributeBtn);

        ArrayAdapter<Scheme> schemeArrayAdapter = new ArrayAdapter<>(getActivity(),android.R.layout.simple_list_item_1,schemes);
        schemesSpinner.setAdapter(schemeArrayAdapter);

        momoCheckbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    momo_TIL.setVisibility(View.VISIBLE);
                } else {
                    momo_TIL.setVisibility(View.GONE);
                }
            }
        });

        contributeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(payCLicked){
                    Log.e("payClicked","clicked");
                    return;
                }

                if(amount_Et.getText().toString().trim().isEmpty()){
                    amount_TIL.setError(getString(R.string.empty_error));
                    return;
                }

                if(momoCheckbox.isChecked() && momo_Et.getText().toString().isEmpty()){
                    momo_TIL.setError(getString(R.string.empty_error));
                    return;
                }

                dialogFragment = new Loader();
                ispss_manager.showDialog(dialogFragment,"loader");
                try{
                    JSONObject invoiceData = new JSONObject();
                    invoiceData.put("schemeId",schemes.get(schemesSpinner.getSelectedItemPosition()).getId());
                    invoiceData.put("memberID",ispss_manager.getContributorID());
                    invoiceData.put("description", "Contribution");
                    invoiceData.put("currency","GHS");
                    invoiceData.put("amount",Double.parseDouble(amount_Et.getText().toString().trim()));

                    JSONObject data = new JSONObject();
                    data.put("app_id",APP_ID);
                    data.put("app_key",APP_KEY);
                    data.put("currency","GHS");
                    data.put("Amount",Double.parseDouble(amount_Et.getText().toString().trim()));
                    data.put("order_desc","Contribution");
                    data.put("mobile",momoCheckbox.isChecked() ? "+233"+momo_Et.getText().toString().trim().substring(1) : "");
                    data.put("return_url",EMERGENT_REDIRECT);

                    JSONArray dataArray = new JSONArray();
                    dataArray.put(invoiceData);
                    dataArray.put(data);

                    generateInvoice(dataArray);

                } catch (Exception e){
                    ispss_manager.cancelDialog(dialogFragment);
                    Toast.makeText(getActivity(), "Failed to initiate payment", Toast.LENGTH_SHORT).show();
                    Log.e("test", e.toString() );
                }

            }
        });

        builder.setTitle("Contribute to a Fund")
                .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        ContributeDialog.this.getDialog().cancel();
                    }
                })
                .setView(view);
        return builder.create();
    }

    private void generateInvoice(final JSONArray dataArray){
        try {
            RequestQueue queue = Volley.newRequestQueue(getActivity());
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST,
                    DOMAIN + GENERATE_CONTRIBUTION_INVOICE,
                    dataArray.getJSONObject(0),
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                if(response.getInt("code") == 0){
                                    Log.e("invoiceResult", "onResponse: "+response.toString());
                                    JSONObject data = dataArray.getJSONObject(1).put("Order_id",response.getJSONObject("body").getString("id"));
                                    goPayOnline(data);
                                }
                            } catch (JSONException e) {
                                Log.e("error",e.toString());
                                ispss_manager.cancelDialog(dialogFragment);
                                Toast.makeText(getActivity(), "Failed to initiate payment", Toast.LENGTH_SHORT).show();
                                e.printStackTrace();
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Log.e("error",error.toString());
                            ispss_manager.cancelDialog(dialogFragment);
                            Toast.makeText(getActivity(), "Failed to initiate payment", Toast.LENGTH_SHORT).show();
                        }
                    });
            queue.add(jsonObjectRequest);
        } catch (Exception e){
            Log.e("error",e.toString());
            ispss_manager.cancelDialog(dialogFragment);
            Toast.makeText(getActivity(), "Failed to initiate payment", Toast.LENGTH_SHORT).show();
        }
    }

    private void goPayOnline(JSONObject data){
        RequestQueue queue = Volley.newRequestQueue(getActivity());
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST,
                PAY_URL_TEST,
                data,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.e("test", response.toString() );
                        ispss_manager.cancelDialog(dialogFragment);
                        try {
                            Intent intent1 = new Intent(getActivity(),WebViewPaymentActivity.class);
                            intent1.putExtra("url",response.getString("redirect_url"));
                            startActivity(intent1);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        ispss_manager.cancelDialog(dialogFragment);
                        Toast.makeText(getActivity(), "Failed to initiate payment", Toast.LENGTH_SHORT).show();
                        Log.e("test", error.toString() );
                    }
                });
        queue.add(jsonObjectRequest);
    }
}