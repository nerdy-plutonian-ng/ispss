package com.persol.ispss;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.DialogFragment;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.github.ybq.android.spinkit.sprite.Sprite;
import com.github.ybq.android.spinkit.style.Wave;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONObject;

import java.util.ArrayList;

import static com.persol.ispss.Constants.ADD_SCHEME;
import static com.persol.ispss.Constants.DOMAIN;
import static com.persol.ispss.Constants.ISPSS;

public class AddSchemeDialog extends DialogFragment {

    private ArrayList<Scheme> schemeArrayList,registeredSchemes,availableSchemes;
    private DialogFragment dialogFragment;
    private ISPSSManager ispssManager;

    public AddSchemeDialog(ArrayList<Scheme> schemeArrayList,ArrayList<Scheme> registeredSchemes) {
        this.schemeArrayList = schemeArrayList;
        this.registeredSchemes = registeredSchemes;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Get the layout inflater
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        final View view = inflater.inflate(R.layout.add_scheme_layout, null);
        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        final Spinner schemesSpinner = view.findViewById(R.id.schemesSpinner);
        final MaterialButton addSchemeBtn = view.findViewById(R.id.addSchemeBtn);
        final TextInputLayout percentageTIL = view.findViewById(R.id.percentageTIL);
        final TextInputEditText percentageEt = view.findViewById(R.id.percentageEt);

        ispssManager = new ISPSSManager(getActivity());

        percentageEt.addTextChangedListener(new MyTextWatcher(percentageTIL));
        availableSchemes = new ArrayList<>();
        ////////////
        for(Scheme scheme:schemeArrayList){
            Log.e("scheme",scheme.getName());
        }
        for(Scheme scheme:registeredSchemes){
            Log.e("regScheme",scheme.getName());
        }
        //////////////
        for(Scheme scheme : schemeArrayList){
            boolean isContained = false;
            for(Scheme regScheme : registeredSchemes){
                if(scheme.isEqualTo(regScheme)){
                    isContained = true;
                    break;
                }
            }
            if(!isContained){
                availableSchemes.add(scheme);
            }
        }

        for(Scheme scheme:availableSchemes){
            Log.e("availableScheme",scheme.getName()+" "+scheme.getId());
        }

        ArrayAdapter<Scheme> schemeArrayAdapter = new ArrayAdapter<>(getActivity(),android.R.layout.simple_list_item_1,availableSchemes);
        schemesSpinner.setAdapter(schemeArrayAdapter);


        addSchemeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(percentageEt.getText().toString().trim().isEmpty()){
                    percentageTIL.setError(getString(R.string.empty_error));
                    return;
                }

                    try {
                        SharedPreferences sharedPref = getActivity().getSharedPreferences(
                                getString(R.string.sharedPrefs), Context.MODE_PRIVATE);
                        String contributorId = sharedPref.getString(getString(R.string.user_id), "");
                        JSONObject data =  new JSONObject();
                        data.put("memberID",contributorId);
                        data.put("schemeId",availableSchemes.get(schemesSpinner.getSelectedItemPosition()).getId());
                        data.put("appr",Double.parseDouble(percentageEt.getText().toString().trim()));
                        Log.e("schemeChosen",availableSchemes.get(schemesSpinner.getSelectedItemPosition()).getName() + " is No "+schemesSpinner.getSelectedItemPosition());
                        dialogFragment = new Loader();
                        ispssManager.showDialog(dialogFragment,"loader");
                        RequestQueue queue = Volley.newRequestQueue(getActivity());
                        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST,
                                DOMAIN + ADD_SCHEME,
                                data,
                                new Response.Listener<JSONObject>() {
                                    @Override
                                    public void onResponse(JSONObject response) {
                                        try{
                                            if(response.getInt("code") == 0){
                                                Toast.makeText(getActivity(), "Registered to fund successfully", Toast.LENGTH_LONG).show();
                                                ispssManager.cancelDialog(dialogFragment);
                                                AddSchemeDialog.this.getDialog().cancel();
                                                return;
                                            }
                                            ispssManager.cancelDialog(dialogFragment);
                                            Toast.makeText(getActivity(), "Registration failed", Toast.LENGTH_LONG).show();
                                        } catch (Exception e){
                                            ispssManager.cancelDialog(dialogFragment);
                                            Toast.makeText(getActivity(), "Registration failed", Toast.LENGTH_LONG).show();
                                            Log.d(ISPSS, "onResponse: "+e.toString());
                                        }

                                    }
                                },
                                new Response.ErrorListener() {
                                    @Override
                                    public void onErrorResponse(VolleyError error) {
                                        ispssManager.cancelDialog(dialogFragment);
                                        Log.e("addSchemeError",error.toString());
                                        Toast.makeText(getActivity(), "Registration failed", Toast.LENGTH_LONG).show();
                                    }
                                });
                        queue.add(jsonObjectRequest);
                    } catch (Exception e){
                        Log.e(ISPSS, "onClick: "+e.toString() );
                        AddSchemeDialog.this.getDialog().cancel();
                    }


            }
        });

        builder.setTitle("Add a Scheme")
                .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        AddSchemeDialog.this.getDialog().cancel();
                    }
                })
                .setView(view);
        return builder.create();
    }

    @Override
    public void onCancel(@NonNull DialogInterface dialog) {
        super.onCancel(dialog);
        ((SchemeActivity)getActivity()).refreshPage();
    }
}
