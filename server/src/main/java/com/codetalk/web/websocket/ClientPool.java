package com.codetalk.web.websocket;

import com.codetalk.web.websocket.model.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.concurrent.ConcurrentHashMap;

@Component
public class ClientPool {
    private static final Logger LOG = LoggerFactory.getLogger(ClientPool.class);

    private final ConcurrentHashMap<String, DocumentClient> sessionsToClients = new ConcurrentHashMap<>();

    public Flux<DocumentClient> getByDocumentId(String documentId) {
        return Flux.fromIterable(sessionsToClients.values())
                .filter(client -> documentId.equals(client.getDocumentId()));
    }

    public Mono<String> getUserIdBySessionId(String sessionId) {
        return getBySessionId(sessionId)
                .handle((client, sink) -> {
                    if (client.getUserId() != null) {
                        sink.next(client.getUserId());
                    }
                });

    }

    public Mono<DocumentClient> getBySessionId(String sessionId) {
        DocumentClient client = sessionsToClients.get(sessionId);
        return client == null ? Mono.empty() : Mono.just(client);
    }

    public void add(DocumentClient client) {
        LOG.debug("Add new client: sessionId={}", client.getSessionId());
        sessionsToClients.putIfAbsent(client.getSessionId(), client);
        logSize();
    }

    public void remove(String sessionId) {
        LOG.debug("Remove client: sessionId={}", sessionId);
        sessionsToClients.remove(sessionId);
        logSize();
    }

    public Mono<Void> broadcast(Message<?> message, String senderSessionId) {
        return getBySessionId(senderSessionId)
                .map(DocumentClient::getDocumentId)
                .flatMapMany(this::getByDocumentId)
                .filter(client -> !client.getSessionId().equals(senderSessionId))
                .doOnNext(client -> client.sendData(message))
                .then();
    }

    public int getSize() {
        return sessionsToClients.size();
    }

    private void logSize() {
        LOG.debug("clients.size={}", getSize());
        LOG.debug("clients={}", sessionsToClients);
    }
}
