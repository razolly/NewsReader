package com.example.razli.newsreader;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class WebViewActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_view);

        WebView webView = findViewById(R.id.webView);
        webView.getSettings().getJavaScriptEnabled();
        webView.setWebViewClient(new WebViewClient());

        webView.loadUrl(getIntent().getStringExtra("url"));
    }
}
