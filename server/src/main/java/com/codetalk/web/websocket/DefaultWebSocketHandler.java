package com.codetalk.web.websocket;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.socket.WebSocketHandler;
import org.springframework.web.reactive.socket.WebSocketMessage;
import org.springframework.web.reactive.socket.WebSocketSession;
import reactor.core.publisher.Mono;

@Component
public class DefaultWebSocketHandler implements WebSocketHandler {
    private static final Logger LOG = LoggerFactory.getLogger(DefaultWebSocketHandler.class);

    private final ObjectMapper objectMapper = new ObjectMapper();

    private final MessageUnicastService unicastService;

    public DefaultWebSocketHandler(MessageUnicastService unicastService) {
        this.unicastService = unicastService;
    }

    @Override
    public Mono<Void> handle(WebSocketSession session) {
        LOG.info("New websocket connection: session.id={}", session.getId());
        session.send(unicastService.getMessages().flatMap(this::messageToString).map(session::textMessage));
        return session.receive()
                .map(WebSocketMessage::getPayloadAsText)
                .doOnNext(LOG::info)
                .flatMap(this::stringToMessage)
                .doOnNext(unicastService::onNext)
                .then();
    }

    public Mono<Message> stringToMessage(String str) {
        try {
            return Mono.just(objectMapper.readValue(str, Message.class));
        } catch (JsonProcessingException e) {
            LOG.error("Failed to deserialize message: message={}", str, e);
            return Mono.error(e);
        }
    }

    public Mono<String> messageToString(Message message) {
        try {
            return Mono.just(objectMapper.writeValueAsString(message));
        } catch (JsonProcessingException e) {
            LOG.error("Failed to serialize message: message={}", message, e);
            return Mono.error(e);
        }
    }
}
