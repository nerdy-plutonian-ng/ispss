package com.persol.ispss;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.UUID;

import static com.persol.ispss.Constants.Beneficiaries;
import static com.persol.ispss.Constants.DOMAIN;
import static com.persol.ispss.Constants.GET_ALL_BENEFICIARIES;
import static com.persol.ispss.Constants.GET_ALL_BENEFICIARIES_WITH_SCHEMES;
import static com.persol.ispss.Constants.ISPSS;
import static com.persol.ispss.Constants.SchemesGlobal;

public class SingleSchemeActivity extends AppCompatActivity {

    private TextInputLayout appr_TIL;
    private TextInputEditText appr_ET;
    private ISPSSManager ispssManager;
    private DialogFragment dialogFragment;
    private ArrayList<Beneficiary> beneficiaries;
    private RecyclerView recyclerView;
    private TextView savingsTV;
    private TextView startDateTV;
    private Intent intent;
    private Gson gson;
    private Beneficiary[] schemeBeneficiaries;
    private ArrayList<Beneficiary> availableBeneficiaries;

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

        gson = new Gson();
        String schemeJSONStr = intent.getStringExtra(getString(R.string.scheme));
        Log.d(ISPSS, "onCreate: "+schemeJSONStr);
        final Scheme scheme = gson.fromJson(schemeJSONStr,Scheme.class);
        ab.setTitle(scheme.getName());

        schemeBeneficiaries = scheme.getBeneficiaries();
        availableBeneficiaries = new ArrayList<>();


        appr_TIL = findViewById(R.id.appr_TIL);
        appr_ET = findViewById(R.id.appr_Et);
        savingsTV = findViewById(R.id.savingsTV);
        startDateTV = findViewById(R.id.startTV);
        savingsTV.setText(Utils.formatMoney(scheme.getSavings()));
        startDateTV.setText(Utils.getHumanDate(scheme.getStartDate()));
        appr_ET.setText(Utils.formatMoney(scheme.getPercentage()));
        final ImageView addBeneficiaryBtn = findViewById(R.id.addBeneficiaryBtn);
        recyclerView = findViewById(R.id.beneficiariesRecyclerView);
        recyclerView.setHasFixedSize(false);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        ispssManager = new ISPSSManager(this);
        beneficiaries = new ArrayList<>();
        refreshBeneficiary();

        addBeneficiaryBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(availableBeneficiaries.size() < 1){
                    Toast.makeText(SingleSchemeActivity.this, "All your beneficiaries are already registered on this scheme", Toast.LENGTH_LONG).show();
                    return;
                }
                dialogFragment = new CheckBeneficiariesDialog(availableBeneficiaries,scheme.getId());
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



    }

    public void refreshBeneficiary() {
        dialogFragment = new Loader();
        ispssManager.showDialog(dialogFragment,"loader");
        RequestQueue queue = Volley.newRequestQueue(this);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET,
                DOMAIN + GET_ALL_BENEFICIARIES_WITH_SCHEMES + ispssManager.getContributorID() + "/Schemes",
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d("ISPSS", "onResponse: "+response.toString());
                        try {
                            if(response.getInt("code") == 0) {
                                JSONArray data = response.getJSONArray("body");
                                Beneficiaries = new Beneficiary[data.length()];
                                for(int i = 0; i < data.length();i++) {
                                    JSONArray schemesArray = data.getJSONObject(i).getJSONArray("schemes");
                                    Scheme[] schemes = new Scheme[schemesArray.length()];
                                    for(int j = 0; j < schemesArray.length();j++){
                                        schemes[j] = new Scheme(schemesArray.getJSONObject(j).getString("schemeId"),
                                                schemesArray.getJSONObject(j).getDouble("percentage"));
                                    }
                                    beneficiaries.add(new Beneficiary(data.getJSONObject(i).getString("id"),
                                            data.getJSONObject(i).getString("firstName"),
                                            data.getJSONObject(i).getString("lastName"),
                                            Utils.getDateNoTime(data.getJSONObject(i).getString("dob")),
                                            data.getJSONObject(i).getString("phoneNumber"),
                                            data.getJSONObject(i).getString("relationshipId"),
                                            0.00,
                                            data.getJSONObject(i).getString("gender"),schemes
                                            ));
                                    Beneficiaries[i] = beneficiaries.get(i);
                                    Log.d("ben Dob", data.getJSONObject(i).getString("dob"));
                                    setupScreen();
                                }
                                for(int i = 0; i < Beneficiaries.length; i++){
                                    boolean found = false;
                                    for(int j = 0;j< schemeBeneficiaries.length;j++){
                                         if(Beneficiaries[i].getId().equals(schemeBeneficiaries[j].getId())){
                                             found = true;
                                             break;
                                         }
                                    }
                                    if(!found){
                                        availableBeneficiaries.add(Beneficiaries[i]);
                                    }
                                }

                                ispssManager.cancelDialog(dialogFragment);
                                return;
                            }
                            Toast.makeText(SingleSchemeActivity.this, "Failed getting beneficiaries", Toast.LENGTH_SHORT).show();
                            ispssManager.cancelDialog(dialogFragment);
                        } catch (Exception e) {
                            Log.d(ISPSS, "onResponse: "+e.toString());
                            ispssManager.cancelDialog(dialogFragment);
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        ispssManager.cancelDialog(dialogFragment);
                        Log.d(ISPSS, "onErrorResponse: "+error);
                    }
                });
        queue.add(jsonObjectRequest);
    }

    private void setupScreen(){
        savingsTV.setText(Utils.formatMoney(intent.getDoubleExtra(getString(R.string.savings),0.00)));
        startDateTV.setText(intent.getStringExtra(getString(R.string.start)));
        appr_ET.setText(Utils.formatMoney(intent.getDoubleExtra(getString(R.string.percentage),0.00)));
        recyclerView.setVisibility(View.VISIBLE);
        ArrayList<Beneficiary> beneficiaries = new ArrayList<>(Arrays.asList(schemeBeneficiaries));
        recyclerView.setAdapter(new SchemeBeneficiaryAdapter(beneficiaries));
    }
}