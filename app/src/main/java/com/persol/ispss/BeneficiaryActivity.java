package com.persol.ispss;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;

import static com.persol.ispss.Constants.Beneficiaries;
import static com.persol.ispss.Constants.DOMAIN;
import static com.persol.ispss.Constants.GET_ALL_BENEFICIARIES;
import static com.persol.ispss.Constants.GET_ALL_BENEFICIARIES_WITH_SCHEMES;
import static com.persol.ispss.Constants.ISPSS;

public class BeneficiaryActivity extends AppCompatActivity {

    private DialogFragment dialogFragment;
    private ISPSSManager ispssManager;
    private RecyclerView recyclerView;
    private ArrayList<Beneficiary> beneficiaries;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_beneficiary);

        //toolbar
        Toolbar myToolbar = findViewById(R.id.beneficiaryAppBar);
        setSupportActionBar(myToolbar);
        ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);
        ab.setIcon(ContextCompat.getDrawable(this,R.drawable.people));

        beneficiaries = new ArrayList<>();

        recyclerView = findViewById(R.id.beneficiariesRecyclerView);
        recyclerView.setHasFixedSize(false);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        ispssManager = new ISPSSManager(this);
        dialogFragment = new Loader();
        ispssManager.showDialog(dialogFragment,"loader");
        RequestQueue queue = Volley.newRequestQueue(this);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET,
                DOMAIN + GET_ALL_BENEFICIARIES_WITH_SCHEMES + ispssManager.getContributorID() + "/Schemes",
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
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
                                            data.getJSONObject(i).getString("gender"),
                                            schemes));
                                    Beneficiaries[i] = beneficiaries.get(i);
                                    Log.d("ben Dob", data.getJSONObject(i).getString("dob"));
                                }
                                recyclerView.setAdapter(new BeneficiariesAdapter(BeneficiaryActivity.this,beneficiaries));
                                ispssManager.cancelDialog(dialogFragment);
                                return;
                            }
                            Toast.makeText(BeneficiaryActivity.this, "Failed getting beneficiaries", Toast.LENGTH_SHORT).show();
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.list_menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.add:
                dialogFragment = new AddNewBeneficiaryDialog();
                ispssManager.showDialog(dialogFragment,"addNewBeneficiary");
                break;
            case R.id.favourites:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public void refreshBeneficiary() {
        dialogFragment = new Loader();
        ispssManager.showDialog(dialogFragment,"loader");
        RequestQueue queue = Volley.newRequestQueue(this);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET,
                DOMAIN + GET_ALL_BENEFICIARIES + ispssManager.getContributorID(),
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            if(response.getInt("code") == 0) {
                                beneficiaries = new ArrayList<>();
                                JSONArray data = response.getJSONArray("body");
                                Beneficiaries = new Beneficiary[data.length()];
                                for(int i = 0; i < data.length();i++) {
                                    beneficiaries.add(new Beneficiary(data.getJSONObject(i).getString("id"),
                                            data.getJSONObject(i).getString("firstName"),
                                            data.getJSONObject(i).getString("lastName"),
                                            Utils.getDateNoTime(data.getJSONObject(i).getString("dob")),
                                            data.getJSONObject(i).getString("phoneNumber"),
                                            data.getJSONObject(i).getString("relationshipId"),
                                            data.getJSONObject(i).getDouble("percentage"),
                                            data.getJSONObject(i).getString("gender")));
                                    Beneficiaries[i] = beneficiaries.get(i);
                                    Log.d("ben Dob", data.getJSONObject(i).getString("dob"));
                                }
                                recyclerView.setAdapter(new BeneficiariesAdapter(BeneficiaryActivity.this,beneficiaries));
                                ispssManager.cancelDialog(dialogFragment);
                                return;
                            }
                            Toast.makeText(BeneficiaryActivity.this, "Failed getting beneficiaries", Toast.LENGTH_SHORT).show();
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
}