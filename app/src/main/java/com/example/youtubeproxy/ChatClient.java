package com.example.chat;

import java.io.*;
import java.net.Socket;

public class ChatClient {

    private Socket socket;
    private BufferedReader reader;
    private BufferedWriter writer;

    public ChatClient(String host, int port, String serverKey, String username) throws IOException {
        socket = new Socket(host, port);
        reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));

        // Отправка ключа при подключении
        writer.write("KEY:" + serverKey + "\n");
        writer.flush();
    }

    public void send(String message) throws IOException {
        if (socket == null || socket.isClosed())
            throw new IOException("Socket is closed");

        writer.write(message + "\n");
        writer.flush();
    }

    public String read() throws IOException {
        return reader.readLine();
    }

    public void close() {
        try { if (reader != null) reader.close(); } catch (IOException ignored) {}
        try { if (writer != null) writer.close(); } catch (IOException ignored) {}
        try { if (socket != null) socket.close(); } catch (IOException ignored) {}
    }
}
