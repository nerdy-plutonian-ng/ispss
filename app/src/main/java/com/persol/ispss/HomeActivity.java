package com.persol.ispss;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import static com.persol.ispss.Constants.BankAccountTypesGlobal;
import static com.persol.ispss.Constants.ISPSS;

public class HomeActivity extends AppCompatActivity {

    private ISPSSManager ispssManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        ispssManager = new ISPSSManager(this);

        TextView greetTv = findViewById(R.id.greetingTV);
        TextView nameTV = findViewById(R.id.nameTV);
        greetTv.setText(ispssManager.greet());
        nameTV.setText(ispssManager.getUserName());


        BottomNavigationView navigationView = findViewById(R.id.nav_view);
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupWithNavController(navigationView, navController);
        ispssManager = new ISPSSManager(this);
        Log.d(ISPSS, "UserID: "+ispssManager.getContributorID());
        if(BankAccountTypesGlobal.size() < 1){
            ispssManager.getGenericData();
        }



    }
}