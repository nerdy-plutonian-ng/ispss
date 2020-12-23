package com.persol.ispss;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class TransactionsAdapter extends RecyclerView.Adapter<TransactionsAdapter.ViewHolder> {

    private ArrayList<Transaction> transactions;
    private Context context;

    public TransactionsAdapter(Context context,ArrayList<Transaction> transactions) {
        this.context = context;
        this.transactions = transactions;
    }

    @NonNull
    @Override
    public TransactionsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.transaction_layout,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TransactionsAdapter.ViewHolder holder, int position) {
        holder.typeTV.setText(transactions.get(position).getType());
        holder.amountTV.setText(Utils.formatMoney(transactions.get(position).getAmount()));
        holder.dateTV.setText(Utils.getHumanDate(transactions.get(position).getDate()));
        if(transactions.get(position).getType().equals("Withdrawal")){
            holder.typeTV.setTextColor(context.getResources().getColor(R.color.colorAccent));
        } else {
            holder.typeTV.setTextColor(context.getResources().getColor(R.color.colorPrimary));
        }
    }

    @Override
    public int getItemCount() {
        return transactions.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView typeTV, amountTV, dateTV;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            typeTV = itemView.findViewById(R.id.typeTV);
            amountTV = itemView.findViewById(R.id.amountTV);
            dateTV = itemView.findViewById(R.id.dateTV);
        }
    }
}
