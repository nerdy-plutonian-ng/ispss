package com.persol.ispss;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONObject;

import static com.persol.ispss.Constants.DOMAIN;
import static com.persol.ispss.Constants.REGISTER;

public class RegisterUserActivity extends AppCompatActivity {

    private ISPSSManager ispssManager;
    private TextInputLayout fNAmeTIL,mNAmeTIL,lNAmeTIL,dobTIL,phoneTIL,emailTIL;
    private TextInputEditText fNAmeEt,mNAmeEt,lNAmeEt,dobEt,phoneEt,emailEt;
    private RadioGroup genderGroup;
    private CoordinatorLayout coordinatorLayout;
    private DialogFragment dialogFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_user);

        //toolbar
        Toolbar myToolbar = findViewById(R.id.registerAppBar);
        setSupportActionBar(myToolbar);
        ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);
        ab.setIcon(ContextCompat.getDrawable(this,R.drawable.register_user));

        ispssManager = new ISPSSManager(this);

        fNAmeTIL = findViewById(R.id.fName_TIL);
        mNAmeTIL = findViewById(R.id.mName_TIL);
        lNAmeTIL = findViewById(R.id.lName_TIL);
        dobTIL = findViewById(R.id.dateOfBirth_TIL);
        phoneTIL = findViewById(R.id.phone_TIL);
        emailTIL = findViewById(R.id.email_TIL);
        fNAmeEt = findViewById(R.id.fName_Et);
        mNAmeEt = findViewById(R.id.mName_Et);
        lNAmeEt = findViewById(R.id.lName_Et);
        dobEt = findViewById(R.id.dateOfBirth_ET);
        phoneEt = findViewById(R.id.phone_ET);
        emailEt = findViewById(R.id.email_Et);
        genderGroup = findViewById(R.id.genderGroup);
        coordinatorLayout = findViewById(R.id.registerUser_Coor);
        fNAmeEt.addTextChangedListener(new MyTextWatcher(fNAmeTIL));
        mNAmeEt.addTextChangedListener(new MyTextWatcher(mNAmeTIL));
        lNAmeEt.addTextChangedListener(new MyTextWatcher(lNAmeTIL));
        dobEt.addTextChangedListener(new MyTextWatcher(dobTIL));
        phoneEt.addTextChangedListener(new MyTextWatcher(phoneTIL));
        emailEt.addTextChangedListener(new MyTextWatcher(emailTIL));
        dobEt.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if(b){
                    MyDatePicker myDatePicker = new MyDatePicker(RegisterUserActivity.this,dobEt);
                    myDatePicker.showDialog();
                }
            }
        });

    }

    public void registerUser(View view) {
        if(fNAmeEt.getText().toString().trim().isEmpty()){
            fNAmeTIL.setError(getString(R.string.empty_error));
            return;
        }

        if(lNAmeEt.getText().toString().trim().isEmpty()){
            lNAmeTIL.setError(getString(R.string.empty_error));
            return;
        }

        if(!emailEt.getText().toString().trim().isEmpty() && !Utils.isValidEmail(emailEt.getText().toString().trim())){
            emailTIL.setError(getString(R.string.invalid_email_error));
            return;
        }

        if(dobEt.getText().toString().trim().isEmpty()){
            dobTIL.setError(getString(R.string.empty_error));
            return;
        }

        if(phoneEt.getText().toString().trim().isEmpty()){
            phoneTIL.setError(getString(R.string.empty_error));
            return;
        }


        if(Utils.getISODate(User.dobDate) == null){
            dobTIL.setError(getString(R.string.invalid_date_error));
            return;
        }

        dialogFragment = new Loader();
        ispssManager.showDialog(dialogFragment,"loader");
        User user = new User(fNAmeEt.getText().toString().trim(),
                mNAmeEt.getText().toString().trim(),
                lNAmeEt.getText().toString().trim(),
                Utils.getISODate(User.dobDate),
                phoneEt.getText().toString().trim(),
                emailEt.getText().toString().trim(),
                genderGroup.getCheckedRadioButtonId() == R.id.maleRadio ? "M" : "F"
        );
        Log.e("userObj",user.getNewUserObject().toString());
        RequestQueue queue = Volley.newRequestQueue(this);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST,
                DOMAIN + REGISTER,
                user.getNewUserObject(),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.e("response",response.toString());
                        ispssManager.cancelDialog(dialogFragment);
                        try{
                            if(response.getInt("code") == 0){
                                JSONObject body = response.getJSONObject("body");
                                Toast.makeText(RegisterUserActivity.this, R.string.user_registration_successful, Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(RegisterUserActivity.this,CodeActivity.class);;
                                intent.putExtra(getString(R.string.user_id),body.getString("memberID"));
                                intent.putExtra(getString(R.string.messageid),body.getString("messageId"));
                                startActivity(intent);
                            } else {
                                Snackbar.make(coordinatorLayout,"Failed creating user. Check data provided",Snackbar.LENGTH_LONG).show();
                            }
                        } catch (Exception e){
                            Log.e("error", e.toString());
                            Snackbar.make(coordinatorLayout,"Failed creating user",Snackbar.LENGTH_LONG).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("response",error.toString());
                        ispssManager.cancelDialog(dialogFragment);
                        Snackbar.make(coordinatorLayout,"Failed creating user",Snackbar.LENGTH_LONG).show();

                    }
                });
        queue.add(jsonObjectRequest);
    }



}