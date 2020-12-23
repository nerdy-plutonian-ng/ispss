package com.persol.ispss;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import static com.persol.ispss.Constants.APP_ID_TEST;
import static com.persol.ispss.Constants.APP_KEY_TEST;
import static com.persol.ispss.Constants.DOMAIN;
import static com.persol.ispss.Constants.EMERGENT_REDIRECT;
import static com.persol.ispss.Constants.GENERATE_PAYMENT_INVOICE;
import static com.persol.ispss.Constants.GET_A_MEMBER;
import static com.persol.ispss.Constants.GET_MEMBER_NO_FILTER;
import static com.persol.ispss.Constants.ISPSS;
import static com.persol.ispss.Constants.PAY_URL_TEST;

public class PayDialog extends DialogFragment {

    private ISPSSManager ispss_manager;
    private DialogFragment dialogFragment;
    private TextView payeeNameTV;
    private boolean faved = false;
    private String searchedMemberId = "";
    private String searchedMemberName = "";
    private TextInputLayout contributorID_TIL;
    private AutoCompleteTextView contribution_Et;
    private String[] favNames;
    private ArrayList<Favourite> favouriteArrayList;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Get the layout inflater
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        final View view = inflater.inflate(R.layout.pay_member_layout, null);
        ispss_manager =  new ISPSSManager(getActivity());
        dialogFragment = new Loader();

        favouriteArrayList = ispss_manager.getFavourites();
        favNames = new String[favouriteArrayList.size()];
        for(int i = 0; i < favouriteArrayList.size();i++){
            favNames[i] = favouriteArrayList.get(i).getName() + " - " + favouriteArrayList.get(i).getId() ;
            Log.d(ISPSS, "fav: "+favouriteArrayList.get(i).getName());
        }
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1,favNames);


        final CheckBox momoCheckbox = view.findViewById(R.id.momoCheckbox);
        contributorID_TIL = view.findViewById(R.id.contributorID_TIL);
        final TextInputLayout amount_TIL = view.findViewById(R.id.amount_TIL);
        final TextInputLayout description_TIL = view.findViewById(R.id.description_TIL);
        final TextInputLayout momo_TIL = view.findViewById(R.id.momo_TIL);
        contribution_Et = view.findViewById(R.id.contributorID_Et);
        final TextInputEditText amount_Et = view.findViewById(R.id.amount_Et);
        final TextInputEditText description_Et = view.findViewById(R.id.description_Et);
        final TextInputEditText momo_Et = view.findViewById(R.id.momo_Et);
        MaterialButton payBtn = view.findViewById(R.id.payBtn);
        payeeNameTV = view.findViewById(R.id.contributorNameTV);

        contribution_Et.setAdapter(arrayAdapter);
        contribution_Et.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                contribution_Et.setText(favouriteArrayList.get(i).getId());
            }
        });

        contribution_Et.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if(editable.toString().length() == 7){
                    getMemberName(contribution_Et.getText().toString());
                }
            }
        });

        contributorID_TIL.setEndIconOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(payeeNameTV.getText().toString().isEmpty() || payeeNameTV.getText().toString().equals(getString(R.string.no_such_user)) || payeeNameTV.getText().toString().equals(getString(R.string.enter_valid_user))){
                    Toast.makeText(getActivity(), "You must search a member first", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(faved){
                    if(ispss_manager.deleteFavourite(searchedMemberId)){
                        contributorID_TIL.setEndIconDrawable(R.drawable.star_border);
                        faved = false;
                        Toast.makeText(getActivity(), "Member is no longer a favourite", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getActivity(), "Failed removing member from favourites", Toast.LENGTH_SHORT).show();
                    }

                } else {
                    if(ispss_manager.addToFavourites(new Favourite(searchedMemberId,searchedMemberName))){
                        contributorID_TIL.setEndIconDrawable(R.drawable.star);
                        faved = true;
                        Toast.makeText(getActivity(), "You made this member a favourite", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getActivity(), "Error adding member to favourites", Toast.LENGTH_SHORT).show();
                    }

                }
            }
        });


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

        payBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(contribution_Et.getText().toString().trim().isEmpty()){
                    contributorID_TIL.setError(getString(R.string.verify_member_error));
                    payeeNameTV.setText("");
                    payeeNameTV.setVisibility(View.GONE);
                    return;
                }

                if(amount_Et.getText().toString().trim().isEmpty()){
                    amount_TIL.setError(getString(R.string.empty_error));
                    return;
                }

                if(description_Et.getText().toString().trim().isEmpty()){
                    description_TIL.setError(getString(R.string.empty_error));
                    return;
                }

                if(momoCheckbox.isChecked() && (momo_Et.getText().toString().isEmpty() || momo_Et.getText().toString().length() < 10)){
                    momo_TIL.setError(getString(R.string.empty_error));
                    return;
                }

                if(payeeNameTV.getText().toString().trim().isEmpty() ||
                        payeeNameTV.getText().toString().trim().equals(getString(R.string.no_such_user)) ||
                        payeeNameTV.getText().toString().trim().equals(getString(R.string.enter_valid_user)) ){
                    payeeNameTV.setText(R.string.enter_valid_user);
                    return;
                }

                try{
                    ispss_manager.showDialog(dialogFragment,"loader");
                    JSONObject invoiceData = new JSONObject();
                    invoiceData.put("payeeMemberID",contribution_Et.getText().toString().trim());
                    invoiceData.put("description",description_Et.getText().toString().trim());
                    invoiceData.put("currency", "GHS");
                    invoiceData.put("amount",Double.parseDouble(amount_Et.getText().toString().trim()));
                    invoiceData.put("payerMemberID",ispss_manager.getContributorID());

                    JSONObject data = new JSONObject();
                    data.put("app_id",APP_ID_TEST);
                    data.put("app_key",APP_KEY_TEST);
                    data.put("currency","GHS");
                    data.put("Amount",Double.parseDouble(amount_Et.getText().toString().trim()));
                    data.put("order_desc",description_Et.getText().toString().trim());
                    data.put("mobile", momoCheckbox.isChecked() ? "+233"+momo_Et.getText().toString().trim().substring(1) : "");
                    data.put("return_url",EMERGENT_REDIRECT);

                    JSONArray dataArray = new JSONArray();
                    dataArray.put(invoiceData);
                    dataArray.put(data);

                    Log.e("test", dataArray.toString() );
                    generateInvoice(dataArray);

                }catch (Exception e){
                    ispss_manager.cancelDialog(dialogFragment);
                    Toast.makeText(getActivity(), "Failed to initiate payment", Toast.LENGTH_SHORT).show();
                    Log.e("test", e.toString() );
                }

            }
        });

        builder.setTitle("Pay a Member")
                .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        PayDialog.this.getDialog().cancel();
                    }
                })
                .setView(view);
        return builder.create();
    }

    private void generateInvoice(final JSONArray dataArray){
        try {
            RequestQueue queue = Volley.newRequestQueue(getActivity());
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST,
                    DOMAIN + GENERATE_PAYMENT_INVOICE,
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
                            Log.e("test", e.toString() );
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

    private void getMemberName(String userId){
        RequestQueue queue = Volley.newRequestQueue(getActivity());
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET,
                DOMAIN+GET_MEMBER_NO_FILTER+userId,
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            Log.e("test", response.getJSONArray("body").getJSONObject(0).toString() );
                            if(response.getInt("code") == 0){
                                JSONObject body = response.getJSONArray("body").getJSONObject(0);
                                Log.d(ISPSS, "onResponse: "+body.toString());
                                String fName = body.getString("firstName");
                                String mName = body.getString("middleName");
                                String lName = body.getString("lastName");
                                searchedMemberName = fName +" "+ (mName.isEmpty() ? mName : mName+" ") + lName;
                                searchedMemberId = body.getString("memberID");
                                payeeNameTV.setText(searchedMemberName);
                                payeeNameTV.setTextColor(Color.parseColor("#75a302"));
                                payeeNameTV.setVisibility(View.VISIBLE);
                                setFaveDefaults();
                            } else {
                                Log.d(ISPSS, "onResponse: code = "+response.getInt("code"));
                                payeeNameTV.setTextColor(Color.parseColor("#FF0000"));
                                payeeNameTV.setText(getString(R.string.no_such_user));
                                searchedMemberId = "";
                                searchedMemberName = "";
                                faved = false;
                            }
                        } catch (JSONException e) {
                            payeeNameTV.setTextColor(Color.parseColor("#FF0000"));
                            payeeNameTV.setText(getString(R.string.no_such_user));
                            searchedMemberId = "";
                            searchedMemberName = "";
                            faved = false;
                            Log.d(ISPSS, "onResponse: "+e.toString());
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        payeeNameTV.setText("");
                        searchedMemberId = "";
                        searchedMemberName = "";
                        faved = false;
                    }
                });
        queue.add(jsonObjectRequest);
    }

    private void setFaveDefaults(){
        int result = ispss_manager.getFavouriteIndex(searchedMemberId);
        if(result == -1){
            contributorID_TIL.setEndIconDrawable(R.drawable.star_grey);
            faved = false;
        } else {
            contributorID_TIL.setEndIconDrawable(R.drawable.star);
            faved = true;
        }
    }
}