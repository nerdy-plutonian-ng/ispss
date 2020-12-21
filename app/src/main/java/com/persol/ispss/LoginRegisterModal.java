package com.persol.ispss;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;

import static com.persol.ispss.Constants.DOMAIN;
import static com.persol.ispss.Constants.LOGIN;

public class LoginRegisterModal extends BottomSheetDialogFragment {

    private ISPSSManager ispssManager;
    private DialogFragment dialogFragment;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.login_register_layout, container,
                false);

        TextInputLayout userID_TIL = view.findViewById(R.id.userID_TIL);
        final TextInputEditText userID_ET = view.findViewById(R.id.userID_ET);
        TextView registerTV = view.findViewById(R.id.registerUser_TV);
        ispssManager = new ISPSSManager(getActivity());
        dialogFragment = new Loader();

        userID_TIL.setEndIconOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String userID = userID_ET.getText().toString().trim();
                if(userID.isEmpty()){
                    Toast.makeText(getActivity(), "This field can not be empty", Toast.LENGTH_SHORT).show();
                    return;
                }
                ispssManager.showDialog(dialogFragment,"loader");
                RequestQueue queue = Volley.newRequestQueue(getActivity());
                JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST,
                        DOMAIN + LOGIN + userID,
                        null,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                try {
                                    if(response.getInt("code") == 0){
                                        SharedPreferences sharedPref = getActivity().getSharedPreferences(
                                                getString(R.string.sharedPrefs), Context.MODE_PRIVATE);
                                        ispssManager.cancelDialog(dialogFragment);
                                        Intent intent = new Intent(getActivity(),CodeActivity.class);
                                        LoginRegisterModal.this.getDialog().cancel();
                                        intent.putExtra(getString(R.string.user_id),userID);
                                        intent.putExtra(getString(R.string.messageid),response.getString("body"));
                                        startActivity(intent);
                                    } else {
                                        Toast.makeText(getActivity(), "No such user", Toast.LENGTH_SHORT).show();
                                        ispssManager.cancelDialog(dialogFragment);
                                    }
                                } catch (JSONException e) {
                                    Log.e("error",e.toString());
                                    Toast.makeText(getActivity(), "Error logging in", Toast.LENGTH_SHORT).show();
                                    ispssManager.cancelDialog(dialogFragment);
                                    e.printStackTrace();
                                }
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Log.e("error",error.toString());
                                Toast.makeText(getActivity(), "Failed logging you in", Toast.LENGTH_SHORT).show();
                                ispssManager.cancelDialog(dialogFragment);
                            }
                        }
                );
                queue.add(jsonObjectRequest);



            }
        });


//        userID_ET.setOnKeyListener(new View.OnKeyListener() {
//            @Override
//            public boolean onKey(View view, int i, KeyEvent keyEvent) {
//                if (keyEvent.getAction() == KeyEvent.ACTION_DOWN && i == KeyEvent.KEYCODE_ENTER) {
//
//                }
//                return false;
//            }
//        });
        registerTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(),RegisterUserActivity.class);
                startActivity(intent);
            }
        });

        return view;
    }
}
