package com.persol.ispss;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import io.github.douglasjunior.androidSimpleTooltip.SimpleTooltip;

import static com.persol.ispss.Constants.APP_ID_TEST;
import static com.persol.ispss.Constants.APP_KEY_TEST;
import static com.persol.ispss.Constants.DOMAIN;
import static com.persol.ispss.Constants.EMERGENT_REDIRECT;
import static com.persol.ispss.Constants.GENERATE_PAYMENT_INVOICE;
import static com.persol.ispss.Constants.GET_A_MEMBER;
import static com.persol.ispss.Constants.GET_MEMBER_GENERIC;
import static com.persol.ispss.Constants.GET_MEMBER_NO_FILTER;
import static com.persol.ispss.Constants.ISPSS;
import static com.persol.ispss.Constants.PAY_URL_TEST;

public class PayActivity extends AppCompatActivity {

    private ImageView favListBtn;
    private ISPSSManager ispss_manager;
    private DialogFragment dialogFragment;
    private boolean faved = false;
    private String searchedMemberId = "";
    private String searchedMemberName = "";
    private TextInputLayout memberID_TIL;
    private TextInputLayout memberName_TIL;
    private AutoCompleteTextView memberID_Et;
    private TextInputEditText memberName_Et;
    private String[] favNames;
    private ArrayList<Favourite> favouriteArrayList;
    private ImageView faveBtn;
    private CheckBox momoCheckbox;
    private TextInputLayout amount_TIL;
    private TextInputLayout description_TIL;
    private TextInputLayout momo_TIL;
    private TextInputEditText amount_Et;
    private TextInputEditText description_Et;
    private TextInputEditText momo_Et;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pay);

        //toolbar
        Toolbar myToolbar = findViewById(R.id.payMemberAppBar);
        setSupportActionBar(myToolbar);
        ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);
        ab.setIcon(ContextCompat.getDrawable(this,R.drawable.payments));

        ispss_manager = new ISPSSManager(this);

        ExtendedFloatingActionButton saveBtn = findViewById(R.id.payBtn);
        favListBtn = findViewById(R.id.favoriteBook_IV);
        faveBtn = findViewById(R.id.favorite_IV);
        memberName_TIL = findViewById(R.id.memberName_TIL);
        memberName_Et = findViewById(R.id.memberName_ET);
        momoCheckbox = findViewById(R.id.momoCheckbox);
        amount_TIL = findViewById(R.id.amount_TIL);
        description_TIL = findViewById(R.id.description_TIL);
        momo_TIL = findViewById(R.id.momo_TIL);
        amount_Et = findViewById(R.id.amount_ET);
        description_Et = findViewById(R.id.description_ET);
        momo_Et = findViewById(R.id.momo_Et);
        memberID_Et = findViewById(R.id.memberID_ET);
        memberID_TIL = findViewById(R.id.memberID_TIL);

        dialogFragment = new Loader();

        showTips();

        favouriteArrayList = ispss_manager.getFavourites();
        favNames = new String[favouriteArrayList.size()];
        for(int i = 0; i < favouriteArrayList.size();i++){
            favNames[i] = favouriteArrayList.get(i).getName() + " - " + favouriteArrayList.get(i).getId();
            Log.d(ISPSS, "fav: "+favouriteArrayList.get(i).getName());
        }
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1,favNames);

        memberID_Et.setAdapter(arrayAdapter);
        memberID_Et.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                memberID_Et.setText(favouriteArrayList.get(i).getId());
            }
        });

        memberID_Et.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if(editable.toString().length() == 7 || editable.toString().length() == 10){
                    getMemberName(memberID_Et.getText().toString());
                } else {
                    memberName_Et.getText().clear();
                    faveBtn.setImageResource(R.drawable.star_grey);
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



    }

    private void showTips() {
        new SimpleTooltip.Builder(this)
                .backgroundColor(Color.GREEN)
                .arrowColor(Color.GREEN)
                .anchorView(favListBtn)
                .text("Open" +
                        "\nfavourites")
                .gravity(Gravity.START)
                .animated(true)
                .onDismissListener(new SimpleTooltip.OnDismissListener() {
                    @Override
                    public void onDismiss(SimpleTooltip tooltip) {
                        new SimpleTooltip.Builder(PayActivity.this)
                                .backgroundColor(Color.GREEN)
                                .arrowColor(Color.GREEN)
                                .anchorView(faveBtn)
                                .text("Add to" +
                                        "\nfavourites")
                                .gravity(Gravity.START)
                                .animated(true)
                                .onDismissListener(new SimpleTooltip.OnDismissListener() {
                                    @Override
                                    public void onDismiss(SimpleTooltip tooltip) {
                                        ispss_manager.setFavFirstTime();
                                    }
                                })
                                .build()
                                .show();
                    }
                })
                .build()
                .show();
    }

    public void showFavouriteList(View view) {
        if(favNames.length < 1){
            Toast.makeText(this, "You have no favourites yet", Toast.LENGTH_SHORT).show();
            return;
        }
        memberID_Et.showDropDown();
    }

    private void generateInvoice(final JSONArray dataArray){
        try {
            RequestQueue queue = Volley.newRequestQueue(this);
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
                                Toast.makeText(PayActivity.this, "Failed to initiate payment", Toast.LENGTH_SHORT).show();
                                e.printStackTrace();
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Log.e("error",error.toString());
                            ispss_manager.cancelDialog(dialogFragment);
                            Toast.makeText(PayActivity.this, "Failed to initiate payment", Toast.LENGTH_SHORT).show();
                        }
                    });
            queue.add(jsonObjectRequest);
        } catch (Exception e){
            Log.e("error",e.toString());
            ispss_manager.cancelDialog(dialogFragment);
            Toast.makeText(PayActivity.this, "Failed to initiate payment", Toast.LENGTH_SHORT).show();
        }
    }

    private void goPayOnline(JSONObject data){
        RequestQueue queue = Volley.newRequestQueue(PayActivity.this);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST,
                PAY_URL_TEST,
                data,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.e("test", response.toString() );
                        ispss_manager.cancelDialog(dialogFragment);
                        try {
                            Intent intent1 = new Intent(PayActivity.this,WebViewPaymentActivity.class);
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
                        Toast.makeText(PayActivity.this, "Failed to initiate payment", Toast.LENGTH_SHORT).show();
                        Log.e("test", error.toString() );
                    }
                });
        queue.add(jsonObjectRequest);
    }

    private void getMemberName(String userId){
        RequestQueue queue = Volley.newRequestQueue(PayActivity.this);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET,
                DOMAIN+GET_MEMBER_GENERIC+userId,
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.e("test", response.toString() );
                        try {
                            if(response.getInt("code") == 0){
                                JSONObject body = response.getJSONArray("body").getJSONObject(0);
                                String fName = body.getString("firstName");
                                String mName = body.getString("middleName");
                                String lName = body.getString("lastName");
                                searchedMemberName = fName +" "+ (mName.isEmpty() ? mName : mName+" ") + lName;
                                searchedMemberId = body.getString("memberID");
                                memberName_Et.setText(searchedMemberName);
                                memberName_Et.setTextColor(Color.parseColor("#75a302"));
                                setFaveDefaults();
                            } else {
                                memberName_Et.getText().clear();
                                searchedMemberId = "";
                                searchedMemberName = "";
                                faved = false;
                                faveBtn.setImageResource(R.drawable.star_grey);
                            }
                        } catch (JSONException e) {
                            memberName_Et.getText().clear();
                            searchedMemberId = "";
                            searchedMemberName = "";
                            faved = false;
                            faveBtn.setImageResource(R.drawable.star_grey);
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        memberName_Et.getText().clear();
                        searchedMemberId = "";
                        searchedMemberName = "";
                        faved = false;
                        faveBtn.setImageResource(R.drawable.star_grey);
                    }
                });
        queue.add(jsonObjectRequest);
    }

    private void setFaveDefaults(){
        int result = ispss_manager.getFavouriteIndex(searchedMemberId);
        if(result == -1){
            faveBtn.setImageResource(R.drawable.star_grey);
            faved = false;
        } else {
            faveBtn.setImageResource(R.drawable.star);
            faved = true;
        }
    }

    public void makeFavourite(View view) {
        if(memberName_Et.getText().toString().isEmpty() || memberName_Et.getText().toString().equals(getString(R.string.no_such_user)) || memberName_Et.getText().toString().equals(getString(R.string.enter_valid_user))){
            Toast.makeText(this, "You must search a member first", Toast.LENGTH_SHORT).show();
            return;
        }
        if(faved){
            if(ispss_manager.deleteFavourite(searchedMemberId)){
                faveBtn.setImageResource(R.drawable.star_grey);
                faved = false;
                Toast.makeText(this, "Member is no longer a favourite", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Failed removing member from favourites", Toast.LENGTH_SHORT).show();
            }

        } else {
            if(ispss_manager.addToFavourites(new Favourite(searchedMemberId,searchedMemberName))){
                faveBtn.setImageResource(R.drawable.star);
                faved = true;
                Toast.makeText(this, "You made this member a favourite", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Error adding member to favourites", Toast.LENGTH_SHORT).show();
            }

        }
    }

    public void pay(View view) {
        if(searchedMemberId.isEmpty()){
            memberID_TIL.setError(getString(R.string.verify_member_error));
            memberName_Et.setText("");
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

//        if(payeeNameTV.getText().toString().trim().isEmpty() ||
//                payeeNameTV.getText().toString().trim().equals(getString(R.string.no_such_user)) ||
//                payeeNameTV.getText().toString().trim().equals(getString(R.string.enter_valid_user)) ){
//            payeeNameTV.setText(R.string.enter_valid_user);
//            return;
//        }

        try{
            ispss_manager.showDialog(dialogFragment,"loader");
            JSONObject invoiceData = new JSONObject();
            invoiceData.put("payeeMemberID",searchedMemberId);
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
            Toast.makeText(this, "Failed to initiate payment", Toast.LENGTH_SHORT).show();
            Log.e("test", e.toString() );
        }
    }
}