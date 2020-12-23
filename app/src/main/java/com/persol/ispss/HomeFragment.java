package com.persol.ispss;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import static com.persol.ispss.Constants.CONTRIBUTORS_SCHEMES;
import static com.persol.ispss.Constants.DOMAIN;


public class HomeFragment extends Fragment {

    private ISPSSManager ispss_manager;
    private DialogFragment dialogFragment;
    private final int CONTRIBUTE_PAGE = 90;
    private final int WITHDRAW_PAGE = 95;


    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        ispss_manager = new ISPSSManager(getActivity());



        RecyclerView recyclerView = view.findViewById(R.id.menuRecycler);
        recyclerView.setHasFixedSize(true);
        // use a linear layout manager
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getActivity(),2);
//        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);
        // specify an adapter (see also next example)
        final ArrayList<Menu> menus = new ArrayList<>();
        menus.add(new Menu(1,"Add a Member",R.drawable.register_contributor));
        menus.add(new Menu(2,"Pay a Member",R.drawable.pay_contributor));

        if (ispss_manager.getUserType() == 1) {
            menus.add(new Menu(3,"Contribute",R.drawable.contribute));
            menus.add(new Menu(4,"Savings",R.drawable.savings));
            menus.add(new Menu(5,"Schemes",R.drawable.schemes));
            menus.add(new Menu(6,"Beneficiaries",R.drawable.beneficiaries));
            menus.add(new Menu(7,"Withdrawals",R.drawable.withdrawals));
            menus.add(new Menu(8,"History",R.drawable.pay_contributor));
        } else {
            menus.add(new Menu(3,"Join a Scheme",R.drawable.become_contributor));
            menus.add(new Menu(4,"History",R.drawable.pay_contributor));
        }
        MenuAdapter menuAdapter = new MenuAdapter(getActivity(),menus);
        menuAdapter.setListener(new MenuAdapter.Listener() {
            @Override
            public void onClick(int position) {
                final DialogFragment dialogFragment;
                Intent intent;
                switch (position){
                    case 0:
                        intent = new Intent(getActivity(),RegisterMemberActivity.class);
                        startActivity(intent);
                        break;
                    case 1:
                        intent = new Intent(getActivity(),PayActivity.class);
                        startActivity(intent);
                        break;
                    case 2:
                        if(ispss_manager.getUserType() == 0){
                            intent = new Intent(getActivity(),JoinSchemeActivity.class);
                            startActivity(intent);
                        } else {
                            showContributeWithdrawalDialog(WITHDRAW_PAGE);
                        }
                        break;
                    case 3:
                        if(ispss_manager.getUserType() == 0){
                            intent = new Intent(getActivity(),HistoryActivity.class);
                        } else {
                            intent = new Intent(getActivity(),SavingsConfigActivity.class);
                        }
                        startActivity(intent);
                        break;
                    case 4:
                        intent = new Intent(getActivity(),SchemeActivity.class);
                        startActivity(intent);
                        break;
                    case 5:
                        intent = new Intent(getActivity(),BeneficiaryActivity.class);
                        startActivity(intent);
                        break;
                    case 6:
                        showContributeWithdrawalDialog(WITHDRAW_PAGE);
                        break;
                    case 7:
                        intent = new Intent(getActivity(),HistoryActivity.class);
                        startActivity(intent);
                        break;
                }
            }
        });
        recyclerView.setAdapter(menuAdapter);
        return view;
    }

    private void showContributeWithdrawalDialog(final int page){
        dialogFragment = new Loader();
        ispss_manager.showDialog(dialogFragment,"loader");
        RequestQueue queue = Volley.newRequestQueue(getActivity());
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET,
                DOMAIN + CONTRIBUTORS_SCHEMES + ispss_manager.getContributorID(),
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            ArrayList<Scheme> schemeArrayList = new ArrayList<>();
                            if(response.getInt("code") == 0){
                                JSONObject body = response.getJSONObject("body");
                                JSONArray jsonArray = body.getJSONArray("schemes");
                                for(int i = 0;i < jsonArray.length();i++){
                                    JSONObject data = jsonArray.getJSONObject(i);
                                    Scheme scheme = new Scheme(data.getString("schemeId"),data.getString("schemeName"));
                                    schemeArrayList.add(scheme);
                                }
                                if(schemeArrayList.size() == 0){
                                    Toast.makeText(getActivity(), "You have not been registered to any scheme", Toast.LENGTH_LONG).show();
                                    ispss_manager.cancelDialog(dialogFragment);
                                    return;
                                }
                                ispss_manager.cancelDialog(dialogFragment);
                                String label;
                                if(page == CONTRIBUTE_PAGE){
                                    dialogFragment = new ContributeDialog(schemeArrayList);
                                    label = "contribute";
                                } else {
                                    dialogFragment = new WithdrawalDialog(schemeArrayList);
                                    label = "withdrawal";
                                }
                                ispss_manager.showDialog(dialogFragment,label);
                            }
                        } catch (JSONException e) {
                            ispss_manager.cancelDialog(dialogFragment);
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        ispss_manager.cancelDialog(dialogFragment);
                    }
                });
        queue.add(jsonObjectRequest);
    }
}