package com.persol.ispss;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class SchemeBeneficiaryAdapter extends RecyclerView.Adapter<SchemeBeneficiaryAdapter.ViewHolder> {

    private ArrayList<Beneficiary> beneficiaries;

    public SchemeBeneficiaryAdapter(ArrayList<Beneficiary> beneficiaries) {
        this.beneficiaries = beneficiaries;
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
    public void onBindViewHolder(@NonNull SchemeBeneficiaryAdapter.ViewHolder holder, int position) {
        holder.nameTV.setText(beneficiaries.get(position).getFirstName()+ " " +beneficiaries.get(position).getLastName());
        holder.percentageET.setText(String.valueOf(beneficiaries.get(position).getPercentage()));
    }

    @Override
    public int getItemCount() {
        Log.d("ISPSS", "getItemCount: "+beneficiaries.size());
        return beneficiaries.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView nameTV;
        private EditText percentageET;
        private ImageView deleteTV;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            nameTV = itemView.findViewById(R.id.nameTV);
            percentageET = itemView.findViewById(R.id.percentageET);
            deleteTV = itemView.findViewById(R.id.delete_IV);
        }
    }
}
