package com.codetalk.web.websocket.handler;

import com.codetalk.web.websocket.ClientPool;
import com.codetalk.web.websocket.DocumentClient;
import com.codetalk.web.websocket.model.ContentMessage;
import com.codetalk.web.websocket.model.EditMessage;
import com.codetalk.web.websocket.model.Message;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
public class EditMessageHandler implements MessageHandler {
    private static final Logger LOG = LoggerFactory.getLogger(EditMessageHandler.class);

    private final ClientPool clientPool;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public EditMessageHandler(ClientPool clientPool) {
        this.clientPool = clientPool;
    }

    @Override
    public String getMessageType() {
        return EditMessage.TYPE;
    }

    @Override
    public Mono<Void> handleMessage(Message<?> message, String sessionId) {
        Object data = message.getData();
        if (!(EditMessage.TYPE.equals(message.getType()) && data instanceof JsonNode)) {
            LOG.warn("Unexpected message: message={}", message);
            return Mono.empty();
        }

        return clientPool.getBySessionId(sessionId)
                .map(DocumentClient::getDocumentId)
                .flatMapMany(clientPool::getByDocumentId)
                .filter(client -> !client.getSessionId().equals(sessionId))
                .doOnNext(client -> client.sendData(messageToString(message)))
                .then();
    }

    private String messageToString(Message<?> message) {
        try {
            return objectMapper.writeValueAsString(message);
        } catch (JsonProcessingException e) {
            LOG.error("Failed to serialize message: message={}", message, e);
            throw new RuntimeException(e);
        }
    }
}
