package org.chat.net.client;

import org.chat.Peer;
import org.chat.net.server.ClientHandler;
import org.chat.net.server.Server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Logger;

import static org.chat.net.PacketConstants.MESSAGE_OPCODE;

public class Client implements Runnable {

    private static final Logger LOG = Logger.getLogger(Client.class.getSimpleName());
    private int port;
    private Socket socket;
    private DataOutputStream out;
    private DataInputStream in;
    private boolean isRunning;

    @Override
    public void run() {
        isRunning = true;
        LOG.info("Running client loop");
        while(isRunning) {
            try {
                if (in.available() >= 0) {
                    out.writeByte(0);
                    int opcode = in.readByte();
                    switch (opcode) {
                        case MESSAGE_OPCODE -> readMessage();
                        case -5 -> readMessageRemoval();
                    }
                }
            } catch (IOException e) {
                System.out.println("Client with the ip " + socket.getInetAddress().toString().substring(1) + " and port " + socket.getPort() + " has been dropped.");
                try {
                    socket.close();
                    out.close();
                    in.close();
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
                setRunning(false);
            }
        }
    }

    public void connect(String ip, int port) throws IOException {
        socket = new Socket(ip, port);
        out = new DataOutputStream(socket.getOutputStream());
        in = new DataInputStream(socket.getInputStream());
        LOG.info("Started client connection");
    }

    public Socket getSocket() {
        return socket;
    }

    public void readMessage() {
        try {
            int messageLength = in.readByte();
            String messageReceived = new String(in.readNBytes(messageLength));
            String connectionIp = socket.getInetAddress().getHostAddress();
            int senderPort = socket.getLocalPort();
            System.out.println("Message received from " + connectionIp);
            System.out.println("Sender's Port: " + senderPort);
            System.out.println("Message: " + messageReceived);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void readMessageRemoval() {
        try {
            int messageLength = in.readByte();
            String messageReceived = new String(in.readNBytes(messageLength));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean isRunning() {
        return isRunning;
    }

    public void setRunning(boolean running) {
        isRunning = running;
    }

    public DataOutputStream getOut() {
        return out;
    }

    public void stop() {
        try {
            out.close();
            in.close();
            socket.close();
        } catch (IOException e) {
            System.out.println("Error");
        } catch (NullPointerException ignored) {}
    }
}


