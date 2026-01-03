package com.example.youtubeproxy;

import java.io.*;
import java.net.Socket;

public class ChatClient {

    private Socket socket;
    private BufferedInputStream in;
    private BufferedOutputStream out;

    public ChatClient(String ip, int port, String key, String username) throws Exception {
        socket = new Socket(ip, port);
        in = new BufferedInputStream(socket.getInputStream());
        out = new BufferedOutputStream(socket.getOutputStream());

        read();              // KEY:
        sendRaw(key);

        read();              // USERNAME:
        sendRaw(username);

        String ok = read();  // OK
        if (!ok.equals("OK"))
            throw new Exception("Server отказал: " + ok);
    }

    public void send(String msg) throws Exception {
        sendRaw(msg);
    }

    public String read() throws Exception {
        byte[] buf = new byte[4096];
        int len = in.read(buf);
        if (len == -1) return null;
        return new String(buf, 0, len, "UTF-8").trim();
    }

    private void sendRaw(String s) throws Exception {
        out.write((s + "\n").getBytes("UTF-8"));
        out.flush();
    }

    public void close() {
        try { socket.close(); } catch (Exception ignored) {}
    }
}
