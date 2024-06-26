package org.chat.net.server;

import java.io.IOException;
import java.net.BindException;
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
        } catch (Exception e) {
            System.out.println("Unable to start server on port: " + port);
            System.exit(0);
        }
        isRunning = true;
        while (isRunning) {
            try {
                Socket socket = serverSocket.accept();
                addClientHandler(new ClientHandler(socket), 1);
                System.out.println("New connection: " + socket.getInetAddress() + ":" + socket.getPort());
            } catch (IOException e) {
                LOG.log(Level.SEVERE, "Unable to connect to server", e);
            }
        }
    }

    public ClientHandler getClientByIndex(int i) {
        if (i > clientHandlers.size() || i < 0) {
            return null;
        }
        else {
            return clientHandlers.get(i);
        }
    }

    public List<ClientHandler> getClientHandlers() {
        return clientHandlers;
    }

    public void terminate(int i) {
        try {
            ClientHandler ch = clientHandlers.get(i-1);
            ch.getOut().close();
            ch.getIn().close();
            ch.getSocket().close();
            clientHandlers.remove(ch);
        } catch (IOException e) {
            LOG.log(Level.SEVERE, "Unable to remove client");
        } catch (IndexOutOfBoundsException ignored) {}
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

    public void addClientHandler(ClientHandler clientHandler, int value) {
        if (value == 1) {
            clientThreads.submit(clientHandler);
        }
        clientHandlers.add(clientHandler);
    }

}
