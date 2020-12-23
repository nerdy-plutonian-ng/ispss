package com.persol.ispss;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class BatchPaymentsAdapter extends RecyclerView.Adapter<BatchPaymentsAdapter.ViewHolder> {

    private final ArrayList<Payee> payees;

    public BatchPaymentsAdapter(ArrayList<Payee> payees) {
        this.payees = payees;
    }

    @NonNull
    @Override
    public BatchPaymentsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.payee_layout,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BatchPaymentsAdapter.ViewHolder holder, int position) {
        holder.nameTV.setText(payees.get(position).getName());
        holder.phoneTV.setText(payees.get(position).getPhoneNumber());
        holder.amountET.setText(Utils.formatMoney(payees.get(position).getAmount()));
    }

    @Override
    public int getItemCount() {
        return payees.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView nameTV, phoneTV;
        private EditText amountET;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            nameTV = itemView.findViewById(R.id.nameTV);
            phoneTV = itemView.findViewById(R.id.phone_TV);
            amountET = itemView.findViewById(R.id.amount_ET);
        }
    }
}
