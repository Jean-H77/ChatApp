package org.chat.net.server;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Server implements Runnable {

    private static final Logger LOG = Logger.getLogger(Server.class.getSimpleName());
    private static final int BACK_LOG = 50;
    private final List<ClientHandler> clientHandlers = Collections.synchronizedList(new ArrayList<>());
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
                LOG.info("First");
                Socket socket = serverSocket.accept();
                LOG.info("Second");
                addClientHandler(new ClientHandler(socket));
                LOG.info("Third");
                System.out.println("New connection: " + socket.getInetAddress() + ":" + socket.getPort());
            } catch (IOException e) {
                LOG.log(Level.SEVERE, "Unable to connect to server", e);
            }
        }
    }

    public ClientHandler getClientByIndex(int i) {
        return new ArrayList<>(clientHandlers).get(i);
    }

    public List<ClientHandler> getClientHandlers() {
        return clientHandlers;
    }

    public void terminate(int i) {
        clientHandlers.remove(i-1);
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
        if(clientHandlers.stream()
                .anyMatch(c ->
                c.getPort() == clientHandler.getPort() && Objects.equals(c.getIp(), clientHandler.getIp()))) {

            LOG.log(Level.WARNING, "Cannot add duplicate client handler " + clientHandler.getIp() + ": " + clientHandler.getPort());
            return;
        }

        clientThreads.submit(clientHandler);
        clientHandlers.add(clientHandler);
    }
}
