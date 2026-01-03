package com.example.chat;

import android.os.Bundle;
import android.util.Log;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    TextView chat;
    EditText input;
    Button send;

    ChatClient client;
    volatile boolean connected = false;

    @Override
    protected void onCreate(Bundle b) {
        super.onCreate(b);
        setContentView(R.layout.activity_main);

        chat = findViewById(R.id.chat);
        input = findViewById(R.id.input);
        send = findViewById(R.id.send);

        send.setEnabled(false);

        // 🔹 Подключение к серверу
        new Thread(() -> {
            try {
                Log.d("CHAT", "Connecting...");
                client = new ChatClient("192.168.0.150", 9009);
                connected = true;

                runOnUiThread(() -> {
                    chat.append("\n✅ Connected");
                    send.setEnabled(true);
                });

                while (true) {
                    String msg = client.read();
                    if (msg == null) break;

                    runOnUiThread(() ->
                        chat.append("\nFriend: " + msg)
                    );
                }

            } catch (Exception e) {
                Log.e("CHAT", "Connection error", e);
                runOnUiThread(() ->
                    chat.append("\n❌ Connection failed")
                );
            }
        }).start();

        // 🔹 ОТПРАВКА СООБЩЕНИЯ — ТОЛЬКО В ПОТОКЕ
        send.setOnClickListener(v -> {
            if (!connected || client == null) return;

            String text = input.getText().toString();
            input.setText("");

            chat.append("\nMe: " + text);

            new Thread(() -> {
                try {
                    client.send(text);
                } catch (Exception e) {
                    Log.e("CHAT", "Send failed", e);
                    runOnUiThread(() ->
                        chat.append("\n❌ Send error")
                    );
                }
            }).start();
        });
    }
}
