package com.persol.ispss;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.UUID;

import static com.persol.ispss.Constants.ADD_A_MEMBER;
import static com.persol.ispss.Constants.ADD_CRITICAL_INFO;
import static com.persol.ispss.Constants.BECOME_A_MEMBER;
import static com.persol.ispss.Constants.BankAccountTypesGlobal;
import static com.persol.ispss.Constants.DOMAIN;
import static com.persol.ispss.Constants.SchemesGlobal;

public class AddSchemeFragment extends Fragment {

    private Spinner schemeSpinner;
    private TextInputLayout percentageOnPay_TIL,maxContribution_TIL;
    private TextInputEditText percentageOnPay_ET,maxContribution_ET;
    private ExtendedFloatingActionButton saveButton;
    private DialogFragment dialogFragment;
    private ISPSSManager ispssManager;

    public AddSchemeFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_add_scheme, container, false);

        percentageOnPay_TIL = view.findViewById(R.id.percentageOnPay_TIL);
        maxContribution_TIL = view.findViewById(R.id.maxContribution_TIL);
        percentageOnPay_ET = view.findViewById(R.id.percentageOnPay_ET);
        maxContribution_ET = view.findViewById(R.id.maxContribution_ET);
        saveButton = view.findViewById(R.id.saveButton);
        schemeSpinner = view.findViewById(R.id.schemesSpinner);

        ispssManager = new ISPSSManager(getActivity());

        ArrayAdapter<String> schemeArrayAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item,
                ispssManager.getSchemeNames(SchemesGlobal));
        schemeArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        schemeSpinner.setAdapter(schemeArrayAdapter);

        ispssManager = new ISPSSManager(getActivity());
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(percentageOnPay_ET.getText().toString().trim().isEmpty()){
                    percentageOnPay_TIL.setError(getString(R.string.empty_error));
                    return;
                }

                if(maxContribution_ET.getText().toString().trim().isEmpty()){
                    maxContribution_TIL.setError(getString(R.string.empty_error));
                    return;
                }

                if(Double.parseDouble(percentageOnPay_ET.getText().toString().trim()) < 1){
                    percentageOnPay_TIL.setError(getString(R.string.zero_error));
                    return;
                }

                if(Double.parseDouble(maxContribution_ET.getText().toString().trim()) < 1){
                    maxContribution_TIL.setError(getString(R.string.zero_error));
                    return;
                }
                dialogFragment = new Loader();
                NewMember newMember = new NewMember();
                newMember.setAppr(Double.parseDouble(percentageOnPay_ET.getText().toString().trim()));
                newMember.setMmc(Double.parseDouble(maxContribution_ET.getText().toString().trim()));
                newMember.setScheme(new Scheme(SchemesGlobal.get(schemeSpinner.getSelectedItemPosition()).getId(),"name"));
                ispssManager.showDialog(dialogFragment,"loader");
                if(newMember.isMemberExisting()){
                    joinScheme(newMember);
                } else {
                    addMember(newMember);
                }
            }
        });

        return view;
    }

    private void joinScheme(final NewMember newMember){

        try{
            JSONObject data = new JSONObject();
            JSONObject additional = new JSONObject();
            additional.put("memberID",ispssManager.getContributorID());
            additional.put("residence",newMember.getResidence());
            additional.put("occupation",newMember.getOccupation());
            additional.put("hometown",newMember.getHometown());
            additional.put("mmc",newMember.getMmc());
            JSONArray beneficiaries = new JSONArray();
            for(Beneficiary beneficiary : newMember.getBeneficiaries()) {
                JSONObject beneficiaryObject = new JSONObject();
                beneficiaryObject.put("relationshipId", beneficiary.getRelationship());
                beneficiaryObject.put("firstName", beneficiary.getFirstName());
                beneficiaryObject.put("middleName", "");
                beneficiaryObject.put("lastName", beneficiary.getLastName());
                beneficiaryObject.put("gender", beneficiary.getGender());
                beneficiaryObject.put("dob", Utils.getISODate(beneficiary.getDob()));
                beneficiaryObject.put("phoneNumber", beneficiary.getPhone());
                beneficiaryObject.put("emailAddress", "");
                beneficiaryObject.put("percentage", beneficiary.getPercentage());
                beneficiaries.put(beneficiaryObject);
            }
            JSONObject bank = new JSONObject();
            JSONObject momo = new JSONObject();
            if(newMember.getBankAccount() == null){
                bank = null;
                momo.put("accountName",newMember.getMomoAccount().getMomoName());
                momo.put("accountNumber",newMember.getMomoAccount().getMomoNumber());
                momo.put("providerId",newMember.getMomoAccount().getMomoProvider());
                momo.put("isDefault",true);
            } else {
                bank.put("accountName",newMember.getBankAccount().getBankAccountName());
                bank.put("accountNumber",newMember.getBankAccount().getBankNumber());
                bank.put("accountTypeId",newMember.getBankAccount().getBankAccountType());
                bank.put("bankName",newMember.getBankAccount().getBankName());
                bank.put("bankBranch",newMember.getBankAccount().getBankBranch());
                bank.put("isDefault",true);
                momo = null;
            }
            JSONObject scheme = new JSONObject();
            scheme.put("schemeId",newMember.getScheme().getId());
            scheme.put("appr",newMember.getScheme().getPercentage());
            data.put("additional",additional);
            data.put("beneficiaries",beneficiaries);
            data.put("bank",bank);
            data.put("momo",momo);
            data.put("fundSetup",scheme);
            JSONObject idCard = new JSONObject();
            idCard.put("idTypeId",newMember.getNationalId().getIdType());
            idCard.put("idNumber",newMember.getNationalId().getIdNumber());
            idCard.put("nameOnCard",newMember.getNationalId().getNameOnCard());
            idCard.put("expiresOn",Utils.getISODate(newMember.getNationalId().getExpiryDate()));
            data.put("idCard",idCard);
            Log.e("criti",data.toString());
            RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST,
                    DOMAIN + BECOME_A_MEMBER,
                    data,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                if(response.getInt("code") == 0){
                                    Log.e("test",response.toString());
                                    ispssManager.updateMMC(response.getJSONObject("body").getDouble("mmc"));
                                    ispssManager.cancelDialog(dialogFragment);
                                    Intent intent = new Intent(getActivity(),HomeActivity.class);
                                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    startActivity(intent);
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            ispssManager.cancelDialog(dialogFragment);
                        }
                    });
            requestQueue.add(jsonObjectRequest);
        } catch (Exception e){

        }

    }

    private void addMember(NewMember newMember){

        try{
            JSONObject data = new JSONObject();
            JSONObject personal = new JSONObject();
            personal.put("firstName",newMember.getFirstName());
            personal.put("middleName",newMember.getMiddleName());
            personal.put("lastName",newMember.getLastName());
            personal.put("gender",newMember.getGender());
            personal.put("dob",Utils.getISODate(newMember.getDateOfBirth()));
            personal.put("phoneNumber",newMember.getPhone());
            personal.put("emailAddress",newMember.getEmail());
            personal.put("residence",newMember.getResidence());
            personal.put("occupation",newMember.getOccupation());
            personal.put("hometown",newMember.getHometown());
            JSONArray beneficiaries = new JSONArray();
            for(Beneficiary beneficiary : newMember.getBeneficiaries()) {
                JSONObject beneficiaryObject = new JSONObject();
                beneficiaryObject.put("relationshipId", beneficiary.getRelationship());
                beneficiaryObject.put("firstName", beneficiary.getFirstName());
                beneficiaryObject.put("middleName", "");
                beneficiaryObject.put("lastName", beneficiary.getLastName());
                beneficiaryObject.put("gender", beneficiary.getGender());
                beneficiaryObject.put("dob", Utils.getISODate(beneficiary.getDob()));
                beneficiaryObject.put("phoneNumber", beneficiary.getPhone());
                beneficiaryObject.put("emailAddress", "");
                beneficiaryObject.put("percentage", beneficiary.getPercentage());
                beneficiaries.put(beneficiaryObject);
            }
            JSONObject bank = new JSONObject();
            JSONObject momo = new JSONObject();
            if(newMember.getBankAccount() == null){
                bank = null;
                momo.put("accountName",newMember.getMomoAccount().getMomoName());
                momo.put("accountNumber",newMember.getMomoAccount().getMomoNumber());
                momo.put("providerId",newMember.getMomoAccount().getMomoProvider());
                momo.put("isDefault",true);
            } else {
                bank.put("accountName",newMember.getBankAccount().getBankAccountName());
                bank.put("accountNumber",newMember.getBankAccount().getBankNumber());
                bank.put("accountTypeId",newMember.getBankAccount().getBankAccountType());
                bank.put("bankName",newMember.getBankAccount().getBankName());
                bank.put("bankBranch",newMember.getBankAccount().getBankBranch());
                bank.put("isDefault",true);
                momo = null;
            }
            JSONObject scheme = new JSONObject();
            scheme.put("schemeId",newMember.getScheme().getId());
            scheme.put("appr",newMember.getScheme().getPercentage());
            scheme.put("mmc",newMember.getMmc());
            data.put("personal",personal);
            data.put("beneficiaries",beneficiaries);
            data.put("bank",bank);
            data.put("momo",momo);
            data.put("fundSetup",scheme);
            Log.e("criti",data.toString());
            RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST,
                    DOMAIN + ADD_A_MEMBER,
                    data,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                if(response.getInt("code") == 0){
                                    Log.e("test",response.toString());
                                    ispssManager.updateMMC(response.getJSONObject("body").getDouble("mmc"));
                                    ispssManager.cancelDialog(dialogFragment);
                                    Toast.makeText(getActivity(), "Member registered successfully", Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(getActivity(),HomeActivity.class);
                                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    startActivity(intent);
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            ispssManager.cancelDialog(dialogFragment);
                        }
                    });
            requestQueue.add(jsonObjectRequest);
        } catch (Exception e){

        }

    }
}