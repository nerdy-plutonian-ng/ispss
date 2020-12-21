package com.persol.ispss;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.constraintlayout.widget.ConstraintLayout;
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

import org.json.JSONException;
import org.json.JSONObject;

import static com.persol.ispss.Constants.BankAccountTypesGlobal;
import static com.persol.ispss.Constants.DOMAIN;
import static com.persol.ispss.Constants.ISPSS;
import static com.persol.ispss.Constants.NetworkProvidersGlobal;
import static com.persol.ispss.Constants.UPDATE_BANK_ACCOUNT;
import static com.persol.ispss.Constants.UPDATE_MOMO_ACCOUNT;

public class EditBankDetailsDialog extends DialogFragment {

    private Spinner bankAccountTypeSpinner;
    private TextInputLayout bankAccountNumber_TIL;
    private TextInputLayout bankAccountName_TIL;
    private TextInputLayout bankName_TIL;
    private TextInputLayout bankBranch_TIL;
    private TextInputEditText bankAccountNumber_ET;
    private TextInputEditText bankAccountName_ET;
    private TextInputEditText bankName_ET;
    private TextInputEditText bankBranch_ET;
    private ISPSSManager ispssManager;
    private DialogFragment dialogFragment;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Get the layout inflater
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        final View view = inflater.inflate(R.layout.edit_bank_details_card, null);


        MaterialButton editBtn = view.findViewById(R.id.editButton);
        bankAccountTypeSpinner = view.findViewById(R.id.bankAccountTypeSpinner);
        bankAccountNumber_TIL = view.findViewById(R.id.bankAccountNumber_TIL);
        bankAccountName_TIL = view.findViewById(R.id.bankAccountName_TIL);
        bankName_TIL = view.findViewById(R.id.bankName_TIL);
        bankBranch_TIL = view.findViewById(R.id.bankBranch_TIL);
        bankAccountNumber_ET = view.findViewById(R.id.bankAccountNo_Et);
        bankAccountName_ET = view.findViewById(R.id.bankAccountName_Et);
        bankName_ET = view.findViewById(R.id.bankName_Et);
        bankBranch_ET = view.findViewById(R.id.bankBranch_Et);

        ispssManager = new ISPSSManager(getActivity());

        editBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(bankAccountNumber_ET.getText().toString().trim().isEmpty()){
                    bankAccountNumber_TIL.setError(getString(R.string.empty_error));
                    return ;
                }
                if(bankAccountName_ET.getText().toString().trim().isEmpty()){
                    bankAccountName_TIL.setError(getString(R.string.empty_error));
                    return ;
                }
                if(bankName_ET.getText().toString().trim().isEmpty()){
                    bankName_TIL.setError(getString(R.string.empty_error));
                    return ;
                }
                if(bankBranch_ET.getText().toString().trim().isEmpty()){
                    bankBranch_TIL.setError(getString(R.string.empty_error));
                    return ;
                }

                try {
                    dialogFragment = new Loader();
                    ispssManager.showDialog(dialogFragment,"loader");
                    JSONObject data = new JSONObject();
                    data.put("memberID",ispssManager.getContributorID());
                    data.put("accountName",bankAccountName_ET.getText().toString().trim());
                    data.put("accountNumber",bankAccountNumber_ET.getText().toString().trim());
                    data.put("bankName",bankName_ET.getText().toString().trim());
                    data.put("bankBranch",bankBranch_ET.getText().toString().trim());
                    data.put("accountTypeId",BankAccountTypesGlobal.get(bankAccountTypeSpinner.getSelectedItemPosition()).getId());
                    Log.d(ISPSS, "onClick: "+data.toString());
                    RequestQueue queue = Volley.newRequestQueue(getActivity());
                    JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                            Request.Method.PUT,
                            DOMAIN + UPDATE_BANK_ACCOUNT,
                            data,
                            new Response.Listener<JSONObject>() {
                                @Override
                                public void onResponse(JSONObject response) {
                                    try {
                                        if(response.getInt("code") == 0){
                                            JSONObject bank = new JSONObject();
                                            JSONObject body = response.getJSONObject("body");
                                            bank.put("accountName",body.getString("accountName"));
                                            bank.put("accountNumber",body.getString("accountNumber"));
                                            bank.put("accountTypeId",body.getString("accountTypeId"));
                                            bank.put("bankName",body.getString("bankName"));
                                            bank.put("bankBranch",body.getString("bankBranch"));
                                            ispssManager.setBankDetails(bank.toString());
                                            Toast.makeText(getActivity(), "Update successful", Toast.LENGTH_SHORT).show();
                                            ispssManager.cancelDialog(dialogFragment);
                                            EditBankDetailsDialog.this.getDialog().cancel();
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
                    e.printStackTrace();
                    ispssManager.cancelDialog(dialogFragment);
                    Log.e(ISPSS, "onClick: "+e.toString() );
                }


            }
        });

        try{
            JSONObject data = ispssManager.getBankDetails();
            ArrayAdapter<String> bankAccountTypeArrayAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item,
                    ispssManager.getAccountTypesNames(BankAccountTypesGlobal));
            bankAccountTypeArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            bankAccountTypeSpinner.setAdapter(bankAccountTypeArrayAdapter);
            int position = 0;
            String accountTypeId = data.getString("accountTypeId");
            for(BankAccountType bankAccountType: BankAccountTypesGlobal){
                if(bankAccountType.getId().equals(accountTypeId)){
                    break;
                }
                position++;
            }
            bankAccountTypeSpinner.setSelection(position);
            bankAccountNumber_ET.setText(data.getString("accountNumber"));
            bankAccountName_ET.setText(data.getString("accountName"));
            bankName_ET.setText(data.getString("bankName"));
            bankBranch_ET.setText(data.getString("bankBranch"));

        } catch (Exception e){
            Log.d(ISPSS, "onCreateDialog: "+e.toString());
        }

        builder.setTitle("Bank Account Details")
                .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        EditBankDetailsDialog.this.getDialog().cancel();
                    }
                })
                .setView(view);
        return builder.create();
    }
}
