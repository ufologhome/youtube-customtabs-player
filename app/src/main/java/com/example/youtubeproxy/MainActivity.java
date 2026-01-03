package com.example.chat;

import android.os.Bundle;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    TextView chat;
    EditText input;
    ChatClient client;

    @Override
    protected void onCreate(Bundle b) {
        super.onCreate(b);
        setContentView(R.layout.activity_main);

        chat = findViewById(R.id.chat);
        input = findViewById(R.id.input);
        Button send = findViewById(R.id.send);

        new Thread(() -> {
            try {
                client = new ChatClient("192.168.0.150"); // IP сервера
                while (true) {
                    String msg = client.read();
                    runOnUiThread(() ->
                        chat.append("\nFriend: " + msg)
                    );
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();

        send.setOnClickListener(v -> {
            String text = input.getText().toString();
            client.send(text);
            chat.append("\nMe: " + text);
            input.setText("");
        });
    }
}
