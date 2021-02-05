package com.persol.ispss;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

public class AdditionalInfoFragment extends Fragment {

    private TextInputLayout residenceTIL,hometownTIL,occupationTIL;
    private TextInputEditText residenceET,hometownET,occupationET;

    public AdditionalInfoFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_additional_info, container, false);

        residenceET = view.findViewById(R.id.residence_ET);
        hometownET = view.findViewById(R.id.hometown_ET);
        occupationET = view.findViewById(R.id.occupation_ET);
        residenceTIL = view.findViewById(R.id.residence_TIL);
        hometownTIL = view.findViewById(R.id.hometown_TIL);
        occupationTIL = view.findViewById(R.id.occupation_TIL);
        residenceET.addTextChangedListener(new MyTextWatcher(residenceTIL));
        hometownET.addTextChangedListener(new MyTextWatcher(hometownTIL));
        occupationET.addTextChangedListener(new MyTextWatcher(occupationTIL));
        ExtendedFloatingActionButton nextBtn = view.findViewById(R.id.nextButton);

        nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isReady()){
                    Fragment fragment = new IDCardFragment();
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

        if(residenceET.getText().toString().trim().isEmpty()){
            residenceTIL.setError(getString(R.string.empty_error));
            return false;
        }

        if(hometownET.getText().toString().trim().isEmpty()){
            hometownTIL.setError(getString(R.string.empty_error));
            return false;
        }

        if(occupationET.getText().toString().trim().isEmpty()){
            occupationTIL.setError(getString(R.string.empty_error));
            return false;
        }

        NewMember newMember = new NewMember();
        newMember.setResidence(residenceET.getText().toString().trim());
        newMember.setHometown(hometownET.getText().toString().trim());
        newMember.setOccupation(occupationET.getText().toString().trim());
        newMember.setMemberExisting(true);
        return true;

    }
}