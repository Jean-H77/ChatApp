package org.chat;

import org.chat.net.client.Client;
import org.chat.net.server.ClientHandler;
import org.chat.net.server.Server;

import java.io.DataOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

import static org.chat.net.PacketConstants.*;

public record Peer(
        Server server,
        List<Client> clients,
        ExecutorService serverExecutor,
        ExecutorService clientExecutor,
        List<String> connections
) {

    private static final Logger LOG = Logger.getLogger(Peer.class.getSimpleName());

    public static Peer create(int port) {
        return new Peer(new Server(port), new CopyOnWriteArrayList<>(), Executors.newSingleThreadExecutor(), Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors()), new CopyOnWriteArrayList<>());
    }

    public void startServer() {
        serverExecutor.submit(server);
    }

    public void connect(String dest, int port) throws IOException {
        Client c = new Client();
        c.connect(dest, port);
        LOG.info("Connecting to: " + dest + " " + port);
        clients.add(c);
        clientExecutor.submit(c);
        LOG.info("Client size: " + clients.size());
    }

    public void sendMessage(int id, String message) {
        DataOutputStream out = getClientByIndex(id).getOut();
        try {
            out.writeByte(MESSAGE_OPCODE);
            out.writeByte(message.length());
            out.writeBytes(message);
            out.flush();
        } catch (Exception e) {
            LOG.log(Level.SEVERE, "Unable to send message", e);
        }
        System.out.println("Message sent to " + id);
    }

    public void terminate(int connectionId) {
        server.terminate(connectionId);
    }

    public Set<ClientHandler> getConnections() {
        return server.getClientHandlers();
    }

    public ClientHandler getClientByIndex(int index) {
        return server.getClientByIndex(index-1);
    }

    public void stop() {
        serverExecutor.shutdown();
        clientExecutor.shutdown();
        try {
            if (!serverExecutor.awaitTermination(800, TimeUnit.MILLISECONDS)) {
                serverExecutor.shutdownNow();
            }
            if (!clientExecutor.awaitTermination(800, TimeUnit.MILLISECONDS)) {
                clientExecutor.shutdownNow();
            }
        } catch (InterruptedException e) {
            serverExecutor.shutdownNow();
            clientExecutor.shutdownNow();
        }
    }
}
