package com.persol.ispss;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import java.util.ArrayList;

public class CheckBeneficiariesDialog extends DialogFragment {

    private ArrayList<Beneficiary> allBeneficiaries,selectedBeneficiaries;
    private String[] availableBeneficiaries;

    public CheckBeneficiariesDialog(ArrayList<Beneficiary> allBeneficiaries) {
        this.allBeneficiaries = allBeneficiaries;
        this.selectedBeneficiaries = new ArrayList<>();
        availableBeneficiaries = new String[this.allBeneficiaries.size()];
        int count = 0;
        for(Beneficiary beneficiary : allBeneficiaries){
            availableBeneficiaries[count] = beneficiary.getFirstName() + " " + beneficiary.getLastName();
            count++;
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Set the dialog title
        builder.setTitle(R.string.select_beneficiaries)
                .setMultiChoiceItems(availableBeneficiaries, null,
                        new DialogInterface.OnMultiChoiceClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which,
                                                boolean isChecked) {
                                if (isChecked) {
                                    // If the user checked the item, add it to the selected items
                                    selectedBeneficiaries.add(allBeneficiaries.get(which));
                                } else if (selectedBeneficiaries.contains(selectedBeneficiaries.get(which))) {
                                    // Else, if the item is already in the array, remove it
                                    selectedBeneficiaries.remove(which);
                                }
                            }
                        })
                // Set the action buttons
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        // User clicked OK, so save the selectedItems results somewhere
                        // or return them to the component that opened the dialog
                    }
                })
                .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                    }
                });

        return builder.create();
    }
}


