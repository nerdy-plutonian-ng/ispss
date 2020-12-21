package com.persol.ispss;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.ArrayList;

public class PayeeDialog extends DialogFragment {

    private ArrayList<Payee> payees;

    public PayeeDialog(ArrayList<Payee> payees) {
        this.payees = payees;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        final View view = inflater.inflate(R.layout.add_beneficiary_card, null);

        final TextInputLayout name_TIL = view.findViewById(R.id.name_TIL);
        final TextInputLayout phone_TIL = view.findViewById(R.id.phone_TIL);
        final TextInputLayout amount_TIL = view.findViewById(R.id.amount_TIL);
        final TextInputEditText name_ET = view.findViewById(R.id.name_ET);
        final TextInputEditText phone_ET = view.findViewById(R.id.phone_ET);
        final TextInputEditText amount_ET = view.findViewById(R.id.amount_ET);
        final MaterialButton addEditButton = view.findViewById(R.id.addEditPaymentButton);
        name_ET.addTextChangedListener(new MyTextWatcher(name_TIL));
        phone_ET.addTextChangedListener(new MyTextWatcher(phone_TIL));
        amount_ET.addTextChangedListener(new MyTextWatcher(amount_TIL));
        addEditButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(name_ET.getText().toString().isEmpty()){
                    name_TIL.setError(getString(R.string.empty_error));
                    return;
                }
                if(phone_ET.getText().toString().isEmpty()){
                    phone_TIL.setError(getString(R.string.empty_error));
                    return;
                }
                if(amount_ET.getText().toString().isEmpty()){
                    amount_TIL.setError(getString(R.string.empty_error));
                    return;
                }

                Payee payee = new Payee(payees.size()+1,name_ET.getText().toString().trim(),
                        phone_ET.getText().toString().trim(),Double.parseDouble(amount_ET.getText().toString().trim()));
                payees.add(payee);
                ((BatchPayActivity)getActivity()).notifyInsertion();
                PayeeDialog.this.getDialog().cancel();
            }
        });

        builder.setTitle(R.string.add_a_payee)
                .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        PayeeDialog.this.getDialog().cancel();
                    }
                })
                .setView(view);
        return builder.create();
    }
}
