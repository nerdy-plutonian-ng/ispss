package com.persol.ispss;

import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.PopupMenu;
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
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import static com.persol.ispss.Constants.Beneficiaries;
import static com.persol.ispss.Constants.DOMAIN;
import static com.persol.ispss.Constants.ISPSS;
import static com.persol.ispss.Constants.REMOVE_BENEFICIARY;

public class BeneficiaryArrayAdapter extends ArrayAdapter<Beneficiary> {

    private Beneficiary[] beneficiaries;
    private Context context;
    private DialogFragment dialogFragment;
    private ISPSSManager ispssManager;

    public BeneficiaryArrayAdapter(@NonNull Context context, int resource, @NonNull Beneficiary[] beneficiaries) {
        super(context, resource, beneficiaries);
        this.beneficiaries = beneficiaries;
        this.context = context;
        ispssManager = new ISPSSManager(context);
    }

    @Override
    public int getCount() {
        return beneficiaries.length;
    }

    @Nullable
    @Override
    public Beneficiary getItem(int position) {
        return beneficiaries[position];
    }

    @NonNull
    @Override
    public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if(convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.beneficiary_object_layout, parent, false);
        }
        final View view = convertView;
        TextView nameTV = view.findViewById(R.id.nameTV);
        TextView percentageTV = view.findViewById(R.id.percentTV);
        ImageView moreIV = view.findViewById(R.id.moreIV);
        String benName = beneficiaries[position].getFirstName() + " " + beneficiaries[position].getLastName();
        nameTV.setText(benName);
        percentageTV.setText(Utils.formatMoney(beneficiaries[position].getPercentage()));
        moreIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popup = new PopupMenu(context, view, Gravity.END);
                popup.getMenuInflater().inflate(R.menu.beneficiaries_menu, popup.getMenu());
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {
                        switch (menuItem.getItemId()){
                            case R.id.edit:
                                dialogFragment = new AddNewBeneficiaryDialog(position);
                                ispssManager.showDialog(dialogFragment,"addNewBeneficiary");
                                break;
                            case R.id.remove:
                                removeBeneficiary(Beneficiaries[position].getId());
                                break;
                        }
                        return false;
                    }

                });
                popup.show();
            }
        });
        return view;
    }

    private void removeBeneficiary(String id){
        Log.d(ISPSS, "removeBeneficiaryId: "+id);
        Log.d(ISPSS, "removeBeneficiary: url "+DOMAIN + REMOVE_BENEFICIARY + id);
        dialogFragment = new Loader();
        ispssManager.showDialog(dialogFragment,"loader");
        RequestQueue queue = Volley.newRequestQueue(context);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.DELETE,
                DOMAIN + REMOVE_BENEFICIARY + id,
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d(ISPSS, "onResponse: "+response.toString());
                        ispssManager.cancelDialog(dialogFragment);
                        try {
                            if(response.getInt("code") == 0){
                                 ((BeneficiariesActivity)context).refreshBeneficiary();
                                Toast.makeText(context, "Successfully removed beneficiary", Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            ispssManager.cancelDialog(dialogFragment);
                            Toast.makeText(context, "Failed to remove beneficiary. Try again", Toast.LENGTH_SHORT).show();
                            Log.d(ISPSS, "onResponse: "+e.toString());
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        ispssManager.cancelDialog(dialogFragment);
                        Log.d(ISPSS, "onErrorResponse: "+error.toString());
                        Toast.makeText(context, "Failed to remove beneficiary. Try again", Toast.LENGTH_SHORT).show();
                    }
                });
        queue.add(jsonObjectRequest);
    }


}