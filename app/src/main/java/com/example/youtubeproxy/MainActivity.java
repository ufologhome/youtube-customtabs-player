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

    String username = "UFO"; // Никнейм своего пользователя
    String serverKey = "efwefefwel7yywfeeyfefofjojooobjhvdgdgasdash"; // Простой ключ для подключения

    @Override
    protected void onCreate(Bundle b) {
        super.onCreate(b);
        setContentView(R.layout.activity_main);

        chat = findViewById(R.id.chat);
        input = findViewById(R.id.input);
        send = findViewById(R.id.send);
        scrollView = findViewById(R.id.scrollView);

        send.setEnabled(false); // ❗ пока не подключились

        // Подключение в отдельном потоке
        new Thread(() -> {
            try {
                Log.d("CHAT", "Connecting...");
                client = new ChatClient("192.168.0.150", serverKey, username); // IP и ключ
                connected = true;

                runOnUiThread(() -> {
                    appendChat("✅ Connected as " + username);
                    send.setEnabled(true);
                });

                while (true) {
                    String msg = client.read();
                    if (msg == null) break;

                    String[] parts = msg.split(":", 2);
                    String sender = parts.length > 1 ? parts[0] : "Friend";
                    String text = parts.length > 1 ? parts[1] : msg;

                    runOnUiThread(() ->
                        appendChat(sender + ": " + text)
                    );
                }
            } catch (Exception e) {
                Log.e("CHAT", "Connection error", e);
                runOnUiThread(() ->
                    appendChat("❌ Connection failed")
                );
            }
        }).start();

        // Отправка сообщений
        send.setOnClickListener(v -> {
            if (!connected || client == null) return;

            String text = input.getText().toString().trim();
            if (text.isEmpty()) return;

            try {
                client.send(username + ":" + text); // отправляем с никнеймом
                appendChat(username + ": " + text); // показываем локально
            } catch (Exception e) {
                appendChat("❌ Failed to send message");
            }

            input.setText("");
        });
    }

    // Добавление текста в chat с прокруткой вниз
    void appendChat(String text) {
        chat.append(text + "\n");
        scrollView.post(() -> scrollView.fullScroll(ScrollView.FOCUS_DOWN));
    }
}
