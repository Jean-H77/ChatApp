package org.chat.net.server;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Server implements Runnable {

    private static final Logger LOG = Logger.getLogger(Server.class.getSimpleName());
    private static final int BACK_LOG = 50;
    private final Set<ClientHandler> clientHandlers = ConcurrentHashMap.newKeySet();
    private final ExecutorService clientThreads = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
    private final int port;

    private ServerSocket serverSocket;
    private boolean isRunning;

    public Server(int port) {
        this.port = port;
    }

    @Override
    public void run() {
        try {
            serverSocket = new ServerSocket(port, BACK_LOG, InetAddress.getLocalHost());
        } catch (IOException e) {
            LOG.log(Level.SEVERE, "Unable to start server on port: " + port, e);
        }
        isRunning = true;
        while (isRunning) {
            try {
                Socket socket = serverSocket.accept();
                addClientHandler(new ClientHandler(socket));
                System.out.println("New connection: " + socket.getInetAddress() + ":" + socket.getPort());
            } catch (IOException e) {
                LOG.log(Level.SEVERE, "Unable to connect to server", e);
            }
        }
    }

    public ClientHandler getClientByIndex(int i) {
        return new ArrayList<>(clientHandlers).get(i);
    }

    public Set<ClientHandler> getClientHandlers() {
        return clientHandlers;
    }

    public String getIP() {
        return serverSocket.getInetAddress().getHostAddress();
    }

    public int getPort() {
        return port;
    }

    public void stop() {
        isRunning = false;
    }

    public void addClientHandler(ClientHandler clientHandler) {
        clientThreads.submit(clientHandler);
        clientHandlers.add(clientHandler);
    }
}
