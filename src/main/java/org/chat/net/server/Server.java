package org.chat.net.server;

import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Server implements Runnable {

    private static final Logger LOG = Logger.getLogger(Server.class.getSimpleName());
    private final int port;
    private final Set<Socket> connections = new HashSet<>();

    private ServerSocket serverSocket;

    public Server(int port) {
        this.port = port;
    }

    @Override
    public void run() {
        try {
            int backlog = 50; //we'll set max to 50
            serverSocket = new ServerSocket(port, backlog, InetAddress.getLocalHost());
            Socket incomingConnection = serverSocket.accept();
            connections.add(incomingConnection);
            LOG.info("Received connection");
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
