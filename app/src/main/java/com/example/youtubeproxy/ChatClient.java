package com.example.youtubeproxy;

import java.io.*;
import java.net.Socket;

public class ChatClient {
    private Socket socket;
    private BufferedReader reader;
    private BufferedWriter writer;

    private String serverIp;
    private int serverPort;
    private String key;
    private String username;

    public ChatClient(String serverIp, int serverPort, String key, String username) throws IOException {
        this.serverIp = serverIp;
        this.serverPort = serverPort;
        this.key = key;
        this.username = username;

        connect();
    }

    private void connect() throws IOException {
        socket = new Socket(serverIp, serverPort);
        reader = new BufferedReader(new InputStreamReader(socket.getInputStream(), "UTF-8"));
        writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), "UTF-8"));

        // Протокол: сначала ключ
        reader.readLine(); // "Enter key:"
        sendLine(key);

        String response = reader.readLine();
        if (!response.contains("Key accepted")) {
            throw new IOException("Invalid key: " + response);
        }

        // Потом никнейм
        sendLine(username);
        reader.readLine(); // "Connected to chat..."
    }

    // Отправка строки с переносом
    public void send(String msg) throws IOException {
        sendLine(msg);
    }

    private void sendLine(String msg) throws IOException {
        writer.write(msg + "\n");
        writer.flush();
    }

    // Чтение строки с сервера
    public String read() throws IOException {
        return reader.readLine();
    }

    // Закрыть соединение
    public void close() throws IOException {
        socket.close();
    }
}
