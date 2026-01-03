package com.example.chat;

import java.io.*;
import java.net.Socket;

public class ChatClient {
    private Socket socket;
    private BufferedReader in;
    private BufferedWriter out;

    public ChatClient(String host, String key, String username) throws IOException {
        socket = new Socket(host, 9009);
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));

        // Отправляем ключ
        out.write(key);
        out.newLine();
        out.flush();

        // Отправляем никнейм
        out.write(username);
        out.newLine();
        out.flush();
    }

    // Отправка сообщений
    public void send(String message) {
        try {
            out.write(message);
            out.newLine();
            out.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Чтение сообщений
    public String read() {
        try {
            return in.readLine();
        } catch (IOException e) {
            return null;
        }
    }

    public void close() {
        try {
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
