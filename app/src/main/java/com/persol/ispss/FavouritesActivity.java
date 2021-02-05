package com.persol.ispss;

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
import android.widget.ListView;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import static com.persol.ispss.Constants.DOMAIN;
import static com.persol.ispss.Constants.GET_FAVOURITES;
import static com.persol.ispss.Constants.ISPSS;
import static com.persol.ispss.Constants.UPDATE_SCHEME_BENEFICIARIES;

public class FavouritesActivity extends AppCompatActivity {

    private ISPSSManager ispssManager;
    private RecyclerView recyclerView;
    private ArrayList<Favourite> favouriteArrayList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favourites);

        //toolbar
        Toolbar myToolbar = findViewById(R.id.favouritesAppBar);
        setSupportActionBar(myToolbar);
        ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);
        ab.setIcon(ContextCompat.getDrawable(this,R.drawable.favorite_white));

        ispssManager = new ISPSSManager(this);
        favouriteArrayList = new ArrayList<>();
        recyclerView = findViewById(R.id.favouritesRecyclerview);
        recyclerView.setHasFixedSize(false);
        // use a linear layout manager
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        getFavourites();

    }

    public void getFavourites(){
        final DialogFragment loader = new Loader();
        ispssManager.showDialog(loader,"loader");
        RequestQueue queue = Volley.newRequestQueue(this);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET,
                DOMAIN + GET_FAVOURITES + ispssManager.getContributorID(),
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            if(response.getInt("code") == 0){
                                JSONArray favArray = response.getJSONArray("body");
                                for(int i = 0; i < favArray.length();i++){
                                    JSONObject fav = favArray.getJSONObject(i);
                                    favouriteArrayList.add(new Favourite(fav.getString("id"),
                                            fav.getJSONObject("favourite").getString("memberID"),
                                            fav.getJSONObject("favourite").getString("firstName") + " " +
                                                    (fav.getJSONObject("favourite").getString("middleName").isEmpty() ? "" : fav.getJSONObject("favourite").getString("middleName") + " ") +
                                                    fav.getJSONObject("favourite").getString("lastName")));
                                    FavouritesAdapter favouritesAdapter = new FavouritesAdapter(favouriteArrayList,FavouritesActivity.this);
                                    recyclerView.setAdapter(favouritesAdapter);
                                }
                            } else {
                                Toast.makeText(FavouritesActivity.this, "no", Toast.LENGTH_SHORT).show();
                            }
                            ispssManager.cancelDialog(loader);
                        } catch (Exception e){
                            ispssManager.cancelDialog(loader);
                            Log.d(ISPSS, "onResponse: "+e.toString());
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
        );
        queue.add(jsonObjectRequest);
    }

    public void deleteFavourite(String id){
        final DialogFragment loader = new Loader();
        ispssManager.showDialog(loader,"loader");
        RequestQueue queue = Volley.newRequestQueue(this);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.DELETE,
                DOMAIN + GET_FAVOURITES + id,
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            if(response.getInt("code") == 0){

                            }
                            ispssManager.cancelDialog(loader);
                        } catch (JSONException e) {
                            ispssManager.cancelDialog(loader);
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        ispssManager.cancelDialog(loader);
                    }
                }
        );
        queue.add(jsonObjectRequest);
    }
}