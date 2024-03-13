package org.chat;

import org.chat.net.client.Client;
import org.chat.net.server.Server;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public record Peer(
        Server server,
        Client client,
        ExecutorService serverExecutor,
        ExecutorService clientExecutor
) {

    public static Peer create(int serverPort) {
        return new Peer(new Server(serverPort), new Client(), Executors.newSingleThreadExecutor(), Executors.newSingleThreadExecutor());
    }

    public void startServer() {
        serverExecutor.submit(server);
    }

    public void connect(String dest, int port) {

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
