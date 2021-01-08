package com.persol.ispss;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.github.ybq.android.spinkit.sprite.Sprite;
import com.github.ybq.android.spinkit.style.Wave;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import static com.persol.ispss.Constants.CONTRIBUTORS_SCHEMES;
import static com.persol.ispss.Constants.DOMAIN;
import static com.persol.ispss.Constants.DOMAIN_ADMIN;
import static com.persol.ispss.Constants.ISPSS;
import static com.persol.ispss.Constants.SCHEMES_LIST;
import static com.persol.ispss.Constants.SchemesGlobal;

public class SchemeActivity extends AppCompatActivity {

    private ISPSSManager ispss_manager;
    private DialogFragment dialogFragment;
    private boolean addSchemeClicked = false;
    private ConstraintLayout emptyCL;
    private RecyclerView recyclerView;
    private ArrayList<Scheme> contributorRegisteredSchemes;
    private SchemesAdapter.Listener schemeListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scheme);

        //toolbar
        Toolbar myToolbar = findViewById(R.id.schemesAppBar);
        setSupportActionBar(myToolbar);
        ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);
        ab.setIcon(ContextCompat.getDrawable(this,R.drawable.config));

        ispss_manager = new ISPSSManager(this);

        emptyCL = findViewById(R.id.emptySchemeCL);
        recyclerView = findViewById(R.id.schemesRecycler);
        recyclerView.setHasFixedSize(true);
        // use a linear layout manager
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        if(ispss_manager.getUserType() == 0){
            emptyCL.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        } else {
            recyclerView.setVisibility(View.VISIBLE);
            emptyCL.setVisibility(View.GONE);
            dialogFragment = new Loader();
            ispss_manager.showDialog(dialogFragment,"loader");
            RequestQueue queue = Volley.newRequestQueue(this);
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET,
                    DOMAIN + CONTRIBUTORS_SCHEMES + ispss_manager.getContributorID()+"/Members",
                    null,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                final ArrayList<Scheme> schemeArrayList = new ArrayList<>();
                                if(response.getInt("code") == 0){
                                    JSONObject body = response.getJSONObject("body");
                                    JSONArray jsonArray = body.getJSONArray("schemes");
                                    for(int i = 0;i < jsonArray.length();i++){
                                        JSONObject data = jsonArray.getJSONObject(i);
                                       // SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
                                        JSONArray jsonArray1 = data.getJSONArray("beneficiaries");
                                        Beneficiary[] beneficiaries = new Beneficiary[jsonArray1.length()];
                                        for(int j = 0;j< jsonArray1.length();j++){
                                            JSONObject beneficiaryObject = jsonArray1.getJSONObject(j);
                                            beneficiaries[j] = new Beneficiary(beneficiaryObject.getString("id"),
                                                    beneficiaryObject.getString("firstName"),
                                                    beneficiaryObject.getString("lastName"),
                                                    Utils.getDate(beneficiaryObject.getString("dob")),
                                                    beneficiaryObject.getString("phoneNumber"),
                                                    beneficiaryObject.getString("relationshipId"),
                                                    Double.parseDouble(beneficiaryObject.getString("percentage")),
                                                    beneficiaryObject.getString("gender"));
                                        }
                                        Scheme scheme = new Scheme(data.getString("schemeId"),
                                                data.getString("schemeName"),data.getDouble("appr"),
                                                Utils.getDateNoTime(data.getString("createdAt")),data.getDouble("balance"),beneficiaries);
                                        schemeArrayList.add(scheme);
                                        Log.d(ISPSS, "onResponse: "+scheme.getStartDate().toString());
                                    }
                                    SchemesAdapter schemesAdapter = new SchemesAdapter(schemeArrayList);
                                    schemeListener = new SchemesAdapter.Listener() {
                                        @Override
                                        public void onClick(int position) {
                                            Intent intent = new Intent(SchemeActivity.this,SingleSchemeActivity.class);
                                            Gson gson = new Gson();
                                            intent.putExtra(getString(R.string.scheme),gson.toJson(schemeArrayList.get(position)));
                                            startActivity(intent);
//                                            dialogFragment = new EditSchemeDialog(schemeArrayList.get(position).getId(),
//                                                    schemeArrayList.get(position).getName(),schemeArrayList.get(position).getPercentage());
//                                            ispss_manager.showDialog(dialogFragment,"editSchemeDialog");
                                        }
                                    };
                                    schemesAdapter.setListener(schemeListener);
                                    contributorRegisteredSchemes = schemeArrayList;
                                    recyclerView.swapAdapter(schemesAdapter,true);
                                    ispss_manager.cancelDialog(dialogFragment);
                                }
                            } catch (JSONException e) {
                                ispss_manager.cancelDialog(dialogFragment);
                                e.printStackTrace();
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            ispss_manager.cancelDialog(dialogFragment);
                        }
                    });
            queue.add(jsonObjectRequest);
        }
    }

    public void refreshPage(){
        if(ispss_manager.getUserType() == 0){
            emptyCL.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        } else {
            recyclerView.setVisibility(View.VISIBLE);
            emptyCL.setVisibility(View.GONE);
            dialogFragment = new Loader();
            ispss_manager.showDialog(dialogFragment,"loader");
            RequestQueue queue = Volley.newRequestQueue(this);
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET,
                    DOMAIN + CONTRIBUTORS_SCHEMES + ispss_manager.getContributorID()+"/Members",
                    null,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                final ArrayList<Scheme> schemeArrayList = new ArrayList<>();
                                if(response.getInt("code") == 0){
                                    JSONObject body = response.getJSONObject("body");
                                    JSONArray jsonArray = body.getJSONArray("schemes");
                                    for(int i = 0;i < jsonArray.length();i++){
                                        JSONObject data = jsonArray.getJSONObject(i);
                                        // SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
                                        JSONArray jsonArray1 = data.getJSONArray("beneficiaries");
                                        Beneficiary[] beneficiaries = new Beneficiary[jsonArray1.length()];
                                        for(int j = 0;j< jsonArray1.length();j++){
                                            JSONObject beneficiaryObject = jsonArray1.getJSONObject(j);
                                            beneficiaries[j] = new Beneficiary(beneficiaryObject.getString("id"),
                                                    beneficiaryObject.getString("firstName"),
                                                    beneficiaryObject.getString("lastName"),
                                                    Utils.getDate(beneficiaryObject.getString("dob")),
                                                    beneficiaryObject.getString("phoneNumber"),
                                                    beneficiaryObject.getString("relationshipId"),
                                                    Double.parseDouble(beneficiaryObject.getString("percentage")),
                                                    beneficiaryObject.getString("gender"));
                                        }
                                        Scheme scheme = new Scheme(data.getString("schemeId"),
                                                data.getString("schemeName"),data.getDouble("appr"),
                                                Utils.getDateNoTime(data.getString("createdAt")),data.getDouble("balance"),beneficiaries);
                                        schemeArrayList.add(scheme);
                                        Log.d(ISPSS, "onResponse: "+scheme.getStartDate().toString());
                                    }
                                    SchemesAdapter schemesAdapter = new SchemesAdapter(schemeArrayList);
                                    schemeListener = new SchemesAdapter.Listener() {
                                        @Override
                                        public void onClick(int position) {
                                            Intent intent = new Intent(SchemeActivity.this,SingleSchemeActivity.class);
                                            Gson gson = new Gson();
                                            intent.putExtra(getString(R.string.scheme),gson.toJson(schemeArrayList.get(position)));
                                            startActivity(intent);
//                                            dialogFragment = new EditSchemeDialog(schemeArrayList.get(position).getId(),
//                                                    schemeArrayList.get(position).getName(),schemeArrayList.get(position).getPercentage());
//                                            ispss_manager.showDialog(dialogFragment,"editSchemeDialog");
                                        }
                                    };
                                    schemesAdapter.setListener(schemeListener);
                                    contributorRegisteredSchemes = schemeArrayList;
                                    recyclerView.swapAdapter(schemesAdapter,true);
                                    ispss_manager.cancelDialog(dialogFragment);
                                }
                            } catch (JSONException e) {
                                ispss_manager.cancelDialog(dialogFragment);
                                e.printStackTrace();
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            ispss_manager.cancelDialog(dialogFragment);
                        }
                    });
            queue.add(jsonObjectRequest);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.add_menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.add:
                if(SchemesGlobal.size() == contributorRegisteredSchemes.size()){
                    Toast.makeText(SchemeActivity.this, "You are registered with all available schemes", Toast.LENGTH_LONG).show();
                    return false;
                }
                DialogFragment addSchemeFragement = new AddSchemeDialog(SchemesGlobal,contributorRegisteredSchemes);
                ispss_manager.showDialog(addSchemeFragement,"addScheme");
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
