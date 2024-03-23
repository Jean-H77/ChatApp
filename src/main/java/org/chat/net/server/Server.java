package org.chat.net.server;

import org.chat.net.client.Client;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Server implements Runnable {

    private static final Logger LOG = Logger.getLogger(Server.class.getSimpleName());
    private final int port;
    private final Set<Socket> connections = new HashSet<>();
    private final ArrayList<String> split = new ArrayList<>();
    private final Client client = new Client();

    private ServerSocket serverSocket;


    public Server(int port) {
        this.port = port;
    }

    @Override
    public void run() {
        try {
            int backlog = 50; //we'll set max to 50
            serverSocket = new ServerSocket(port, backlog, InetAddress.getLocalHost());
        }
        catch(IOException e) {
            e.printStackTrace();
        }
            while(true) {
                try {
                    Socket incomingConnection = serverSocket.accept();
                    if (split.isEmpty()) {
                        split.add(0, incomingConnection.getRemoteSocketAddress().toString().replaceAll("/","").replaceAll(":", " ").split("\\s+")[0]);
                        split.add(1, incomingConnection.getRemoteSocketAddress().toString().replaceAll("/","").replaceAll(":", " ").split("\\s+")[1]);
                    }
                    else {
                        split.set(0, incomingConnection.getRemoteSocketAddress().toString().replaceAll("/","").replaceAll(":", " ").split("\\s+")[0]);
                        split.set(1, incomingConnection.getRemoteSocketAddress().toString().replaceAll("/","").replaceAll(":", " ").split("\\s+")[1]);
                    }
                    //client.connect2(split.get(0), split.get(1));
                   // System.out.println(split);
                    connections.add(incomingConnection);
//                    System.out.println(connections);
                    LOG.info("Received connection");
                }
                catch (Exception e) {
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

    public ArrayList<String> list() {
        if(split.isEmpty()) {
            return null;
        }
        else {
            return client.connect2(split.get(0), split.get(1));

        }
    }

}
