package com.persol.ispss;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.Date;
import java.util.UUID;

import static com.persol.ispss.Constants.IDCardTypesGlobal;


public class IDCardFragment extends Fragment {

    private Spinner nationalIdSpinner;
    private TextInputLayout idTIL,nameOnCardTIL,expiryTIL;
    private TextInputEditText idEt,nameOnCardEt,expiryEt;
    private ISPSSManager ispssManager;


    public IDCardFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_i_d_card, container, false);

        ispssManager = new ISPSSManager(getActivity());
        nationalIdSpinner = view.findViewById(R.id.nationalIDSpinner);
        idEt = view.findViewById(R.id.nationalId_ET);
        idTIL = view.findViewById(R.id.nationalId_TIL);
        nameOnCardEt = view.findViewById(R.id.nameOnCard_ET);
        nameOnCardTIL = view.findViewById(R.id.nameOnCard_TIL);
        expiryEt = view.findViewById(R.id.cardExpiry_ET);
        expiryTIL = view.findViewById(R.id.cardExpiry_TIL);
        final ExtendedFloatingActionButton nextBtn = view.findViewById(R.id.nextButton);
        idEt.addTextChangedListener(new MyTextWatcher(idTIL));

        ArrayAdapter<String> idTypeArrayAdapter = new ArrayAdapter<>(getActivity(),android.R.layout.simple_list_item_1,ispssManager.getIdTypesNames(IDCardTypesGlobal));
        nationalIdSpinner.setAdapter(idTypeArrayAdapter);

        expiryTIL.setEndIconOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MyDatePicker myDatePicker = new MyDatePicker(getActivity(),expiryEt,expiryTIL);
                myDatePicker.showDialog();
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

        if(idEt.getText().toString().trim().isEmpty()){
            idTIL.setError(getString(R.string.empty_error));
            return false;
        }

        if(nameOnCardEt.getText().toString().trim().isEmpty()){
            nameOnCardTIL.setError(getString(R.string.empty_error));
            return false;
        }

        if(expiryEt.getText().toString().trim().isEmpty()){
            expiryTIL.setError(getString(R.string.invalid_date_error));
            expiryTIL.setErrorIconDrawable(null);
            return false;
        }



//        if(Utils.getISODate(User.dobDate) == null){
//            dobTIL.setError(getString(R.string.invalid_date_error));
//            return false;
//        }



        NewMember newMember = new NewMember();
        newMember.setNationalId(new IDCard(IDCardTypesGlobal.get(nationalIdSpinner.getSelectedItemPosition()).getId(),idEt.getText().toString().trim(),
                nameOnCardEt.getText().toString().trim(),new Date()));
        return true;

    }
}