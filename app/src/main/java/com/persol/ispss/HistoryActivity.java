package com.persol.ispss;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class HistoryActivity extends AppCompatActivity {

    private ListView transactionsListview;
    private String[] names = {"John","Mark","Susan","Pete","Irene","Mark","Susan","Pete","Irene","Mark","Susan","Pete","Irene","Mark","Susan","Pete","Irene"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        //toolbar
        Toolbar myToolbar = findViewById(R.id.historyAppBar);
        setSupportActionBar(myToolbar);
        ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);
        ab.setIcon(ContextCompat.getDrawable(this,R.drawable.history));

        transactionsListview = findViewById(R.id.transactionsListviews);
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1,names);
        transactionsListview.setAdapter(arrayAdapter);
    }
}