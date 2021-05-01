package com.codetalk.web.websocket.handler;

import com.codetalk.web.websocket.ClientPool;
import com.codetalk.web.websocket.model.EditMessage;
import com.codetalk.web.websocket.model.Message;
import com.fasterxml.jackson.databind.JsonNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
public class EditMessageHandler implements MessageHandler {
    private static final Logger LOG = LoggerFactory.getLogger(EditMessageHandler.class);

    private final ClientPool clientPool;

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

        return clientPool.broadcast(message, sessionId);
    }
}
