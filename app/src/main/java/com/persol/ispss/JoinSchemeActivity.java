package com.persol.ispss;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;

import static com.persol.ispss.Constants.BankAccountTypesGlobal;
import static com.persol.ispss.Constants.CONTRIBUTORS_SCHEMES;
import static com.persol.ispss.Constants.DOMAIN;
import static com.persol.ispss.Constants.GET_ALL_RELATIONSHIPS;
import static com.persol.ispss.Constants.GET_ALL_SCHEMES;
import static com.persol.ispss.Constants.GET_BANK_ACCOUNT_TYPES;
import static com.persol.ispss.Constants.GET_NETWORK_PROVIDERS;
import static com.persol.ispss.Constants.NetworkProvidersGlobal;
import static com.persol.ispss.Constants.RelationshipsGlobal;
import static com.persol.ispss.Constants.SchemesGlobal;

public class JoinSchemeActivity extends AppCompatActivity {

    private ISPSSManager ispssManager;
    private DialogFragment dialogFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join_scheme);

        //toolbar
        Toolbar myToolbar = findViewById(R.id.joinSchemeAppBar);
        setSupportActionBar(myToolbar);
        ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);
        ab.setIcon(ContextCompat.getDrawable(this,R.drawable.register_user));

        ispssManager =  new ISPSSManager(this);

        setUpFragments();

    }

    public void setUpFragments(){
        FragmentManager fragmentManager = getSupportFragmentManager();
        final FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        Fragment fragment = new AdditionalInfoFragment();
        fragmentTransaction.add(R.id.fragmentHost, fragment);
        fragmentTransaction.commit();
    }
}