package com.persol.ispss;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.PopupMenu;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.io.Serializable;

public class SingleSchemeActivity extends AppCompatActivity {

    private TextInputLayout appr_TIL;
    private TextInputEditText appr_ET;
    private Class<? extends Serializable> scheme;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_scheme);

        scheme = getIntent().getSerializableExtra(getString(R.string.scheme)).getClass();

        appr_TIL = findViewById(R.id.appr_TIL);
        appr_ET = findViewById(R.id.appr_Et);

        appr_TIL.setEndIconOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                appr_ET.setEnabled(!appr_ET.isEnabled());
                if(appr_ET.isEnabled()){
                    appr_TIL.setEndIconDrawable(R.drawable.edit_primary);
                }
            }
        });

        PopupMenu popup = new PopupMenu(this,appr_ET);
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                return false;
            }
        });

    }
}