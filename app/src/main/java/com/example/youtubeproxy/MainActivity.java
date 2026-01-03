package com.example.youtubeplayer;

import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.browser.customtabs.CustomTabsIntent;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button open = findViewById(R.id.open);

        open.setOnClickListener(v -> {
            String url = "https://m.youtube.com/watch?v=dQw4w9WgXcQ";

            CustomTabsIntent intent = new CustomTabsIntent.Builder()
                    .setShowTitle(true)
                    .build();

            intent.launchUrl(this, Uri.parse(url));
        });
    }
}
