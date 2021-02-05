package com.persol.ispss;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.webkit.WebView;
import android.widget.ProgressBar;

import static com.persol.ispss.Constants.HOME_EXTRA;
import static com.persol.ispss.Constants.MEMBERID;
import static com.persol.ispss.Constants.NAME;

public class WebViewPaymentActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_view_payment);

        Intent intent = getIntent();
        String memberId = intent.getStringExtra(MEMBERID);
        String name = intent.getStringExtra(NAME);

        WebView myWebView = findViewById(R.id.webview);
        myWebView.getSettings().setJavaScriptEnabled(true);
        myWebView.getSettings().setUseWideViewPort(true);
        myWebView.getSettings().setDomStorageEnabled(true);
        myWebView.loadUrl(getIntent().getStringExtra("url"));

        ProgressBar progressBar = new ProgressBar(this);

        myWebView.setWebViewClient(new WebviewClient(this,memberId,name));
    }

//    @Override
//    public void onBackPressed() {
//        super.onBackPressed();
//        Intent intent = new Intent(this,HomeActivity.class);
//        //intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//        startActivity(intent);
//    }
}