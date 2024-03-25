package org.chat.net.server;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public enum ServerContext {
    INSTANCE;

    private final Set<ClientHandler> clients = ConcurrentHashMap.newKeySet();

    private final ExecutorService clientThreads = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());

    public Set<ClientHandler> getClients() {
        return clients;
    }

    public ExecutorService getClientThreads() {
        return clientThreads;
    }

    public void addClientHandler(ClientHandler clientHandler) {
        clientThreads.submit(clientHandler);
        clients.add(clientHandler);
    }
}
