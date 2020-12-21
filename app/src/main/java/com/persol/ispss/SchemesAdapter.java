package com.persol.ispss;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;

import java.util.ArrayList;

public class SchemesAdapter extends RecyclerView.Adapter<SchemesAdapter.MyViewHolder> {

private ArrayList<Scheme> schemes;
    private SchemesAdapter.Listener listener;
    interface Listener {
        void onClick(int position);
    }

public SchemesAdapter(ArrayList<Scheme> schemes){
        this.schemes = schemes;
        }

@NonNull
@Override
public SchemesAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        MaterialCardView v = (MaterialCardView) LayoutInflater.from(parent.getContext())
        .inflate(R.layout.scheme_object_card, parent, false);
        return new SchemesAdapter.MyViewHolder(v);
        }

@Override
public void onBindViewHolder(@NonNull SchemesAdapter.MyViewHolder holder, final int position) {
        holder.schemeNameTv.setText(schemes.get(position).getName());
        holder.percentageTv.setText(Utils.formatMoney(schemes.get(position).getPercentage()));
        holder.savingsTv.setText(Utils.formatMoney(schemes.get(position).getSavings()));
        holder.startDateTv.setText(Utils.getHumanDate(schemes.get(position).getStartDate()));
        holder.schemeCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onClick(position);
                }
        }
    });
        }

@Override
public int getItemCount() {
        return schemes.size();
        }

public static class MyViewHolder extends RecyclerView.ViewHolder {

    private TextView schemeNameTv;
    private TextView percentageTv;
    private TextView savingsTv;
    private TextView startDateTv;
    private MaterialCardView schemeCard;

    public MyViewHolder(@NonNull View itemView) {
        super(itemView);
        schemeCard = itemView.findViewById(R.id.schemeCard);
        schemeNameTv = itemView.findViewById(R.id.schemeName);
        percentageTv = itemView.findViewById(R.id.percentTV);
        savingsTv = itemView.findViewById(R.id.savingsTV);
        startDateTv = itemView.findViewById(R.id.startTV);
    }
}

    public void setListener(SchemesAdapter.Listener listener){
        this.listener = listener;
    }
}
