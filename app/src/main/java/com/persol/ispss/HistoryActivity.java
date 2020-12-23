package com.persol.ispss;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Date;
import java.util.UUID;

public class HistoryActivity extends AppCompatActivity {

    private RecyclerView transactionsRecycler;
    private ArrayList<Transaction> transactions;

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

        transactionsRecycler = findViewById(R.id.historyRecycler);

        transactions = new ArrayList<>();
        transactions.add(new Transaction(UUID.randomUUID().toString(),"Withdrawal",200.00,new Date()));
        transactions.add(new Transaction(UUID.randomUUID().toString(),"Contribution",100.00,new Date()));
        transactions.add(new Transaction(UUID.randomUUID().toString(),"Withdrawal",50.00,new Date()));
        transactions.add(new Transaction(UUID.randomUUID().toString(),"Contribution",150.00,new Date()));
        transactions.add(new Transaction(UUID.randomUUID().toString(),"Contribution",500.00,new Date()));

        transactionsRecycler.setHasFixedSize(true);
        TransactionsAdapter transactionsAdapter = new TransactionsAdapter(this,transactions);
        transactionsRecycler.setAdapter(transactionsAdapter);

    }
}