package com.example.chat;

import java.io.*;
import java.net.Socket;

public class ChatClient {

    Socket socket;
    BufferedReader in;
    PrintWriter out;

    public ChatClient(String host, int port) throws IOException {
        socket = new Socket(host, port);
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        out = new PrintWriter(new BufferedWriter(
                new OutputStreamWriter(socket.getOutputStream())
        ), true);
    }

    public void send(String msg) {
        out.println(msg);
    }

    public String read() throws IOException {
        return in.readLine();
    }
}
