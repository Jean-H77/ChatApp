package org.chat.net.server;

import java.net.InetAddress;
import java.net.ServerSocket;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Server implements Runnable {

    private static final Logger LOG = Logger.getLogger(Server.class.getSimpleName());
    private final int port;

    private ServerSocket serverSocket;

    public Server(int port) {
        this.port = port;
    }

    @Override
    public void run() {
        try {
            int backlog = 50; //we'll set max to 50
            serverSocket = new ServerSocket(port, backlog, InetAddress.getLocalHost());
            LOG.info("[SERVER] STARTING SERVER ON " + serverSocket.getInetAddress().getHostAddress() + ":" + port);
        } catch (Exception e) {
            LOG.log(Level.SEVERE, "Unable to connect to server", e);
        }
    }

    public String getIP() {
        return serverSocket.getInetAddress().getHostAddress();
    }

    public int getPort() {
        return port;
    }
}
