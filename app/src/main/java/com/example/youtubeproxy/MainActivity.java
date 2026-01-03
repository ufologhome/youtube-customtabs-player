package com.example.youtubeproxy;

import android.os.Bundle;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    TextView chat;
    EditText input;
    Button send;
    ScrollView scroll;

    ChatClient client;

    String username = "Julyet";      // ← НА ВТОРОМ ТЕЛЕФОНЕ Julyet
    String serverIp = "192.168.0.150";
    int serverPort = 9009;
    String serverKey = "efwefefwel7yywfeeyfefofjojooobjhvdgdgasdash";

    @Override
    protected void onCreate(Bundle b) {
        super.onCreate(b);
        setContentView(R.layout.activity_main);

        chat = findViewById(R.id.chat);
        input = findViewById(R.id.input);
        send = findViewById(R.id.send);
        scroll = findViewById(R.id.scrollView);

        send.setEnabled(false);

        // ===== CONNECT =====
        new Thread(() -> {
            try {
                client = new ChatClient(serverIp, serverPort, serverKey, username);

                runOnUiThread(() -> {
                    append("✅ Connected as " + username);
                    send.setEnabled(true);
                });

                // ===== READ LOOP =====
                String msg;
                while ((msg = client.read()) != null) {
                    String finalMsg = msg;
                    runOnUiThread(() -> append(finalMsg));
                }

            } catch (Exception e) {
                runOnUiThread(() ->
                    append("❌ Connection error: " + e.getMessage())
                );
            }
        }).start();

        // ===== SEND =====
        send.setOnClickListener(v -> {
            String text = input.getText().toString().trim();
            if (text.isEmpty()) return;

            try {
                client.send(text);
                append(username + ": " + text);
                input.setText("");
            } catch (Exception e) {
                append("❌ Send failed");
            }
        });
    }

    void append(String s) {
        chat.append(s + "\n");
        scroll.post(() -> scroll.fullScroll(ScrollView.FOCUS_DOWN));
    }
}
