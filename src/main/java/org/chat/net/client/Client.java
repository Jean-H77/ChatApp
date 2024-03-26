package org.chat.net.client;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.logging.Logger;

import static org.chat.net.PacketConstants.MESSAGE_OPCODE;

public class Client implements Runnable {

    private static final Logger LOG = Logger.getLogger(Client.class.getSimpleName());
    private final List<String> connections = new CopyOnWriteArrayList<>();

    private int port;
    private Socket socket;
    private DataOutputStream out;
    private DataInputStream in;
    private boolean isRunning;

    @Override
    public void run() {
        isRunning = true;
        while(isRunning) {
            try {
                if (in.available() > 0) {
                    int opcode = in.readByte();
                    switch (opcode) {
                        case MESSAGE_OPCODE -> readMessage(in.readNBytes(in.available()));
                    }
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public void connect(String ip, int port) throws IOException {
        socket = new Socket(ip, port);
        out = new DataOutputStream(socket.getOutputStream());
        in = new DataInputStream(socket.getInputStream());
    }

    public Socket getSocket() {
        return socket;
    }

    public void readMessage(byte[] payload) {
        String messageReceived = new String(payload);
        System.out.println("Reading message: " + messageReceived);
    }

    public List<String> getConnections() {
        return connections;
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
}
