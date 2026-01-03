package com.example.chat;

import java.io.*;
import java.net.Socket;

public class ChatClient {
    Socket socket;
    BufferedReader in;
    PrintWriter out;

    public ChatClient(String host) throws Exception {
        socket = new Socket(host, 9009);
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        out = new PrintWriter(socket.getOutputStream(), true);
    }

    public void send(String msg) {
        out.println(msg);
    }

    public String read() throws Exception {
        return in.readLine();
    }
}
