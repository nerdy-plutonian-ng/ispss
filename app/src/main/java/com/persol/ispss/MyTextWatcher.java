package com.persol.ispss;

import android.text.Editable;
import android.text.TextWatcher;

import com.google.android.material.textfield.TextInputLayout;

public class MyTextWatcher implements TextWatcher {

    private TextInputLayout textInputLayout;

    public MyTextWatcher( TextInputLayout textInputLayout) {
        this.textInputLayout = textInputLayout;
    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        textInputLayout.setError("");
    }

    @Override
    public void afterTextChanged(Editable editable) {
    }
}
