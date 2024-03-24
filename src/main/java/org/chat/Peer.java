package org.chat;

import org.chat.net.client.Client;
import org.chat.net.server.Server;

import java.io.IOException;
import java.net.InetAddress;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public record Peer(
        Server server,
        Client client,
        ExecutorService serverExecutor,
        ExecutorService clientExecutor,
        List<String> connections
) {

    public static Peer create(int port) {
        return new Peer(new Server(port), new Client(), Executors.newSingleThreadExecutor(), Executors.newSingleThreadExecutor(), new CopyOnWriteArrayList<>());
    }

    public void startServer() {
        serverExecutor.submit(server);
    }

    public void connect(String dest, int port) throws IOException {
        client.connect(dest, port);
        clientExecutor.submit(client);
    }

    public void selfConnect(String ip, int port) throws IOException {

    }

    public void sendMessage(String connectionId, String message) {

    }


    public void terminate(String connectionId) {

    }

    public List<String> getConnectionsList() {
        return client.getConnections();
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
