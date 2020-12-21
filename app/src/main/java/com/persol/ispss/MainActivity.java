package com.persol.ispss;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;

public class MainActivity extends AppCompatActivity {

    private ExtendedFloatingActionButton startButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        startButton = findViewById(R.id.startButton);
        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                SharedPreferences sharedPref = MainActivity.this.getSharedPreferences(
                        getString(R.string.sharedPrefs), Context.MODE_PRIVATE);
                String name = sharedPref.getString(getString(R.string.name),null);
                if(name == null){
                    LoginRegisterModal loginRegisterModal = new
                            LoginRegisterModal();
                    loginRegisterModal.show(getSupportFragmentManager(),
                            "loginRegister");
                } else {
                    Intent intent = new Intent(MainActivity.this,HomeActivity.class);
                    startActivity(intent);
                }

            }
        });
    }
}