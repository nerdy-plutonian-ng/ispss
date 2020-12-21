package com.persol.ispss;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.DialogFragment;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.ArrayList;
import java.util.Date;
import java.util.UUID;

import static com.persol.ispss.Constants.BankAccountTypesGlobal;
import static com.persol.ispss.Constants.RelationshipsGlobal;

public class AddBeneficiaryDialog extends DialogFragment {

    private TextInputLayout fNameTIL,lNameTIL,dobTIL,phoneTIL,percentageTIL;
    private TextInputEditText fNameET,lNameET,dobET,phoneET,percentageET;
    private Spinner relationsSpinner;
    private MaterialButton saveBtn;
    private ListView listView;
    private ArrayList<Beneficiary> beneficiaryArrayList;
    private ISPSSManager ispssManager;
    private ConstraintLayout emptyCL;
    private RadioGroup genderGroup;

    public AddBeneficiaryDialog(ListView listView, ArrayList<Beneficiary> beneficiaryArrayList, ConstraintLayout emptyCL) {
        this.listView = listView;
        this.beneficiaryArrayList = beneficiaryArrayList;
        this.emptyCL = emptyCL;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        final View view = inflater.inflate(R.layout.add_beneficiary_card, null);

        fNameTIL = view.findViewById(R.id.fnameTIL);
        lNameTIL = view.findViewById(R.id.lnameTIL);
        dobTIL = view.findViewById(R.id.DOB_TIL);
        phoneTIL = view.findViewById(R.id.phone_TIL);
        percentageTIL = view.findViewById(R.id.percentageTIL);
        fNameET = view.findViewById(R.id.fName_Et);
        lNameET = view.findViewById(R.id.lName_Et);
        dobET = view.findViewById(R.id.DOB_Et);
        phoneET = view.findViewById(R.id.phone_Et);
        percentageET = view.findViewById(R.id.percentageEt);
        saveBtn = view.findViewById(R.id.saveBtn);
        relationsSpinner = view.findViewById(R.id.relationshipSpinner);
        genderGroup = view.findViewById(R.id.genderGroup);

        ispssManager = new ISPSSManager(getActivity());

        fNameET.addTextChangedListener(new MyTextWatcher(fNameTIL));
        lNameET.addTextChangedListener(new MyTextWatcher(lNameTIL));
        phoneET.addTextChangedListener(new MyTextWatcher(phoneTIL));
        percentageET.addTextChangedListener(new MyTextWatcher(percentageTIL));

        dobET.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if(b){
                    MyDatePicker myDatePicker = new MyDatePicker(getActivity(),dobET);
                    myDatePicker.showDialog();
                }
            }
        });

        ArrayAdapter<String> accountTypeArrayAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item,
                ispssManager.getRelationshipNames(RelationshipsGlobal));
        accountTypeArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        relationsSpinner.setAdapter(accountTypeArrayAdapter);

        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(fNameET.getText().toString().trim().isEmpty()){
                    fNameTIL.setError(getString(R.string.empty_error));
                    return;
                }

                if(lNameET.getText().toString().trim().isEmpty()){
                    lNameTIL.setError(getString(R.string.empty_error));
                    return;
                }

                if(dobET.getText().toString().trim().isEmpty()){
                    dobTIL.setError(getString(R.string.empty_error));
                    return;
                }

                if(phoneET.getText().toString().trim().isEmpty()){
                    phoneTIL.setError(getString(R.string.empty_error));
                    return;
                }

                if(percentageET.getText().toString().trim().isEmpty()){
                    percentageTIL.setError(getString(R.string.empty_error));
                    return;
                }

                Beneficiary beneficiary = new Beneficiary(fNameET.getText().toString().trim(),
                        lNameET.getText().toString().trim(),User.dobDate,phoneET.getText().toString().trim(),
                        RelationshipsGlobal.get(relationsSpinner.getSelectedItemPosition()).getId(),Double.parseDouble(percentageET.getText().toString().trim()),
                        genderGroup.getCheckedRadioButtonId() == R.id.maleRadio ? "Male" : "Female");
                beneficiaryArrayList.add(beneficiary);
                AddBeneficiaryDialog.this.getDialog().cancel();

            }
        });

        builder.setTitle(R.string.add_a_beneficiary)
                .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        AddBeneficiaryDialog.this.getDialog().cancel();
                    }
                })
                .setView(view);
        return builder.create();

    }

    @Override
    public void onCancel(@NonNull DialogInterface dialog) {
        super.onCancel(dialog);
        if(beneficiaryArrayList.size() > 0){
            ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(getActivity(),android.R.layout.simple_list_item_1,ispssManager.getBeneficiariesNames(beneficiaryArrayList));
            listView.setAdapter(arrayAdapter);
            emptyCL.setVisibility(View.GONE);
            listView.setVisibility(View.VISIBLE);
        }

    }

    private void gett(int x){
        if (x > 1){
            System.out.println("f");
            return;
        }
    }
}
