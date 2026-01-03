package com.example.chat;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;

public class ChatClient {

    private Socket socket;
    private BufferedReader reader;
    private BufferedWriter writer;

    public interface MessageListener {
        void onMessage(String msg);
        void onError(String err);
    }

    public ChatClient(
            String host,
            int port,
            String key,
            String username,
            MessageListener listener
    ) {
        new Thread(() -> {
            try {
                socket = new Socket(host, port);
                reader = new BufferedReader(
                        new InputStreamReader(socket.getInputStream())
                );
                writer = new BufferedWriter(
                        new OutputStreamWriter(socket.getOutputStream())
                );

                // ---- SERVER: "Enter key:"
                reader.readLine();

                // SEND KEY
                sendLine(key);

                // ---- SERVER RESPONSE
                String response = reader.readLine();
                if (response == null || response.contains("Invalid")) {
                    listener.onError("Invalid key");
                    close();
                    return;
                }

                // ---- SERVER: "Send your username:"
                reader.readLine();

                // SEND USERNAME
                sendLine(username);

                // ---- SERVER: "Connected to chat"
                reader.readLine();

                // READ CHAT
                while (true) {
                    String line = reader.readLine();
                    if (line == null) break;
                    listener.onMessage(line);
                }

            } catch (Exception e) {
                listener.onError(e.toString());
            } finally {
                close();
            }
        }).start();
    }

    public void sendMessage(String msg) {
        new Thread(() -> {
            try {
                sendLine(msg);
            } catch (Exception ignored) {}
        }).start();
    }

    private void sendLine(String text) throws Exception {
        writer.write(text);
        writer.write("\n");
        writer.flush();
    }

    private void close() {
        try {
            if (socket != null) socket.close();
        } catch (Exception ignored) {}
    }
}
