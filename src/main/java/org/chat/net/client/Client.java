package org.chat.net.client;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.logging.Logger;

public class Client implements Runnable {

    private static final Logger LOG = Logger.getLogger(Client.class.getSimpleName());
    private final List<String> connections = new CopyOnWriteArrayList<>();

    private int port;
    private Socket socket;
    private DataOutputStream out;
    private DataInputStream in;

    @Override
    public void run() {
        LOG.info("Starting loop");
        try {
            if(in.available() > 0) {
                int opcode = in.readByte();
                LOG.info("Reading opcode: " + opcode);
                switch (opcode) {
                    case 10 -> populateConnectionsList();
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void connect(String ip, int port) throws IOException {
        socket = new Socket(ip, port);
        out = new DataOutputStream(socket.getOutputStream());
        in = new DataInputStream(socket.getInputStream());
        LOG.info("Connected");
    }

    public Socket getSocket() {
        return socket;
    }

    public void populateConnectionsList() throws IOException {
        connections.clear();
        int size = in.read() - 1;
        int oLength = in.read();

        String oIp = new String(in.readNBytes(oLength));
        int oPort = in.readUnsignedShort();
        connections.add(oIp + ":" + oPort);

        for(int i = 0; i < size; i++) {
            int length = in.read();
            String ip = new String(in.readNBytes(length));
            int port = in.readUnsignedShort();
            connections.add(ip + ":" + port);
        }
    }

    public List<String> getConnections() {
        return connections;
    }
}
