package com.persol.ispss;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONException;
import org.json.JSONObject;

import static com.persol.ispss.Constants.DOMAIN;
import static com.persol.ispss.Constants.LOGIN;
import static com.persol.ispss.Constants.UPDATE_MMC;

public class SavingsConfigActivity extends AppCompatActivity {
    private TextInputLayout mmc_TIL;
    private TextInputEditText mmc_ET;
    private MaterialButton editButton,viewFundButton,changeFundMediumButton;
    private boolean isMMC_TIL_enabled = false;
    private DialogFragment dialogFragment;
    private ISPSSManager ispssManager;
    private String TAG = "debug";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_savings_config);

        //toolbar
        Toolbar myToolbar = findViewById(R.id.savingsAppBar);
        setSupportActionBar(myToolbar);
        ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);
        ab.setIcon(ContextCompat.getDrawable(this,R.drawable.edit));

        mmc_TIL = findViewById(R.id.maxContribution_TIL);
        mmc_ET = findViewById(R.id.maxContribution_ET);
        editButton = findViewById(R.id.editButton);
        viewFundButton = findViewById(R.id.viewSavingsSettingsButton);
        changeFundMediumButton = findViewById(R.id.changeFundMediumButton);

        ispssManager = new ISPSSManager(this);

        mmc_ET.setText(Utils.formatMoney(ispssManager.getMMC()));

    }

    public void editMMC(View view) {

        if(mmc_ET.getText().toString().trim().isEmpty()){
            mmc_TIL.setError(getString(R.string.empty_error));
            return;
        }

        if(isMMC_TIL_enabled){
            dialogFragment = new Loader();
            ispssManager.showDialog(dialogFragment,"loader");
            try{
                JSONObject data = new JSONObject();
                data.put("memberID",ispssManager.getContributorID());
                data.put("mmc",Utils.removeFormatting(mmc_ET.getText().toString().trim()));
                Log.d(TAG, "editMMC: "+data.toString());
                RequestQueue queue = Volley.newRequestQueue(this);
                JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.PUT,
                        DOMAIN + UPDATE_MMC,
                        data,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                try {
                                    if(response.getInt("code") == 0){
                                        Toast.makeText(SavingsConfigActivity.this, "Update successful", Toast.LENGTH_LONG).show();
                                        mmc_ET.setEnabled(false);
                                        isMMC_TIL_enabled = false;
                                        mmc_ET.setCompoundDrawablesWithIntrinsicBounds(null, null, ContextCompat.getDrawable(SavingsConfigActivity.this,R.drawable.locked), null);
                                    } else {
                                        Toast.makeText(SavingsConfigActivity.this, "Update Failed", Toast.LENGTH_SHORT).show();
                                    }
                                    ispssManager.cancelDialog(dialogFragment);
                                } catch (JSONException e) {
                                    Log.e("error",e.toString());
                                    Toast.makeText(SavingsConfigActivity.this, "Update Failed", Toast.LENGTH_SHORT).show();
                                    ispssManager.cancelDialog(dialogFragment);
                                }
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Log.e("error",error.toString());
                                Toast.makeText(SavingsConfigActivity.this, "Update Failed", Toast.LENGTH_SHORT).show();
                                ispssManager.cancelDialog(dialogFragment);
                            }
                        }
                );
                queue.add(jsonObjectRequest);
            } catch (Exception e){

            }

        } else {
            mmc_ET.setEnabled(true);
            isMMC_TIL_enabled = true;
            mmc_ET.setCompoundDrawablesWithIntrinsicBounds(null, null, ContextCompat.getDrawable(this,R.drawable.unlocked), null);
        }
    }

    public void viewFundDetails(View view) {
        if(ispssManager.getFundMedium().contains(getString(R.string.momo))){
            dialogFragment = new EditMomoDialog();
        }
        if(ispssManager.getFundMedium().contains(getString(R.string.bank))){
            dialogFragment = new EditBankDetailsDialog();
        }
        ispssManager.showDialog(dialogFragment,"editFundMedium");
    }

    public void changeFundDetails(View view) {
    }
}