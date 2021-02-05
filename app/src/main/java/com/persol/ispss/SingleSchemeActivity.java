package com.persol.ispss;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.gson.Gson;
import com.google.gson.JsonArray;

import org.json.JSONArray;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.UUID;

import static com.persol.ispss.Constants.Beneficiaries;
import static com.persol.ispss.Constants.DOMAIN;
import static com.persol.ispss.Constants.GET_ALL_BENEFICIARIES;
import static com.persol.ispss.Constants.GET_ALL_BENEFICIARIES_WITH_SCHEMES;
import static com.persol.ispss.Constants.ISPSS;
import static com.persol.ispss.Constants.SchemesGlobal;
import static com.persol.ispss.Constants.UPDATE_SCHEME_BENEFICIARIES;

public class SingleSchemeActivity extends AppCompatActivity {

    private TextInputLayout appr_TIL;
    private TextInputEditText appr_ET;
    private ISPSSManager ispssManager;
    private DialogFragment dialogFragment;
    private RecyclerView recyclerView;
    private TextView savingsTV;
    private TextView startDateTV;
    private Intent intent;
    private Scheme scheme;
    private ArrayList<Beneficiary> currentBeneficiaries, currentBeneficiariesToSend;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_scheme);

        //toolbar
        Toolbar myToolbar = findViewById(R.id.schemeAppBar);
        setSupportActionBar(myToolbar);
        ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);
        intent = getIntent();

        Gson gson = new Gson();
        String schemeJSONStr = intent.getStringExtra(getString(R.string.scheme));
        Log.d(ISPSS, "onCreate: "+schemeJSONStr);
        scheme = gson.fromJson(schemeJSONStr,Scheme.class);
        ab.setTitle(scheme.getName());

        currentBeneficiaries = new ArrayList<>();
        currentBeneficiariesToSend = new ArrayList<>();
        currentBeneficiaries.addAll(Arrays.asList(scheme.getBeneficiaries()));
        currentBeneficiariesToSend.addAll(currentBeneficiaries);


        appr_TIL = findViewById(R.id.appr_TIL);
        appr_ET = findViewById(R.id.appr_Et);
        savingsTV = findViewById(R.id.savingsTV);
        startDateTV = findViewById(R.id.startTV);
        savingsTV.setText(Utils.formatMoney(scheme.getSavings()));
        startDateTV.setText(Utils.getHumanDate(scheme.getStartDate()));
        appr_ET.setText(Utils.formatMoney(scheme.getPercentage()));
        final ImageView addBeneficiaryBtn = findViewById(R.id.addBeneficiaryBtn);
        final ImageView saveBeneficiaryBtn = findViewById(R.id.saveBeneficiaryBtn);
        recyclerView = findViewById(R.id.beneficiariesRecyclerView);
        recyclerView.setHasFixedSize(false);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        ispssManager = new ISPSSManager(this);
        setupScreen();

        addBeneficiaryBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(ISPSS, "onClick: clicked outer");
                ArrayList<Beneficiary> availableBeneficiaries = new ArrayList<>();

                for(Beneficiary beneficiary : Beneficiaries){
                    if(!ispssManager.beneficiariesHas(currentBeneficiaries,beneficiary.getId())){
                        availableBeneficiaries.add(beneficiary);
                        Log.d(ISPSS, "this beneficiary = : "+beneficiary.getFirstName());
                    }
                }
                if(availableBeneficiaries.size() < 1){
                    Toast.makeText(SingleSchemeActivity.this, "All your beneficiaries are already registered on this scheme", Toast.LENGTH_LONG).show();
                    return;
                }
                dialogFragment = new CheckBeneficiariesDialog(SingleSchemeActivity.this,availableBeneficiaries,scheme.getId());
                ispssManager.showDialog(dialogFragment,"checkBeneficiaries");
            }
        });

        appr_TIL.setEndIconOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                appr_ET.setEnabled(!appr_ET.isEnabled());
                if(appr_ET.isEnabled()){
                    appr_TIL.setEndIconDrawable(R.drawable.edit_primary);
                }
            }
        });

        saveBeneficiaryBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isben100(currentBeneficiaries)){
                    try {
                        JSONArray data = new JSONArray();
                        for(Beneficiary beneficiary : currentBeneficiariesToSend){
                            JSONObject jsonObject = new JSONObject();
                            jsonObject.put("beneficiaryId",beneficiary.getId());
                            jsonObject.put("schemeId",scheme.getId());
                            jsonObject.put("percentage",beneficiary.getPercentage());
                            jsonObject.put("isDeleted",beneficiary.isDeleted());
                            data.put(jsonObject);
                        }
                        updateBeneficiaries(data);
                    } catch (Exception e){

                    }
                }
            }
        });
    }

    public boolean isben100(final ArrayList<Beneficiary> beneficiaries){
        double sum = 0;
        for(Beneficiary beneficiary : beneficiaries){
            if(beneficiary.getPercentage() <= 0 || beneficiary.getPercentage() > 100){

                Toast.makeText(this, beneficiary.getFirstName() + " percentage is less than 0 or more  than 100", Toast.LENGTH_LONG).show();
                return false;
            }
            sum += beneficiary.getPercentage();
        }
        Log.d(ISPSS, "benNot100: percentages = "+sum);
        if(sum != 100){
            Toast.makeText(this, "All beneficiaries' percentages must add up to 100%", Toast.LENGTH_LONG).show();
        }
        return sum == 100.00;
    }

    public void refreshBeneficiary() {
//        dialogFragment = new Loader();
//        ispssManager.showDialog(dialogFragment,"loader");
//        RequestQueue queue = Volley.newRequestQueue(this);
//        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET,
//                DOMAIN + GET_ALL_BENEFICIARIES_WITH_SCHEMES + ispssManager.getContributorID() + "/Schemes",
//                null,
//                new Response.Listener<JSONObject>() {
//                    @Override
//                    public void onResponse(JSONObject response) {
//                        Log.d("ISPSS", "onResponse: "+response.toString());
//                        try {
//                            if(response.getInt("code") == 0) {
//                                JSONArray data = response.getJSONArray("body");
//                                Beneficiaries = new Beneficiary[data.length()];
//                                for(int i = 0; i < data.length();i++) {
//                                    JSONArray schemesArray = data.getJSONObject(i).getJSONArray("schemes");
//                                    Scheme[] schemes = new Scheme[schemesArray.length()];
//                                    for(int j = 0; j < schemesArray.length();j++){
//                                        schemes[j] = new Scheme(schemesArray.getJSONObject(j).getString("schemeId"),
//                                                schemesArray.getJSONObject(j).getDouble("percentage"));
//                                    }
//                                    beneficiaries.add(new Beneficiary(data.getJSONObject(i).getString("id"),
//                                            data.getJSONObject(i).getString("firstName"),
//                                            data.getJSONObject(i).getString("lastName"),
//                                            Utils.getDateNoTime(data.getJSONObject(i).getString("dob")),
//                                            data.getJSONObject(i).getString("phoneNumber"),
//                                            data.getJSONObject(i).getString("relationshipId"),
//                                            0.00,
//                                            data.getJSONObject(i).getString("gender"),schemes
//                                            ));
//                                    Beneficiaries[i] = beneficiaries.get(i);
//                                    Log.d("ben Dob", data.getJSONObject(i).getString("dob"));
//                                    setupScreen();
//                                }
//                                for(int i = 0; i < Beneficiaries.length; i++){
//                                    boolean found = false;
//                                    for(int j = 0;j< schemeBeneficiaries.length;j++){
//                                         if(Beneficiaries[i].getId().equals(schemeBeneficiaries[j].getId())){
//                                             found = true;
//                                             break;
//                                         }
//                                    }
//                                    if(!found){
//                                        availableBeneficiaries.add(Beneficiaries[i]);
//                                    }
//                                }
//
//                                ispssManager.cancelDialog(dialogFragment);
//                                return;
//                            }
//                            Toast.makeText(SingleSchemeActivity.this, "Failed getting beneficiaries", Toast.LENGTH_SHORT).show();
//                            ispssManager.cancelDialog(dialogFragment);
//                        } catch (Exception e) {
//                            Log.d(ISPSS, "onResponse: "+e.toString());
//                            ispssManager.cancelDialog(dialogFragment);
//                        }
//                    }
//                },
//                new Response.ErrorListener() {
//                    @Override
//                    public void onErrorResponse(VolleyError error) {
//                        ispssManager.cancelDialog(dialogFragment);
//                        Log.d(ISPSS, "onErrorResponse: "+error);
//                    }
//                });
//        queue.add(jsonObjectRequest);
    }

    private void setupScreen(){
        savingsTV.setText(Utils.formatMoney(scheme.getSavings()));
        startDateTV.setText(Utils.getHumanDate(scheme.getStartDate()));
        appr_ET.setText(Utils.formatMoney(scheme.getPercentage()));
        if(scheme.getBeneficiaries().length > 0){
            recyclerView.setAdapter(new SchemeBeneficiaryAdapter(currentBeneficiaries,this));
            recyclerView.setVisibility(View.VISIBLE);
        }
    }

    public void setNewSchemeBens(ArrayList<Beneficiary> beneficiaries){
//        this.beneficiaries = beneficiaries;
    }

    public void addToBens(ArrayList<Beneficiary> newBeneficiaries){
        if(currentBeneficiaries.size() < 1){
            recyclerView.setVisibility(View.VISIBLE);
        }
        currentBeneficiaries.addAll(newBeneficiaries);
        currentBeneficiariesToSend.addAll(newBeneficiaries);
        recyclerView.setAdapter(new SchemeBeneficiaryAdapter(currentBeneficiaries,this));

    }

    public void removeBeneficiary(int index){
        currentBeneficiaries.remove(index);
        recyclerView.setAdapter(new SchemeBeneficiaryAdapter(currentBeneficiaries,this));
        currentBeneficiariesToSend.get(index).setPercentage(0);
        currentBeneficiariesToSend.get(index).setDeleted(true);
    }

    public void updateBeneficiaries(final  JSONArray data){
        Log.d(ISPSS, "updateBeneficiaries: "+data.toString());
        final DialogFragment loader = new Loader();
        ispssManager.showDialog(loader,"loader");
        RequestQueue queue = Volley.newRequestQueue(this);
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.POST,
                DOMAIN + UPDATE_SCHEME_BENEFICIARIES,
                data,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        try {
                            if(response.length() > 0){
                                JSONObject data = response.getJSONObject(0);
                                if(data.getInt("code") == 0){
                                    Toast.makeText(SingleSchemeActivity.this, "Update Successful", Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                Toast.makeText(SingleSchemeActivity.this, "Update Failed", Toast.LENGTH_SHORT).show();
                            }

                            ispssManager.cancelDialog(loader);
                        } catch (Exception e){
                            ispssManager.cancelDialog(loader);
                            Toast.makeText(SingleSchemeActivity.this, "Update Failed", Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        ispssManager.cancelDialog(loader);
                        Log.d(ISPSS, "onErrorResponse: "+error.toString());
                    }
                }
        ){
            @Override
            protected Response<JSONArray> parseNetworkResponse(NetworkResponse response) {
                try {
                    String json = new String(response.data,
                            HttpHeaderParser.parseCharset(response.headers));
                    Log.d(ISPSS, "parseNetworkResponse: "+json);
                    JSONObject jsonObject = new JSONObject(json);
                    JSONArray jsonArray = new JSONArray();
                    jsonArray.put(jsonObject);
                    return Response.success(jsonArray,
                            HttpHeaderParser.parseCacheHeaders(response));
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return Response.success(new JSONArray(),
                        HttpHeaderParser.parseCacheHeaders(response));
            }
        };
        queue.add(jsonArrayRequest);
    }
}