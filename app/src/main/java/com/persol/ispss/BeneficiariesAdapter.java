package com.persol.ispss;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import static com.persol.ispss.Constants.ISPSS;
import static com.persol.ispss.Constants.RelationshipsGlobal;

public class BeneficiariesAdapter extends RecyclerView.Adapter<BeneficiariesAdapter.ViewHolder> {

    private Context context;
    private ArrayList<Beneficiary> beneficiaries;
    private DialogFragment dialogFragment;
    private ISPSSManager ispssManager;

    public BeneficiariesAdapter(Context context, ArrayList<Beneficiary> beneficiaries) {
        this.context = context;
        this.beneficiaries = beneficiaries;
        ispssManager = new ISPSSManager(this.context);
    }

    @NonNull
    @Override
    public BeneficiariesAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.beneficiary_card,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final BeneficiariesAdapter.ViewHolder holder, final int position) {
        String fullName = beneficiaries.get(position).getFirstName() + " " + beneficiaries.get(position).getLastName();
        holder.nameTV.setText(fullName);
        String relationship = "";
        for(Relationship relationship1 : RelationshipsGlobal){
            if(beneficiaries.get(position).getRelationship().equals(relationship1.getId())){
                relationship = relationship1.getName();
                break;
            }
        }
        holder.relationshipTV.setText(relationship);
        holder.toggleIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(beneficiaries.get(position).isExpanded()){
                    holder.toggleIV.setImageResource(R.drawable.collapsed);
                    holder.listView.setVisibility(View.GONE);
                    beneficiaries.get(position).setExpanded(false);
                } else {
                    holder.toggleIV.setImageResource(R.drawable.expanded);
                    holder.listView.setVisibility(View.VISIBLE);
                    beneficiaries.get(position).setExpanded(true);
                }
            }
        });
        holder.editIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogFragment = new AddNewBeneficiaryDialog(position);
                ispssManager.showDialog(dialogFragment,"addNewBeneficiary");
            }
        });
        holder.deleteIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogFragment = new DeleteBeneficiaryDialog(context,beneficiaries.get(position).getId());
                ispssManager.showDialog(dialogFragment,"addNewBeneficiary");
            }
        });
    }

    @Override
    public int getItemCount() {
        return beneficiaries.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private ImageView toggleIV;
        private TextView nameTV;
        private TextView relationshipTV;
        private ImageView editIV;
        private ImageView deleteIV;
        private ListView listView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            toggleIV = itemView.findViewById(R.id.collapsedExpand_IV);
            nameTV = itemView.findViewById(R.id.nameTV);
            relationshipTV = itemView.findViewById(R.id.relationshipTV);
            editIV = itemView.findViewById(R.id.edit_IV);
            deleteIV = itemView.findViewById(R.id.delete_IV);
            listView = itemView.findViewById(R.id.listView);
        }
    }
}
