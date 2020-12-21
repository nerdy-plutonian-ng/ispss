package com.persol.ispss;

import android.view.View;
import android.view.ViewGroup;

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
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull BatchPaymentsAdapter.ViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return payees.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }
}
