package com.example.chat;

import java.io.*;
import java.net.Socket;

public class ChatClient {

    private Socket socket;
    private BufferedReader reader;
    private BufferedWriter writer;

    public ChatClient(String host, int port, String key, String username) throws IOException {
        socket = new Socket(host, port);
        reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));

        // Отправляем ключ
        sendLine(key);

        String keyResponse = readLine();
        if (!keyResponse.contains("Key accepted")) {
            throw new IOException("Invalid server key: " + keyResponse);
        }

        // Отправляем ник
        sendLine(username);

        String connectedMsg = readLine();
        if (!connectedMsg.contains("Connected")) {
            throw new IOException("Failed to connect: " + connectedMsg);
        }
    }

    // Отправка строки на сервер
    public void send(String msg) throws IOException {
        sendLine(msg);
    }

    private void sendLine(String msg) throws IOException {
        writer.write(msg + "\n");
        writer.flush();
    }

    // Чтение строки от сервера
    public String read() throws IOException {
        return readLine();
    }

    private String readLine() throws IOException {
        return reader.readLine();
    }

    public void close() throws IOException {
        if (socket != null) socket.close();
    }
}
