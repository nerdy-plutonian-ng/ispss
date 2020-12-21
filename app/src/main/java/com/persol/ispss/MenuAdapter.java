package com.persol.ispss;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;

import java.util.ArrayList;

public class MenuAdapter extends RecyclerView.Adapter<MenuAdapter.MyViewHolder> {

    private Context context;
    private ArrayList<Menu> menus;
    private Listener listener;
    interface Listener {
        void onClick(int position);
    }

    public MenuAdapter(Context context,ArrayList<Menu> menus) {
        this.menus = menus;
        this.context = context;
    }

    @NonNull
    @Override
    public MenuAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        // Inflate the custom layout
        View photoView = inflater.inflate(R.layout.menu_item, parent, false);
        // Return a new holder instance
        return new MyViewHolder(photoView);
    }

    @Override
    public void onBindViewHolder(@NonNull MenuAdapter.MyViewHolder holder,final int position) {
        holder.textView.setText(menus.get(position).getMenuTitle());
        holder.imageView.setImageDrawable(context.getResources().getDrawable(menus.get(position).getDrawableId()));
        holder.cardView.setOnClickListener(new View.OnClickListener() {
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
        return menus.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        public ImageView imageView;
        public TextView textView;
        public MaterialCardView cardView;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            cardView = itemView.findViewById(R.id.card);
            imageView = itemView.findViewById(R.id.menuImageIv);
            textView = itemView.findViewById(R.id.menuTitleTv);
        }
    }

    public void setListener(Listener listener){
        this.listener = listener;
    }
}
