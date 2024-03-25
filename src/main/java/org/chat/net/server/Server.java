package org.chat.net.server;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

import static org.chat.net.PacketConstants.CONNECTIONS_LIST_OPCODE;

public class Server implements Runnable {

    private static final Logger LOG = Logger.getLogger(Server.class.getSimpleName());
    private static final int BACK_LOG = 50;
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
                LOG.info("Accepting connection: " + socket.getPort() + " " + socket.getLocalPort());
                ServerContext.INSTANCE.addClientHandler(new ClientHandler(socket));
                LOG.info("Received connection\n\n");
            } catch (IOException e) {
                LOG.log(Level.SEVERE, "Unable to connect to server", e);
            }
        }
    }

    public void sendConnectionList(DataOutputStream out) {
        try {
            ServerContext context = ServerContext.INSTANCE;
            out.write(CONNECTIONS_LIST_OPCODE);
            out.write(context.getClients().size());
            for (ClientHandler h : context.getClients()) {
                out.writeByte(h.getIp().length());
                out.writeBytes(h.getIp());
                out.writeShort(h.getPort());
            }
            out.flush();
        } catch (Exception e) {
            LOG.log(Level.SEVERE, "Unable to send updated connections list", e);
        }
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
}
