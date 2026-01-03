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

    String serverIp = "192.168.0.150";
    int serverPort = 9009;
    String serverKey = "efwefefwel7yywfeeyfefofjojooobjhvdgdgasdash";
    String username = "UFO";

    @Override
    protected void onCreate(Bundle b) {
        super.onCreate(b);
        setContentView(R.layout.activity_main);

        chat = findViewById(R.id.chat);
        input = findViewById(R.id.input);
        send = findViewById(R.id.send);
        scroll = findViewById(R.id.scrollView);

        send.setEnabled(false);

        client = new ChatClient(
                serverIp,
                serverPort,
                serverKey,
                username,
                new ChatClient.Listener() {
                    @Override
                    public void onMessage(String msg) {
                        runOnUiThread(() -> append(msg));
                    }

                    @Override
                    public void onError(String err) {
                        runOnUiThread(() -> append("❌ " + err));
                    }
                }
        );

        send.setOnClickListener(v -> {
            String text = input.getText().toString().trim();
            if (text.isEmpty()) return;

            client.send(text);
            input.setText("");
        });

        send.setEnabled(true);
    }

    void append(String s) {
        chat.append(s + "\n");
        scroll.post(() -> scroll.fullScroll(ScrollView.FOCUS_DOWN));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (client != null) client.close();
    }
}
