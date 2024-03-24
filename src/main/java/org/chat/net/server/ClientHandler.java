package org.chat.net.server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ClientHandler implements Runnable {

    private static final Logger LOG = Logger.getLogger(ClientHandler.class.getSimpleName());
    private final Socket socket;
    private final String ip;
    private final int port;

    private DataOutputStream out;
    private DataInputStream in;
    private boolean isRunning;

    public ClientHandler(Socket socket) {
        this.socket = socket;
        this.ip = socket.getInetAddress().getHostAddress();
        this.port = socket.getPort();
        try {
            out = new DataOutputStream(socket.getOutputStream());
            in = new DataInputStream(socket.getInputStream());
        } catch (Exception e) {
            LOG.log(
                    Level.SEVERE,
                    "Unable to initialize client handler on "
                    + socket.getInetAddress().getHostAddress() + ":" + socket.getPort(),
                    e);
        }
        isRunning = true;
    }

    @Override
    public void run() {
        while (isRunning) {
            try {
                if (in.available() > 0) {
                    int code = in.readByte();
                    LOG.info("Incoming opcode from the client " + code + " \n");

                    switch (code) {

                    }
                }

            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public void stop() {
        isRunning = false;
    }

    public Socket getSocket() {
        return socket;
    }

    public DataOutputStream getOut() {
        return out;
    }

    public DataInputStream getIn() {
        return in;
    }

    public boolean isRunning() {
        return isRunning;
    }

    public void setRunning(boolean running) {
        isRunning = running;
    }

    public String getIp() {
        return ip;
    }

    public int getPort() {
        return port;
    }
}
