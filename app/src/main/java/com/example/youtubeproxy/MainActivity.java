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

    private final String username = "Julyet"; // ваш ник
    private final String serverKey = "efwefefwel7yywfeeyfefofjojooobjhvdgdgasdash"; // ключ
    private final String serverIp = "192.168.0.150"; // IP сервера
    private final int serverPort = 9009; // порт сервера

    @Override
    protected void onCreate(Bundle b) {
        super.onCreate(b);
        setContentView(R.layout.activity_main);

        chat = findViewById(R.id.chat);
        input = findViewById(R.id.input);
        send = findViewById(R.id.send);
        scrollView = findViewById(R.id.scrollView);

        send.setEnabled(false); // пока не подключились

        // Подключение к серверу в отдельном потоке
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
                    final String message = msg.trim();
                    if (!message.isEmpty()) {
                        runOnUiThread(() -> appendChat(message));
                    }
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

            try {
                // Отправляем с ником
                client.send(username + ": " + text);
            } catch (Exception e) {
                appendChat("❌ Failed to send message: " + e.getMessage());
                e.printStackTrace();
                return;
            }

            // Локально отображаем только один раз
            appendChat(username + ": " + text);
            input.setText("");
        });
    }

    // Добавление текста в chat с прокруткой вниз
    private void appendChat(String text) {
        chat.append(text + "\n");
        scrollView.post(() -> scrollView.fullScroll(ScrollView.FOCUS_DOWN));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            if (client != null) client.close();
        } catch (Exception ignored) {}
    }
}
