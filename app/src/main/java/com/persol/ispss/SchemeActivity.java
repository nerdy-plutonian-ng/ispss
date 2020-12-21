package com.persol.ispss;

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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;

import static com.persol.ispss.Constants.CONTRIBUTORS_SCHEMES;
import static com.persol.ispss.Constants.DOMAIN;
import static com.persol.ispss.Constants.DOMAIN_ADMIN;
import static com.persol.ispss.Constants.SCHEMES_LIST;
import static com.persol.ispss.Constants.SchemesGlobal;

public class SchemeActivity extends AppCompatActivity {

    private ISPSSManager ispss_manager;
    private DialogFragment dialogFragment;
    private boolean addSchemeClicked = false;
    private ConstraintLayout emptyCL;
    private RecyclerView recyclerView;
    private ExtendedFloatingActionButton addSchemeBtn;
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
        addSchemeBtn = findViewById(R.id.addSchemeBtn);

        if(ispss_manager.getUserType() == 0){
            emptyCL.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        } else {
            Log.e("debug","here");
            recyclerView.setVisibility(View.VISIBLE);
            emptyCL.setVisibility(View.GONE);
            dialogFragment = new Loader();
            ispss_manager.showDialog(dialogFragment,"loader");
            RequestQueue queue = Volley.newRequestQueue(this);
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET,
                    DOMAIN + CONTRIBUTORS_SCHEMES + ispss_manager.getContributorID(),
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
                                        Scheme scheme = new Scheme(data.getString("schemeId"),
                                                data.getString("schemeName"),data.getDouble("appr"),
                                                new Date(),data.getDouble("balance"));
                                        schemeArrayList.add(scheme);
                                    }
                                    SchemesAdapter schemesAdapter = new SchemesAdapter(schemeArrayList);
                                    schemeListener = new SchemesAdapter.Listener() {
                                        @Override
                                        public void onClick(int position) {
                                            Intent intent = new Intent(SchemeActivity.this,SingleSchemeActivity.class);
                                            intent.putExtra(getString(R.string.scheme),schemeArrayList.get(position));
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

        addSchemeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(SchemesGlobal.size() == contributorRegisteredSchemes.size()){
                    Toast.makeText(SchemeActivity.this, "You are registered with all available schemes", Toast.LENGTH_LONG).show();
                    return;
                }
                DialogFragment addSchemeFragement = new AddSchemeDialog(SchemesGlobal,contributorRegisteredSchemes);
                ispss_manager.showDialog(addSchemeFragement,"addScheme");
            }
        });
    }

    public void refreshPage(){
        if(ispss_manager.getUserType() == 0){
            emptyCL.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        } else {
            Log.e("debug","here");

            recyclerView.setVisibility(View.VISIBLE);
            emptyCL.setVisibility(View.GONE);
            dialogFragment = new Loader();
            ispss_manager.showDialog(dialogFragment,"loader");
            RequestQueue queue = Volley.newRequestQueue(this);
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET,
                    DOMAIN + CONTRIBUTORS_SCHEMES + ispss_manager.getContributorID(),
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
                                        Scheme scheme = new Scheme(data.getString("schemeId"),
                                                data.getString("schemeName"),data.getDouble("appr"),new Date(),data.getDouble("balance"));
                                        schemeArrayList.add(scheme);
                                    }
                                    SchemesAdapter schemesAdapter = new SchemesAdapter(schemeArrayList);
                                    schemeListener = new SchemesAdapter.Listener() {
                                        @Override
                                        public void onClick(int position) {
                                            dialogFragment = new EditSchemeDialog(schemeArrayList.get(position).getId(),
                                                    schemeArrayList.get(position).getName(),schemeArrayList.get(position).getPercentage());
                                            ispss_manager.showDialog(dialogFragment,"editSchemeDialog");
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

}