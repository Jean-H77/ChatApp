package org.chat;

import org.chat.net.client.Client;
import org.chat.net.server.Server;

import java.io.DataOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static org.chat.net.PacketConstants.CONNECTIONS_LIST_OPCODE;

public record Peer(
        Server server,
        List<Client> clients,
        ExecutorService serverExecutor,
        ExecutorService clientExecutor,
        List<String> connections
) {

    public static Peer create(int port) {
        return new Peer(new Server(port), new CopyOnWriteArrayList<>(), Executors.newSingleThreadExecutor(), Executors.newSingleThreadExecutor(), new CopyOnWriteArrayList<>());
    }

    public void startServer() {
        serverExecutor.submit(server);
    }

    public void connect(String dest, int port) throws IOException {
        Client c = new Client();
        c.connect(dest, port);
        clients.add(c);
        clientExecutor.submit(c);
    }

    public void selfConnect(String ip, int port) throws IOException {

    }

    public void sendMessage(String connectionId, String message) {

    }


    public void terminate(String connectionId) {

    }

    public void  requestConnectionsList() {
        for(Client c : clients) {
            DataOutputStream out = c.getOut();
            try {
                out.write(CONNECTIONS_LIST_OPCODE);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
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
