package com.persol.ispss;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.ArrayList;

import static com.persol.ispss.Constants.ISPSS;

public class SchemeBeneficiaryAdapter extends RecyclerView.Adapter<SchemeBeneficiaryAdapter.ViewHolder> {

    private ArrayList<Beneficiary> beneficiaries;
    private Context context;

    public SchemeBeneficiaryAdapter(ArrayList<Beneficiary> beneficiaries, Context context) {
        this.beneficiaries = beneficiaries;
        this.context = context;
        Log.d(ISPSS, "SchemeBeneficiaryAdapter: "+beneficiaries.size() + " beneficiaries");
    }

    @NonNull
    @Override
    public SchemeBeneficiaryAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        // Inflate the custom layout
        View view = inflater.inflate(R.layout.beneficiary_on_schemes, parent, false);
        // Return a new holder instance
        return new SchemeBeneficiaryAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final SchemeBeneficiaryAdapter.ViewHolder holder, final int position) {
        holder.nameTV.setText(beneficiaries.get(position).getFirstName()+ " " +beneficiaries.get(position).getLastName());
        holder.percentageET.setText(Utils.formatMoney(beneficiaries.get(position).getPercentage()));
        holder.percentageTIL.setEndIconOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                holder.percentageET.setEnabled(!holder.percentageET.isEnabled());
                if(holder.percentageET.isEnabled()){
                    holder.percentageTIL.setEndIconDrawable(R.drawable.edit_primary);
                } else {
                    beneficiaries.get(position).setPercentage(Double.parseDouble(holder.percentageET.getText().toString().trim()));
                    ((SingleSchemeActivity)context).setNewSchemeBens(beneficiaries);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        Log.d("ISPSS", "getItemCount: "+beneficiaries.size());
        return beneficiaries.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView nameTV;
        private TextInputLayout percentageTIL;
        private TextInputEditText percentageET;
        private ImageView deleteTV;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            nameTV = itemView.findViewById(R.id.nameTV);
            percentageTIL = itemView.findViewById(R.id.percentageTIL);
            percentageET = itemView.findViewById(R.id.percentageET);
            deleteTV = itemView.findViewById(R.id.delete_IV);
        }
    }
}
