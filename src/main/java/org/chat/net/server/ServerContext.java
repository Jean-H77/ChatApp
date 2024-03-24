package org.chat.net.server;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public enum ServerContext {
    INSTANCE;

    private static final int MAX_CLIENT_THREADS = 100;

    private final Set<ClientHandler> clients = ConcurrentHashMap.newKeySet();

    private final ExecutorService clientThreads = Executors.newFixedThreadPool(MAX_CLIENT_THREADS);

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
