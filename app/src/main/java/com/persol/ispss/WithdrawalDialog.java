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

import java.util.ArrayList;

import static com.persol.ispss.Constants.APP_ID;
import static com.persol.ispss.Constants.APP_KEY;
import static com.persol.ispss.Constants.DOMAIN;
import static com.persol.ispss.Constants.EMERGENT_REDIRECT;
import static com.persol.ispss.Constants.GENERATE_CONTRIBUTION_INVOICE;
import static com.persol.ispss.Constants.ISPSS;
import static com.persol.ispss.Constants.WITHDRAWAL;

public class WithdrawalDialog extends DialogFragment {

    private ISPSSManager ispss_manager;
    private ArrayList<Scheme> schemes;
    private DialogFragment dialogFragment;

    public WithdrawalDialog(ArrayList<Scheme> schemes) {
        this.schemes = schemes;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Get the layout inflater
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        final View view = inflater.inflate(R.layout.withdrawal_layout, null);

        ispss_manager = new ISPSSManager(getActivity());

        final Spinner schemesSpinner = view.findViewById(R.id.schemesSpinner);
        final TextInputLayout amount_TIL = view.findViewById(R.id.amount_TIL);
        final TextInputLayout description_TIL = view.findViewById(R.id.description_TIL);
        final TextInputEditText amount_Et = view.findViewById(R.id.amount_Et);
        final TextInputEditText description_Et = view.findViewById(R.id.description_Et);
        final MaterialButton withdrawBtn = view.findViewById(R.id.withdrawBtn);

        ArrayAdapter<Scheme> schemeArrayAdapter = new ArrayAdapter<>(getActivity(),android.R.layout.simple_list_item_1,schemes);
        schemesSpinner.setAdapter(schemeArrayAdapter);

        withdrawBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            if(amount_Et.getText().toString().trim().isEmpty()){
                amount_TIL.setError(getString(R.string.empty_error));
                return;
            }

            try{
                JSONObject data = new JSONObject();
                data.put("memberID",ispss_manager.getContributorID());
                data.put("schemeId",schemes.get(schemesSpinner.getSelectedItemPosition()).getId());
                data.put("description",description_Et.getText().toString().trim());
                data.put("currency","GHS");
                data.put("amount",Utils.removeFormatting(amount_Et.getText().toString().trim()));
                placeWithdrawalRequest(data);
            } catch (Exception e){
                Toast.makeText(getActivity(), "Failed to initiate payment", Toast.LENGTH_SHORT).show();
                Log.e("test", e.toString() );
                }

            }
        });

        builder.setTitle("Withdraw from a Fund")
                .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        WithdrawalDialog.this.getDialog().cancel();
                    }
                })
                .setView(view);
        return builder.create();
    }

    private void placeWithdrawalRequest(final JSONObject data){
        Log.d(ISPSS, "placeWithdrawalRequest: "+data.toString());
        dialogFragment = new Loader();
        ispss_manager.showDialog(dialogFragment,"loader");
        try {
            RequestQueue queue = Volley.newRequestQueue(getActivity());
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST,
                    DOMAIN + WITHDRAWAL,
                    data,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                ispss_manager.cancelDialog(dialogFragment);
                                Log.d(ISPSS, "onResponse: "+response.toString());
                                if(response.getInt("code") == 0){
                                    WithdrawalDialog.this.getDialog().cancel();
                                    Toast.makeText(getActivity(), "Withdrawal request successfully placed", Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(getActivity(), "Failed to initiate withdrawal", Toast.LENGTH_SHORT).show();
                                }
                            } catch (JSONException e) {
                                Log.e("error",e.toString());
                                ispss_manager.cancelDialog(dialogFragment);
                                Toast.makeText(getActivity(), "Failed to initiate withdrawal", Toast.LENGTH_SHORT).show();
                                e.printStackTrace();
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Log.e("error",error.toString());
                            ispss_manager.cancelDialog(dialogFragment);
                            Toast.makeText(getActivity(), "Failed to initiate withdrawal", Toast.LENGTH_SHORT).show();
                        }
                    });
            queue.add(jsonObjectRequest);
        } catch (Exception e){
            Log.e("error",e.toString());
            ispss_manager.cancelDialog(dialogFragment);
            Toast.makeText(getActivity(), "Failed to initiate withdrawal", Toast.LENGTH_SHORT).show();
        }
    }
}
