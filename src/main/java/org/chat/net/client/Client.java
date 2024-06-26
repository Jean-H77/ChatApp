package org.chat.net.client;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

import static org.chat.net.PacketConstants.MESSAGE_OPCODE;
import static org.chat.net.PacketConstants.MESSAGE_REMOVAL_OPCODE;

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
        LOG.log(Level.ALL,"Running client loop");
        while(isRunning) {
            try {
                if (in.available() >= 0) {
                    out.writeByte(0);
                    int opcode = in.readByte();
                    switch (opcode) {
                        case MESSAGE_OPCODE -> readMessage();
                        case MESSAGE_REMOVAL_OPCODE -> readMessageRemoval();
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
        LOG.log(Level.ALL,"Started client connection");
    }

    public Socket getSocket() {
        return socket;
    }

    public void readMessage() {
        try {
            int messageLength = in.readByte();
            String messageReceived = new String(in.readNBytes(messageLength));
            String connectionIp = socket.getInetAddress().getHostAddress();
            int senderPort = socket.getPort();
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

    public void setRunning(boolean running) {
        isRunning = running;
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


