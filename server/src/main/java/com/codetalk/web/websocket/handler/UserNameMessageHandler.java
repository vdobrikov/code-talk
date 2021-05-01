package com.codetalk.web.websocket.handler;

import com.codetalk.web.websocket.ClientPool;
import com.codetalk.web.websocket.model.Message;
import com.codetalk.web.websocket.model.UserNameMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
public class UserNameMessageHandler implements MessageHandler {
    private static final Logger LOG = LoggerFactory.getLogger(UserNameMessageHandler.class);

    private final ClientPool clientPool;

    public UserNameMessageHandler(ClientPool clientPool) {
        this.clientPool = clientPool;
    }

    @Override
    public String getMessageType() {
        return UserNameMessage.TYPE;
    }

    @Override
    public Mono<Void> handleMessage(Message<?> message, String sessionId) {
        Object data = message.getData();
        if (!(UserNameMessage.TYPE.equals(message.getType()) && data instanceof String)) {
            LOG.warn("Unexpected message: message={}", message);
            return Mono.empty();
        }

        return clientPool.getBySessionId(sessionId)
                .doOnNext(client -> client.setUserId(((String) data)))
                .then(clientPool.broadcast(message, sessionId))
                .then();
    }
}
