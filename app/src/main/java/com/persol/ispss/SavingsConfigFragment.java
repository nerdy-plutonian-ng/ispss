package com.persol.ispss;

import android.icu.util.IslamicCalendar;
import android.os.Bundle;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.UUID;

import static com.persol.ispss.Constants.BankAccountTypesGlobal;
import static com.persol.ispss.Constants.NetworkProvidersGlobal;

public class SavingsConfigFragment extends Fragment {

    private ConstraintLayout momoCL;
    private ConstraintLayout bankCL;
    private Spinner providerSpinner;
    private Spinner bankAccountTypeSpinner;
    private TextInputLayout momoNumber_TIL;
    private TextInputLayout momoOwner_TIL;
    private TextInputLayout bankAccountNumber_TIL;
    private TextInputLayout bankAccountName_TIL;
    private TextInputLayout bankName_TIL;
    private TextInputLayout bankBranch_TIL;
    private TextInputEditText momoNumber_ET;
    private TextInputEditText momoOwner_ET;
    private TextInputEditText bankAccountNumber_ET;
    private TextInputEditText bankAccountName_ET;
    private TextInputEditText bankName_ET;
    private TextInputEditText bankBranch_ET;
    private RadioGroup fundMediumGroup;
    private ISPSSManager ispssManager;


    public SavingsConfigFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_savings_config, container, false);

        ispssManager = new ISPSSManager(getActivity());

        ExtendedFloatingActionButton nextBtn = view.findViewById(R.id.nextButton);
        fundMediumGroup = view.findViewById(R.id.mediumRadioGroup);
        momoCL = view.findViewById(R.id.momoCL);
        bankCL = view.findViewById(R.id.bankCL);
        providerSpinner = view.findViewById(R.id.providersSpinner);
        bankAccountTypeSpinner = view.findViewById(R.id.bankAccountTypeSpinner);
        momoNumber_TIL = view.findViewById(R.id.momo_TIL);
        momoOwner_TIL = view.findViewById(R.id.momo_owner_TIL);
        bankAccountNumber_TIL = view.findViewById(R.id.bankAccountNumber_TIL);
        bankAccountName_TIL = view.findViewById(R.id.bankAccountName_TIL);
        bankName_TIL = view.findViewById(R.id.bankName_TIL);
        bankBranch_TIL = view.findViewById(R.id.bankBranch_TIL);
        momoNumber_ET = view.findViewById(R.id.momo_Et);
        momoOwner_ET = view.findViewById(R.id.momo_owner_Et);
        bankAccountNumber_ET = view.findViewById(R.id.bankAccountNo_Et);
        bankAccountName_ET = view.findViewById(R.id.bankAccountName_Et);
        bankName_ET = view.findViewById(R.id.bankName_Et);
        bankBranch_ET = view.findViewById(R.id.bankBranch_Et);
        ArrayAdapter<String> providersArrayAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item,
                ispssManager.getProviderNames(NetworkProvidersGlobal));
        providersArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        providerSpinner.setAdapter(providersArrayAdapter);


        fundMediumGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                switch (i){
                    case R.id.momoRadio:
                        bankCL.setVisibility(View.GONE);
                        momoCL.setVisibility(View.VISIBLE);
                        ArrayAdapter<String> providersArrayAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item,
                                ispssManager.getProviderNames(NetworkProvidersGlobal));
                        providersArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        providerSpinner.setAdapter(providersArrayAdapter);
                        bankAccountTypeSpinner.setAdapter(null);
                        break;
                    case R.id.bankRadio:
                        momoCL.setVisibility(View.GONE);
                        bankCL.setVisibility(View.VISIBLE);
                        ArrayAdapter<String> accountTypeArrayAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item,
                                ispssManager.getAccountTypesNames(BankAccountTypesGlobal));
                        accountTypeArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        providerSpinner.setAdapter(accountTypeArrayAdapter);
                        providerSpinner.setAdapter(null);
                        break;
                }
            }
        });



        nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isReady()){
                    Fragment fragment = new BeneficiariesFragment();
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
        NewMember newMember = new NewMember();
        if(fundMediumGroup.getCheckedRadioButtonId() == R.id.momoRadio){
            if(momoNumber_ET.getText().toString().trim().isEmpty()){
                momoNumber_TIL.setError(getString(R.string.empty_error));
                return false;
            }
            if(momoOwner_ET.getText().toString().trim().isEmpty()){
                momoOwner_TIL.setError(getString(R.string.empty_error));
                return false;
            }
            newMember.setMomoAccount(new MomoAccount(NetworkProvidersGlobal.get(providerSpinner.getSelectedItemPosition()).getId(),
                    momoNumber_ET.getText().toString().trim(),momoOwner_ET.getText().toString().trim()));
            newMember.setBankAccount(null);
        } else if(fundMediumGroup.getCheckedRadioButtonId() == R.id.bankRadio){
            if(bankAccountNumber_ET.getText().toString().trim().isEmpty()){
                bankAccountNumber_TIL.setError(getString(R.string.empty_error));
                return false;
            }
            if(bankAccountName_ET.getText().toString().trim().isEmpty()){
                bankAccountName_TIL.setError(getString(R.string.empty_error));
                return false;
            }
            if(bankName_ET.getText().toString().trim().isEmpty()){
                bankName_TIL.setError(getString(R.string.empty_error));
                return false;
            }
            if(bankBranch_ET.getText().toString().trim().isEmpty()){
                bankBranch_TIL.setError(getString(R.string.empty_error));
                return false;
            }
            newMember.setBankAccount(new BankAccount(bankAccountNumber_ET.getText().toString().trim(),
                    bankAccountName_ET.getText().toString().trim(), bankName_ET.getText().toString().trim(),
                    bankBranch_ET.getText().toString().trim(),BankAccountTypesGlobal.get(bankAccountTypeSpinner.getSelectedItemPosition()).getId()));
            newMember.setMomoAccount(null);
        }
        return true;
    }
}