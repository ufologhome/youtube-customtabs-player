package com.example.youtuberelay;

import android.os.Bundle;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private static final String VIDEO_ID = "3JIPDZqkeSo";
    private static final String RELAY_IP = "192.168.0.150"; // IP твоего ПК с Python relay
    private static final int RELAY_PORT = 8881;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        WebView webView = findViewById(R.id.webview);
        WebSettings settings = webView.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setDomStorageEnabled(true);
        settings.setMediaPlaybackRequiresUserGesture(false);

        webView.setWebViewClient(new WebViewClient());

        String url = "http://" + RELAY_IP + ":" + RELAY_PORT + "/https://www.youtube.com/embed/" + VIDEO_ID + "?autoplay=1&playsinline=1";
        webView.loadUrl(url);
    }
}
