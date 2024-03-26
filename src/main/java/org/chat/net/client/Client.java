package org.chat.net.client;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
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
                if (in.available() > 0) {
                    int opcode = in.readByte();
                    switch (opcode) {
                        case MESSAGE_OPCODE -> readMessage();
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
            int senderPort = socket.getPort();
            System.out.println("Message received from " + connectionIp);
            System.out.println("Sender's Port: " + senderPort);
            System.out.println("Message: " + messageReceived);
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
}
