package com.persol.ispss;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.EditText;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;

import static com.persol.ispss.Constants.DOMAIN;
import static com.persol.ispss.Constants.HOME_EXTRA;
import static com.persol.ispss.Constants.ISPSS;
import static com.persol.ispss.Constants.LOGIN_CODE;

public class CodeActivity extends AppCompatActivity {

    private ISPSSManager ispss_manager;
    private EditText oneEt,twoEt,threeEt,fourEt;
    DialogFragment dialogFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_code);

        ispss_manager = new ISPSSManager(this);

        oneEt = findViewById(R.id.one);
        twoEt = findViewById(R.id.two);
        threeEt = findViewById(R.id.three);
        fourEt = findViewById(R.id.four);

        oneEt.addTextChangedListener(new MyTextWatcher(oneEt,twoEt));
        twoEt.addTextChangedListener(new MyTextWatcher(twoEt,threeEt));
        threeEt.addTextChangedListener(new MyTextWatcher(threeEt,fourEt));
        fourEt.addTextChangedListener(new MyTextWatcher(fourEt,fourEt));
    }
    private class MyTextWatcher implements TextWatcher {

        private EditText currentEdittext;
        private EditText nextEdittext;

        public MyTextWatcher(EditText currentEdittext,EditText nextEdittext) {
            this.currentEdittext = currentEdittext;
            this.nextEdittext = nextEdittext;
        }

        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void afterTextChanged(Editable editable) {
            nextEdittext.requestFocus();
            if((currentEdittext.getId() == R.id.four) && (nextEdittext.getId() == R.id.four)){
                 dialogFragment = new Loader();
                ispss_manager.showDialog(dialogFragment,"loader");
                sendCode();
            }
        }
    }
    private void sendCode(){
        try{
            String code = oneEt.getText().toString().trim() + twoEt.getText().toString().trim() +
                    threeEt.getText().toString().trim() + fourEt.getText().toString().trim();
            Log.d(ISPSS, "CODE: "+code+", messagebody: "+getIntent().getStringExtra("messageId"));
            RequestQueue queue = Volley.newRequestQueue(this);
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST,
                    DOMAIN + LOGIN_CODE+code+"/"+getIntent().getStringExtra("messageId"),
                    null,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try{
                                Log.d(ISPSS, "onResponse: "+response.toString());
                                if(response.getInt("code") == 0){
                                    SharedPreferences sharedPref = CodeActivity.this.getSharedPreferences(
                                            getString(R.string.sharedPrefs), Context.MODE_PRIVATE);
                                    JSONObject data = response.getJSONObject("body");
                                    JSONObject personal = data.getJSONObject("personal");
                                    JSONObject bank = data.getString("bank").contains("accountNumber") ? data.getJSONObject("bank") : null;
                                    JSONObject momo = data.getString("momo").contains("accountNumber") ? data.getJSONObject("momo") : null;
                                    SharedPreferences.Editor editor = sharedPref.edit();
                                    String fName = personal.getString("firstName");
                                    String mName = personal.getString("middleName");
                                    String lName = personal.getString("lastName");
                                    String fullName = fName +" "+ (mName.isEmpty() ? mName : mName+" ") + lName;
                                    editor.putString(getString(R.string.name), fullName);
                                    editor.putString(getString(R.string.phone),personal.getString("phoneNumber"));
                                    editor.putString(getString(R.string.email),personal.getString("emailAddress").isEmpty() ? "Not set" : personal.getString("emailAddress"));
                                    editor.putString(getString(R.string.residence),personal.getString("residence"));
                                    editor.putString(getString(R.string.user_id),getIntent().getStringExtra(getString(R.string.user_id)));
                                    editor.putFloat(getString(R.string.mmc), (float) data.getJSONObject("fundSetup").getDouble("mmc"));
                                    editor.putLong(getString(R.string.last_sync),new Date().getTime());
                                    editor.putString(getString(R.string.momo),momo != null ?  momo.toString() : "");
                                    editor.putString(getString(R.string.bank),bank != null ?  bank.toString() : "");
                                    editor.apply();
                                    Intent intent = new Intent(CodeActivity.this,HomeActivity.class);
                                    intent.putExtra(HOME_EXTRA,false);
                                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    startActivity(intent);
                                }
                            } catch (Exception e){
                                Log.e("error", e.toString() );
                                ispss_manager.cancelDialog(dialogFragment);
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {

                        }
                    }
            );
            queue.add(jsonObjectRequest);
        }catch (Exception e){

        }

    }
}

