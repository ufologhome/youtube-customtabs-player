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

    String username = "Julyet"; 
    String serverKey = "efwefefwel7yywfeeyfefofjojooobjhvdgdgasdash";
    String serverIp = "192.168.0.150"; 
    int serverPort = 9009;

    @Override
    protected void onCreate(Bundle b) {
        super.onCreate(b);
        setContentView(R.layout.activity_main);

        chat = findViewById(R.id.chat);
        input = findViewById(R.id.input);
        send = findViewById(R.id.send);
        scrollView = findViewById(R.id.scrollView);

        send.setEnabled(false);

        // Подключение в отдельном потоке
        new Thread(() -> {
            try {
                client = new ChatClient(serverIp, serverPort, serverKey, username);
                connected = true;

                runOnUiThread(() -> {
                    appendChat("✅ Connected as " + username);
                    send.setEnabled(true);
                });

                String msg;
                while ((msg = client.read()) != null) {
                    runOnUiThread(() -> appendChat(msg));
                }

            } catch (Exception e) {
                Log.e("CHAT", "Connection error", e);
                runOnUiThread(() -> appendChat("❌ Connection failed: " + e.getMessage()));
            }
        }).start();

        // Отправка сообщений
        send.setOnClickListener(v -> {
            if (!connected || client == null) return;

            String text = input.getText().toString().trim();
            if (text.isEmpty()) return;

            new Thread(() -> {
                try {
                    client.send(username + ":" + text);
                    runOnUiThread(() -> appendChat(username + ": " + text));
                } catch (Exception e) {
                    runOnUiThread(() -> appendChat("❌ Failed to send message: " + e.getMessage()));
                    e.printStackTrace();
                }
            }).start();

            input.setText("");
        });
    }

    void appendChat(String text) {
        chat.append(text + "\n");
        scrollView.post(() -> scrollView.fullScroll(ScrollView.FOCUS_DOWN));
    }
}
