package com.persol.ispss;

import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;

import java.util.ArrayList;


public class BeneficiariesFragment extends Fragment {

    private static ArrayList<Beneficiary> beneficiaryArrayList;
    private ExtendedFloatingActionButton nextButton;
    private ImageButton addButton;
    private DialogFragment dialogFragment;
    private ISPSSManager ispssManager;
    private ListView beneficiariesListview;
    private ConstraintLayout emptyCL;

    public BeneficiariesFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_beneficiaries, container, false);

        addButton = view.findViewById(R.id.addBeneficiaryButton);
        nextButton = view.findViewById(R.id.nextButton);
        beneficiariesListview = view.findViewById(R.id.beneficiariesListview);
        emptyCL = view.findViewById(R.id.emptyCL);

        ispssManager = new ISPSSManager(getActivity());

        beneficiaryArrayList = new ArrayList<>();

        if(beneficiaryArrayList.size() == 0){
            beneficiariesListview.setVisibility(View.GONE);
            emptyCL.setVisibility(View.VISIBLE);
        } else {
            emptyCL.setVisibility(View.GONE);
            ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(getActivity(),android.R.layout.simple_list_item_1,ispssManager.getBeneficiariesNames(beneficiaryArrayList));
            beneficiariesListview.setAdapter(arrayAdapter);
            beneficiariesListview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    Toast.makeText(getActivity(), beneficiaryArrayList.get(i).getFirstName(), Toast.LENGTH_SHORT).show();
                }
            });
            beneficiariesListview.setVisibility(View.VISIBLE);
        }

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogFragment = new AddBeneficiaryDialog(beneficiariesListview,beneficiaryArrayList,emptyCL);
                ispssManager.showDialog(dialogFragment,"addBeneficiary");
            }
        });

        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isReady()){
                    Fragment fragment = new AddSchemeFragment();
                    FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
                    fragmentTransaction.replace(R.id.fragmentHost, fragment);
                    fragmentTransaction.addToBackStack(null);
                    fragmentTransaction.commit();
                }

            }
        });

        return view;
    }

    private boolean isReady(){
        if( beneficiaryArrayList.size() < 1){
            Toast.makeText(getActivity(), "Add at least a beneficiary", Toast.LENGTH_SHORT).show();
            return false;
        }

        NewMember newMember = new NewMember();
        newMember.setBeneficiaries(beneficiaryArrayList);
        return true;
    }

    @Override
    public void onResume() {
        super.onResume();
        final NewMember newMember = new NewMember();
        if(newMember.getBeneficiaries().size() > 0){
            emptyCL.setVisibility(View.GONE);
            beneficiaryArrayList = newMember.getBeneficiaries();
            ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(getActivity(),android.R.layout.simple_list_item_1,ispssManager.getBeneficiariesNames(beneficiaryArrayList));
            beneficiariesListview.setAdapter(arrayAdapter);
            beneficiariesListview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    Toast.makeText(getActivity(), beneficiaryArrayList.get(i).getFirstName(), Toast.LENGTH_SHORT).show();
                }
            });
            beneficiariesListview.setVisibility(View.VISIBLE);
        }
    }
}