package com.example.chat;

import android.os.Bundle;
import android.util.Log;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    TextView chat;
    EditText input;
    Button send;
    ScrollView scrollView;

    ChatClient client;
    volatile boolean connected = false;

    String username = "Julyet"; // Никнейм
    String serverKey = "efwefefwel7yywfeeyfefofjojooobjhvdgdgasdash"; // Ключ
    String serverIp = "192.168.0.150"; // IP сервера
    int serverPort = 9009; // Порт сервера

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        chat = findViewById(R.id.chat);
        input = findViewById(R.id.input);
        send = findViewById(R.id.send);
        scrollView = findViewById(R.id.scrollView);

        send.setEnabled(false);

        // Поток подключения
        new Thread(() -> {
            try {
                Log.d("CHAT", "Connecting...");
                client = new ChatClient(serverIp, serverPort, serverKey, username);
                connected = true;

                runOnUiThread(() -> {
                    appendChat("✅ Connected as " + username);
                    send.setEnabled(true);
                });

                String msg;
                while ((msg = client.read()) != null) {
                    String[] parts = msg.split(":", 2);
                    String sender = parts.length > 1 ? parts[0] : "Friend";
                    String text = parts.length > 1 ? parts[1] : msg;

                    String finalSender = sender;
                    String finalText = text;
                    runOnUiThread(() -> appendChat(finalSender + ": " + finalText));
                }

            } catch (Exception e) {
                Log.e("CHAT", "Connection error", e);
                runOnUiThread(() -> appendChat("❌ Connection failed: " + e.getMessage()));
            }
        }).start();

        send.setOnClickListener(v -> {
            if (!connected || client == null) return;

            String text = input.getText().toString().trim();
            if (text.isEmpty()) return;

            client.send(username + ":" + text);
            appendChat(username + ": " + text); // показываем локально
            input.setText("");
        });
    }

    void appendChat(String text) {
        chat.append(text + "\n");
        scrollView.post(() -> scrollView.fullScroll(ScrollView.FOCUS_DOWN));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (client != null) client.close();
    }
}
