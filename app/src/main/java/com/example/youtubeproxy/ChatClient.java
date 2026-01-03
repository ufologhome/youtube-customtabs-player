package com.example.youtubeproxy;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;

public class ChatClient {

    public interface Listener {
        void onMessage(String msg);
        void onError(String err);
    }

    private Socket socket;
    private BufferedReader reader;
    private BufferedWriter writer;
    private Listener listener;

    public ChatClient(
            String host,
            int port,
            String key,
            String username,
            Listener listener
    ) {
        this.listener = listener;

        new Thread(() -> {
            try {
                socket = new Socket(host, port);

                reader = new BufferedReader(
                        new InputStreamReader(socket.getInputStream())
                );
                writer = new BufferedWriter(
                        new OutputStreamWriter(socket.getOutputStream())
                );

                // "Enter key:"
                reader.readLine();
                sendRaw(key);

                String resp = reader.readLine();
                if (resp == null || resp.contains("Invalid")) {
                    listener.onError("Invalid key");
                    close();
                    return;
                }

                // "Send username:"
                reader.readLine();
                sendRaw(username);

                // "Connected"
                reader.readLine();

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

    public void send(String msg) {
        new Thread(() -> {
            try {
                sendRaw(msg);
            } catch (Exception ignored) {}
        }).start();
    }

    private void sendRaw(String s) throws Exception {
        writer.write(s);
        writer.write("\n");
        writer.flush();
    }

    public void close() {
        try {
            if (socket != null) socket.close();
        } catch (Exception ignored) {}
    }
}
