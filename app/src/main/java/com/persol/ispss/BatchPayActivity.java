package com.persol.ispss;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;

public class BatchPayActivity extends AppCompatActivity {

    private ArrayList<Payee> payees;
    private BatchPaymentsAdapter adapter;
    private RecyclerView recyclerView;
    private ISPSSManager ispssManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_batch_pay);

        //toolbar
        Toolbar myToolbar = findViewById(R.id.batchPaymentsAppBar);
        setSupportActionBar(myToolbar);
        ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);
        ab.setIcon(ContextCompat.getDrawable(this,R.drawable.payments));

        recyclerView = findViewById(R.id.payeeRecycler);

        ispssManager = new ISPSSManager(this);
        payees = new ArrayList<>();
        adapter = new BatchPaymentsAdapter(payees);

        recyclerView.setHasFixedSize(false);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);

        updateTotal();

        MaterialButton button = findViewById(R.id.editButton);
        button.setOnClickListener(new Butt());


    }

    public void notifyInsertion(){
        recyclerView.getAdapter().notifyItemInserted(payees.size());
        updateTotal();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.batch_pay_menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.add:
                DialogFragment dialogFragment = new PayeeDialog(payees);
                ispssManager.showDialog(dialogFragment,"payeedialog");
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }

    private void updateTotal(){
        TextView totalTV = findViewById(R.id.totalTV);
        double total = 0.00;


        for (Payee payee : payees){
            total += payee.getAmount();
        }
        totalTV.setText(Utils.formatMoney(total));

    }


    class Butt implements View.OnClickListener{

        @Override
        public void onClick(View view) {

        }
    }
}