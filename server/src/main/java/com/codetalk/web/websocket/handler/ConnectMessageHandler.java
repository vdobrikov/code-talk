package com.codetalk.web.websocket.handler;

import com.codetalk.web.websocket.ClientPool;
import com.codetalk.web.websocket.model.ConnectMessage;
import com.codetalk.web.websocket.model.ConnectionDetails;
import com.codetalk.web.websocket.model.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
public class ConnectMessageHandler implements MessageHandler {
    private static final Logger LOG = LoggerFactory.getLogger(ConnectMessageHandler.class);

    private final ClientPool clientPool;

    public ConnectMessageHandler(ClientPool clientPool) {
        this.clientPool = clientPool;
    }

    @Override
    public String getMessageType() {
        return ConnectMessage.TYPE;
    }

    @Override
    public Mono<Void> handleMessage(Message<?> message, String sessionId) {
        Object data = message.getData();
        if (!(ConnectMessage.TYPE.equals(message.getType()) && data instanceof ConnectionDetails)) {
            LOG.warn("Unexpected message: message={}", message);
            return Mono.empty();
        }
        return clientPool.getBySessionId(sessionId)
                .doOnNext(client -> client.setUserId(((ConnectionDetails) data).getUserName()))
                .then(clientPool.broadcast(message, sessionId))
                .then();
    }
}
