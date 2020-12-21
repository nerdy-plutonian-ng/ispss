package com.persol.ispss;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioGroup;

import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;


public class PersonalInfoFragment extends Fragment {

    private ISPSSManager ispssManager;
    private TextInputLayout fNAmeTIL,mNAmeTIL,lNAmeTIL,dobTIL,phoneTIL,emailTIL,residenceTIL,hometownTIL,occupationTIL;
    private TextInputEditText fNAmeEt,mNAmeEt,lNAmeEt,dobEt,phoneEt,emailEt,residenceET,hometownET,occupationET;
    private RadioGroup genderGroup;


    public PersonalInfoFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_personal_info, container, false);
        ExtendedFloatingActionButton nextBtn = view.findViewById(R.id.nextButton);

        fNAmeTIL = view.findViewById(R.id.fName_TIL);
        mNAmeTIL = view.findViewById(R.id.mName_TIL);
        lNAmeTIL = view.findViewById(R.id.lName_TIL);
        dobTIL = view.findViewById(R.id.dateOfBirth_TIL);
        phoneTIL = view.findViewById(R.id.phone_TIL);
        emailTIL = view.findViewById(R.id.email_TIL);
        residenceTIL = view.findViewById(R.id.residence_TIL);
        hometownTIL = view.findViewById(R.id.hometown_TIL);
        occupationTIL = view.findViewById(R.id.occupation_TIL);
        fNAmeEt = view.findViewById(R.id.fName_Et);
        mNAmeEt = view.findViewById(R.id.mName_Et);
        lNAmeEt = view.findViewById(R.id.lName_Et);
        dobEt = view.findViewById(R.id.dateOfBirth_ET);
        phoneEt = view.findViewById(R.id.phone_ET);
        emailEt = view.findViewById(R.id.email_Et);
        residenceET = view.findViewById(R.id.residence_ET);
        hometownET = view.findViewById(R.id.hometown_ET);
        occupationET = view.findViewById(R.id.occupation_ET);
        genderGroup = view.findViewById(R.id.genderGroup);
        fNAmeEt.addTextChangedListener(new MyTextWatcher(fNAmeTIL));
        mNAmeEt.addTextChangedListener(new MyTextWatcher(mNAmeTIL));
        lNAmeEt.addTextChangedListener(new MyTextWatcher(lNAmeTIL));
        dobEt.addTextChangedListener(new MyTextWatcher(dobTIL));
        phoneEt.addTextChangedListener(new MyTextWatcher(phoneTIL));
        emailEt.addTextChangedListener(new MyTextWatcher(emailTIL));
        dobEt.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if(b){
                    MyDatePicker myDatePicker = new MyDatePicker(getActivity(),dobEt);
                    myDatePicker.showDialog();
                }
            }
        });

        nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isReady()){
                    Fragment fragment = new SavingsConfigFragment();
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
        if(fNAmeEt.getText().toString().trim().isEmpty()){
            fNAmeTIL.setError(getString(R.string.empty_error));
            return false;
        }

        if(lNAmeEt.getText().toString().trim().isEmpty()){
            lNAmeTIL.setError(getString(R.string.empty_error));
            return false;
        }

        if(!emailEt.getText().toString().trim().isEmpty() && !Utils.isValidEmail(emailEt.getText().toString().trim())){
            emailTIL.setError(getString(R.string.invalid_email_error));
            return false;
        }

        if(dobEt.getText().toString().trim().isEmpty()){
            dobTIL.setError(getString(R.string.empty_error));
            return false;
        }

        if(phoneEt.getText().toString().trim().isEmpty()){
            phoneTIL.setError(getString(R.string.empty_error));
            return false;
        }

        if(Utils.getISODate(User.dobDate) == null){
            dobTIL.setError(getString(R.string.invalid_date_error));
            return false;
        }

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
        newMember.setFirstName(fNAmeEt.getText().toString().trim());
        newMember.setMiddleName(mNAmeEt.getText().toString().trim());
        newMember.setLastName(lNAmeEt.getText().toString().trim());
        newMember.setEmail(emailEt.getText().toString().trim());
        newMember.setDateOfBirth(User.dobDate);
        newMember.setPhone(phoneEt.getText().toString().trim());
        newMember.setGender(genderGroup.getCheckedRadioButtonId() == R.id.maleRadio ? "M" : "F");
        newMember.setResidence(residenceET.getText().toString().trim());
        newMember.setHometown(hometownET.getText().toString().trim());
        newMember.setOccupation(occupationET.getText().toString().trim());
        newMember.setMemberExisting(false);
        return true;

    }
}