package org.chat.net.server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Server implements Runnable {

    private static final Logger LOG = Logger.getLogger(Server.class.getSimpleName());
    private static final int BACK_LOG = 50;
    private static final int MAX_CLIENT_THREADS = 100;
    private final Set<Socket> connections = ConcurrentHashMap.newKeySet();
    private final ExecutorService clientThreads = Executors.newFixedThreadPool(MAX_CLIENT_THREADS);
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
                Socket incomingConnection = serverSocket.accept();
                connections.add(incomingConnection);
                clientThreads.submit(new ClientHandler(incomingConnection));
                LOG.info("Received connection\n\n");
            } catch (IOException e) {
                LOG.log(Level.SEVERE, "Unable to connect to server", e);
            }
        }
    }

    public String getIP() {
        return serverSocket.getInetAddress().getHostAddress();
    }

    public int getPort() {
        return port;
    }

    public Set<Socket> getConnections() {
        return connections;
    }

    public void stop() {
        isRunning = false;
    }

    class ClientHandler implements Runnable {
        private final Socket socket;
        private final DataOutputStream out;
        private final DataInputStream in;

        ClientHandler(Socket socket) throws IOException {
            this.socket = socket;
            out = new DataOutputStream(socket.getOutputStream());
            in = new DataInputStream(socket.getInputStream());

            sendUpdatedConnectionsList();
        }

        @Override
        public void run() {
            while (true) {
                try {
                    if (in.available() > 0) {
                        int code = in.readByte();
                        LOG.info("Incoming code " + code + " \n");

                        switch (code) {

                        }
                    }

                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }

        //@Todo(John) better logging
        public void sendUpdatedConnectionsList() {
            try {
                out.write(10);
                out.write(Server.this.connections.size());
                out.writeByte(serverSocket.getInetAddress().getHostAddress().length());
                out.writeBytes(serverSocket.getInetAddress().getHostAddress());
                out.writeShort(port);
                LOG.info("Writing Port: " + port);
                for (Socket s : Server.this.connections) {
                    out.writeByte(s.getInetAddress().getHostAddress().length());
                    out.writeBytes(s.getInetAddress().getHostAddress());
                    out.writeShort(s.getPort());
                }
                out.flush();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
