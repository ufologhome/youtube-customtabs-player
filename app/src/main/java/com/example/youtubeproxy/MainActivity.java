package com.example.chat;

import android.os.Bundle;
import android.util.Log;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private TextView chat;
    private EditText input;
    private Button send;
    private ScrollView scrollView;

    private ChatClient client;
    private volatile boolean connected = false;

    private final String username = "UFO"; // Никнейм пользователя
    private final String serverKey = "efwefefwel7yywfeeyfefofjojooobjhvdgdgasdash"; // Ключ для сервера

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        chat = findViewById(R.id.chat);
        input = findViewById(R.id.input);
        send = findViewById(R.id.send);
        scrollView = findViewById(R.id.scrollView);

        send.setEnabled(false); // ❗ пока не подключились

        // Подключение к серверу в отдельном потоке
        new Thread(() -> {
            try {
                Log.d("CHAT", "Connecting...");
                client = new ChatClient("192.168.0.150", serverKey, username); // IP, ключ, ник
                connected = true;

                runOnUiThread(() -> {
                    appendChat("✅ Connected as " + username);
                    send.setEnabled(true);
                });

                // Чтение сообщений от сервера
                String msg;
                while ((msg = client.read()) != null) {
                    final String message = formatMessage(msg);
                    runOnUiThread(() -> appendChat(message));
                }

            } catch (Exception e) {
                Log.e("CHAT", "Connection error", e);
                runOnUiThread(() -> appendChat("❌ Connection failed: " + e.getMessage()));
            }
        }).start();

        // Отправка сообщений
        send.setOnClickListener(v -> sendMessage());
    }

    // Метод для отправки сообщения
    private void sendMessage() {
        if (!connected || client == null) return;

        String text = input.getText().toString().trim();
        if (text.isEmpty()) return;

        try {
            client.send(username + ":" + text); // Отправка с никнеймом
            appendChat(username + ": " + text); // Показываем локально
        } catch (Exception e) {
            appendChat("❌ Failed to send message: " + e.getMessage());
        }

        input.setText("");
    }

    // Форматирование полученного сообщения
    private String formatMessage(String raw) {
        if (raw.contains(":")) {
            String[] parts = raw.split(":", 2);
            String sender = parts[0];
            String message = parts[1];
            return sender + ": " + message;
        } else {
            return "Friend: " + raw;
        }
    }

    // Добавление текста в chat с прокруткой вниз
    private void appendChat(String text) {
        chat.append(text + "\n");
        scrollView.post(() -> scrollView.fullScroll(ScrollView.FOCUS_DOWN));
    }
}
