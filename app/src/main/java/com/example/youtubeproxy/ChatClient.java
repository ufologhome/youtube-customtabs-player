package com.example.chat;

import android.util.Log;

import java.io.*;
import java.net.Socket;

public class ChatClient {

    private Socket socket;
    private BufferedReader reader;
    private BufferedWriter writer;

    public ChatClient(String host, int port, String key, String username) throws IOException {
        socket = new Socket(host, port);

        reader = new BufferedReader(new InputStreamReader(socket.getInputStream(), "UTF-8"));
        writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), "UTF-8"));

        // Отправляем ключ
        sendRaw(key);

        String serverResponse = readRaw(); // "Key accepted" или "Invalid key"
        Log.d("ChatClient", "Server response: " + serverResponse);

        // Отправляем никнейм
        sendRaw(username);
    }

    // Чтение одной строки
    public String read() {
        try {
            return reader.readLine();
        } catch (IOException e) {
            Log.e("ChatClient", "Read error", e);
            return null;
        }
    }

    // Отправка сообщения с добавлением \n
    public void send(String msg) {
        sendRaw(msg);
    }

    private void sendRaw(String msg) {
        try {
            writer.write(msg + "\n");
            writer.flush();
        } catch (IOException e) {
            Log.e("ChatClient", "Send error", e);
        }
    }

    // Закрытие соединения
    public void close() {
        try {
            if (socket != null) socket.close();
        } catch (IOException e) {
            Log.e("ChatClient", "Close error", e);
        }
    }
}
