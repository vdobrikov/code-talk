package com.codetalk.web.websocket;

import com.codetalk.service.DocumentService;
import com.codetalk.web.websocket.model.ConnectMessage;
import com.codetalk.web.websocket.model.ContentMessage;
import com.codetalk.web.websocket.model.EditMessage;
import com.codetalk.web.websocket.model.Message;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.socket.WebSocketHandler;
import org.springframework.web.reactive.socket.WebSocketMessage;
import org.springframework.web.reactive.socket.WebSocketSession;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Flux;
import reactor.core.publisher.FluxSink;
import reactor.core.publisher.Mono;

import java.net.URI;

@Component
public class DefaultWebSocketHandler implements WebSocketHandler {
    private static final Logger LOG = LoggerFactory.getLogger(DefaultWebSocketHandler.class);
    private static final String PARAM_DOCUMENTID = "documentId";

    private final DocumentService documentService;
    private final ClientPool clientPool;

    private final ObjectMapper objectMapper = new ObjectMapper();

    public DefaultWebSocketHandler(DocumentService documentService, ClientPool clientPool) {
        this.documentService = documentService;
        this.clientPool = clientPool;
    }

    @Override
    public Mono<Void> handle(WebSocketSession session) {
        LOG.info("New websocket connection: session.id={}", session.getId());
        URI uri = session.getHandshakeInfo().getUri();
        String documentId = UriComponentsBuilder.fromUri(uri).build().getQueryParams().get(PARAM_DOCUMENTID)
                .stream()
                .findFirst()
                .orElseThrow(() -> new RuntimeException("'documentId' was not found in URI"));

        Mono<Void> output = session.send(Flux.create(sink -> registerConnection(session, sink, documentId)));

        Mono<Void> input = session.receive()
                .map(WebSocketMessage::getPayloadAsText)
                .doOnNext(LOG::debug)
                .flatMap(this::stringToMessage)
                .flatMap(message -> handleMessage(message, session))
                .doOnComplete(() -> clientPool.remove(session.getId()))
                .doOnCancel(() -> clientPool.remove(session.getId()))
                .doOnError(e -> LOG.error("doOnError", e))
                .then();

        return Mono.zip(input, output).then();
    }

    private Mono<Void> handleMessage(Message<?> message, WebSocketSession session) {
        Object data = message.getData();
        switch (message.getType()) {
            case ContentMessage.TYPE:
                // Save changes into document
                if (data instanceof String) {
                    return clientPool.getBySessionId(session.getId())
                            .map(DocumentClient::getDocumentId)
                            .flatMap(documentService::findById)
                            .doOnNext(document -> document.setContent((String) data))
                            .flatMap(documentService::update)
                            .then();
                }
                break;
            case EditMessage.TYPE:
                // Broadcast document changes
                if (data instanceof JsonNode) {
                    return clientPool.getBySessionId(session.getId())
                            .map(DocumentClient::getDocumentId)
                            .flatMapMany(clientPool::getByDocumentId)
                            .filter(client -> !client.getSessionId().equals(session.getId()))
                            .doOnNext(client -> client.sendData(messageToString(message)))
                            .then();
                }
            case ConnectMessage.TYPE:
                break;
            default:
                LOG.warn("Unknown message: message={}", message);
                break;
        }
        return Mono.empty();
    }

    private void registerConnection(WebSocketSession session, FluxSink<WebSocketMessage> sink, String documentId) {
        LOG.info("Register connection: session.id={}", session.getId());
        clientPool.add(new DocumentClient(sink, session, documentId));
    }

    private String messageToString(Message<?> message) {
        try {
            return objectMapper.writeValueAsString(message);
        } catch (JsonProcessingException e) {
            LOG.error("Failed to serialize message: message={}", message, e);
            throw new RuntimeException(e);
        }
    }

    private Mono<Message<?>> stringToMessage(String str) {
        try {
            return Mono.just(objectMapper.readValue(str, Message.class));
        } catch (JsonProcessingException e) {
            LOG.error("Failed to deserialize message: message={}", str, e);
            return Mono.error(e);
        }
    }
}
