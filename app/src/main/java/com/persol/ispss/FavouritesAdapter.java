package com.persol.ispss;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import static com.persol.ispss.Constants.DOMAIN;
import static com.persol.ispss.Constants.FavouritesGlobal;
import static com.persol.ispss.Constants.GET_FAVOURITES;
import static com.persol.ispss.Constants.ISPSS;

public class FavouritesAdapter extends RecyclerView.Adapter<FavouritesAdapter.ViewHolder> {

    private final ArrayList<Favourite> favouriteArrayList;
    final private Context context;
    private final ISPSSManager ispssManager;

    public FavouritesAdapter(ArrayList<Favourite> favouriteArrayList, Context context) {
        this.favouriteArrayList = favouriteArrayList;
        this.context = context;
        ispssManager = new ISPSSManager(this.context);
    }

    @NonNull
    @Override
    public FavouritesAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        // Inflate the custom layout
        View view = inflater.inflate(R.layout.favourite_card, parent, false);
        // Return a new holder instance
        return new FavouritesAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FavouritesAdapter.ViewHolder holder, final int position) {
        holder.nameTV.setText(favouriteArrayList.get(position).getName());
        holder.memberIdTV.setText(favouriteArrayList.get(position).getUserId());
        holder.deleteTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteFavourite(favouriteArrayList.get(position).getId(),position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return favouriteArrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView memberIdTV;
        private TextView nameTV;
        private ImageView deleteTV;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            memberIdTV = itemView.findViewById(R.id.memberIdTV);
            nameTV = itemView.findViewById(R.id.nameTV);
            deleteTV = itemView.findViewById(R.id.delete_IV);
        }
    }

    public void deleteFavourite(final String id, final int postion){
        final DialogFragment loader = new Loader();
        ispssManager.showDialog(loader,"loader");
        RequestQueue queue = Volley.newRequestQueue(context);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.DELETE,
                DOMAIN + GET_FAVOURITES + id,
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        ispssManager.cancelDialog(loader);
                        try {
                            if(response.getInt("code") == 0){
                                favouriteArrayList.remove(postion);
                                notifyItemRemoved(postion);
                                int counter = 0;
                                for(Favourite fav : FavouritesGlobal){
                                    if(fav.getId().equals(id)){
                                        break;
                                    }
                                    counter++;
                                }
                                FavouritesGlobal.remove(counter);
                                Toast.makeText(context, "Favourite removed", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(context, "Failed to remove favourite", Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            Toast.makeText(context, "Failed to remove favourite", Toast.LENGTH_SHORT).show();
                            Log.d(ISPSS, "onResponse: "+e.toString());
                            e.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                ispssManager.cancelDialog(loader);
                Log.d(ISPSS, "onErrorResponse: "+error.toString());
            }
        }
        );
        queue.add(jsonObjectRequest);
    }
}
