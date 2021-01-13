package com.persol.ispss;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
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
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import static com.persol.ispss.Constants.ADD_NEW_BENEFICIARY;
import static com.persol.ispss.Constants.Beneficiaries;
import static com.persol.ispss.Constants.DOMAIN;
import static com.persol.ispss.Constants.ISPSS;
import static com.persol.ispss.Constants.POST;
import static com.persol.ispss.Constants.REGISTER;
import static com.persol.ispss.Constants.RelationshipsGlobal;
import static com.persol.ispss.Constants.UPDATE;
import static com.persol.ispss.Constants.UPDATE_BENEFICIARY;

public class AddNewBeneficiaryDialog extends DialogFragment {

    private TextInputLayout fNameTIL,lNameTIL,dobTIL,phoneTIL;
    private TextInputEditText fNameET,lNameET,dobET,phoneET;
    private Spinner relationsSpinner;
    private MaterialButton saveBtn;
    private ArrayList<Beneficiary> beneficiaryArrayList;
    private ISPSSManager ispssManager;
    private ConstraintLayout emptyCL;
    private RadioGroup genderGroup;
    private DialogFragment dialogFragment;
    private int position = 1000000000;

    public AddNewBeneficiaryDialog(int position) {
        Log.d(ISPSS, "AddNewBeneficiaryDialog: "+ position);
        this.position = position;
    }

    public AddNewBeneficiaryDialog() {
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        final View view = inflater.inflate(R.layout.add_beneficiary_card, null);

        fNameTIL = view.findViewById(R.id.fnameTIL);
        lNameTIL = view.findViewById(R.id.lnameTIL);
        dobTIL = view.findViewById(R.id.DOB_TIL);
        phoneTIL = view.findViewById(R.id.phone_TIL);
        fNameET = view.findViewById(R.id.fName_Et);
        lNameET = view.findViewById(R.id.lName_Et);
        dobET = view.findViewById(R.id.DOB_Et);
        phoneET = view.findViewById(R.id.phone_Et);
        saveBtn = view.findViewById(R.id.saveBtn);
        relationsSpinner = view.findViewById(R.id.relationshipSpinner);
        genderGroup = view.findViewById(R.id.genderGroup);

        fNameET.addTextChangedListener(new MyTextWatcher(fNameTIL));
        lNameET.addTextChangedListener(new MyTextWatcher(lNameTIL));
        phoneET.addTextChangedListener(new MyTextWatcher(phoneTIL));

        ispssManager = new ISPSSManager(getActivity());

        dobET.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if(b){
                    MyDatePicker myDatePicker = new MyDatePicker(getActivity(),dobET);
                    myDatePicker.showDialog();
                }
            }
        });
        ArrayAdapter<String> accountTypeArrayAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item,
                ispssManager.getRelationshipNames(RelationshipsGlobal));
        accountTypeArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        relationsSpinner.setAdapter(accountTypeArrayAdapter);

        if(position != 1000000000){
            fNameET.setText(Beneficiaries[position].getFirstName());
            lNameET.setText(Beneficiaries[position].getLastName());
            dobET.setText(Utils.getSlashedDate(Beneficiaries[position].getDob()));
            phoneET.setText(Beneficiaries[position].getPhone());
            genderGroup.check(Beneficiaries[position].getGender().toLowerCase().contains("f") ? R.id.femaleRadio : R.id.maleRadio);
            saveBtn.setText(getString(R.string.edit));
            relationsSpinner.setSelection(ispssManager.getRelationshipPosition(Beneficiaries[position].getRelationship()));
        }

        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(fNameET.getText().toString().trim().isEmpty()){
                    fNameTIL.setError(getString(R.string.empty_error));
                    return;
                }

                if(lNameET.getText().toString().trim().isEmpty()){
                    lNameTIL.setError(getString(R.string.empty_error));
                    return;
                }

                if(dobET.getText().toString().trim().isEmpty()){
                    dobTIL.setError(getString(R.string.empty_error));
                    return;
                }

                if(phoneET.getText().toString().trim().isEmpty()){
                    phoneTIL.setError(getString(R.string.empty_error));
                    return;
                }

                try {
                        dialogFragment = new Loader();
                        JSONObject beneficiary = new JSONObject();
                        beneficiary.put("relationshipId", RelationshipsGlobal.get(relationsSpinner.getSelectedItemPosition()).getId());
                        beneficiary.put("firstName", fNameET.getText().toString().trim());
                        beneficiary.put("middleName", "");
                        beneficiary.put("lastName", lNameET.getText().toString().trim());
                        beneficiary.put("gender", genderGroup.getCheckedRadioButtonId() == R.id.maleRadio ? "Male" : "Female");
                        beneficiary.put("dob", Utils.getISODate(User.dobDate));
                        beneficiary.put("phoneNumber", phoneET.getText().toString().trim());
                        beneficiary.put("emailAddress", "");
                        beneficiary.put("percentage", 0.00);
                        if(position != 1000000000){
                            beneficiary.put("id",Beneficiaries[position].getId());
                            beneficiary.put("memberId",ispssManager.getContributorID());
                            updateBeneficiary(beneficiary);
                            return;
                        }
                        addBeneficiary(beneficiary);

                } catch (Exception e){
                    Log.d(ISPSS, "onClick: "+e.toString());
                    ispssManager.cancelDialog(dialogFragment);
                }
            }
        });

        builder.setTitle(R.string.add_a_beneficiary)
                .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        AddNewBeneficiaryDialog.this.getDialog().cancel();
                    }
                })
                .setView(view);
        return builder.create();
    }

    private void addBeneficiary(JSONObject beneficiary){
        ispssManager.showDialog(dialogFragment, "loader");
        RequestQueue queue = Volley.newRequestQueue(getActivity());
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST,
                DOMAIN + ADD_NEW_BENEFICIARY+ispssManager.getContributorID(),
                beneficiary,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.e("response",response.toString());
                        ispssManager.cancelDialog(dialogFragment);
                        try{
                            if(response.getInt("code") == 0){
                                AddNewBeneficiaryDialog.this.getDialog().cancel();
                                Toast.makeText(getActivity(), "Beneficiary added successfully", Toast.LENGTH_SHORT).show();
                                ((BeneficiaryActivity)getActivity()).refreshBeneficiary();
                            } else {
                                Toast.makeText(getActivity(), "Failed adding beneficiary", Toast.LENGTH_SHORT).show();
                            }
                        } catch (Exception e){
                            Log.e("error", e.toString());
                            Toast.makeText(getActivity(), "Failed adding beneficiary", Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("response",error.toString());
                        ispssManager.cancelDialog(dialogFragment);
                        Toast.makeText(getActivity(), "Failed adding beneficiary", Toast.LENGTH_SHORT).show();

                    }
                });
        queue.add(jsonObjectRequest);
    }

    private void updateBeneficiary(JSONObject beneficiary){
        ispssManager.showDialog(dialogFragment, "loader");
        RequestQueue queue = Volley.newRequestQueue(getActivity());
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.PUT,
                DOMAIN + UPDATE_BENEFICIARY,
                beneficiary,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.e("response",response.toString());
                        ispssManager.cancelDialog(dialogFragment);
                        try{
                            if(response.getInt("code") == 0){
                                AddNewBeneficiaryDialog.this.getDialog().cancel();
                                Toast.makeText(getActivity(), "Beneficiary added successfully", Toast.LENGTH_SHORT).show();
                                ((BeneficiaryActivity)getActivity()).refreshBeneficiary();
                            } else {
                                Toast.makeText(getActivity(), "Failed adding beneficiary", Toast.LENGTH_SHORT).show();
                            }
                        } catch (Exception e){
                            Log.e("error", e.toString());
                            Toast.makeText(getActivity(), "Failed adding beneficiary", Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("response",error.toString());
                        ispssManager.cancelDialog(dialogFragment);
                        Toast.makeText(getActivity(), "Failed adding beneficiary", Toast.LENGTH_SHORT).show();

                    }
                });
        queue.add(jsonObjectRequest);
    }


}
