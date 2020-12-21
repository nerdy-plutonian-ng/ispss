package com.persol.ispss;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Date;

import static com.persol.ispss.Constants.Beneficiaries;
import static com.persol.ispss.Constants.DOMAIN;
import static com.persol.ispss.Constants.GET_ALL_BENEFICIARIES;
import static com.persol.ispss.Constants.ISPSS;
import static com.persol.ispss.Constants.POST;

public class BeneficiariesActivity extends AppCompatActivity {

    private DialogFragment dialogFragment;
    private ISPSSManager ispssManager;
    private ListView listView;
    private ExtendedFloatingActionButton addBeneficiaryButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_beneficiaries);

        //toolbar
        Toolbar myToolbar = findViewById(R.id.beneficiaryAppBar);
        setSupportActionBar(myToolbar);
        ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);
        ab.setIcon(ContextCompat.getDrawable(this,R.drawable.people));

        listView = findViewById(R.id.beneficiariesListview);
        addBeneficiaryButton = findViewById(R.id.addBeneficiaryButton);

        ispssManager = new ISPSSManager(this);
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
                                JSONArray data = response.getJSONArray("body");
                                Beneficiaries = new Beneficiary[data.length()];
                                for(int i = 0; i < data.length();i++) {
                                    Beneficiaries[i] = new Beneficiary(data.getJSONObject(i).getString("id"),
                                            data.getJSONObject(i).getString("firstName"),
                                            data.getJSONObject(i).getString("lastName"),
                                            Utils.getDateNoTime(data.getJSONObject(i).getString("dob")),
                                            data.getJSONObject(i).getString("phoneNumber"),
                                            data.getJSONObject(i).getString("relationshipId"),
                                            data.getJSONObject(i).getDouble("percentage"),
                                            data.getJSONObject(i).getString("gender"));
                                    Log.d("ben Dob", data.getJSONObject(i).getString("dob"));
                                }
                                listView.setAdapter(new BeneficiaryArrayAdapter(BeneficiariesActivity.this,
                                        R.layout.beneficiary_object_layout,Beneficiaries));
                                ispssManager.cancelDialog(dialogFragment);
                                return;
                            }
                            Toast.makeText(BeneficiariesActivity.this, "Failed getting beneficiaries", Toast.LENGTH_SHORT).show();
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

        addBeneficiaryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogFragment = new AddNewBeneficiaryDialog();
                ispssManager.showDialog(dialogFragment,"addNewBeneficiary");
            }
        });
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
                                JSONArray data = response.getJSONArray("body");
                                Beneficiaries = new Beneficiary[data.length()];
                                for(int i = 0; i < data.length();i++) {
                                    Beneficiaries[i] = new Beneficiary(data.getJSONObject(i).getString("id"),
                                            data.getJSONObject(i).getString("firstName"),
                                            data.getJSONObject(i).getString("lastName"),
                                            Utils.getDateNoTime(data.getJSONObject(i).getString("dob")),
                                            data.getJSONObject(i).getString("phoneNumber"),
                                            data.getJSONObject(i).getString("relationshipId"),
                                            data.getJSONObject(i).getDouble("percentage"),
                                            data.getJSONObject(i).getString("gender"));
                                }
                                listView.setAdapter(new BeneficiaryArrayAdapter(BeneficiariesActivity.this,
                                        R.layout.beneficiary_object_layout,Beneficiaries));
                                ispssManager.cancelDialog(dialogFragment);
                                return;
                            }
                            Toast.makeText(BeneficiariesActivity.this, "Failed getting beneficiaries", Toast.LENGTH_SHORT).show();
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