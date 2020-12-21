package com.persol.ispss;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ListView;

public class FavouritesActivity extends AppCompatActivity {

    private ISPSSManager ispssManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favourites);

        ListView favourite = findViewById(R.id.favouritesListview);

    }
}