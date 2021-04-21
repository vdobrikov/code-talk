package com.codetalk.web.websocket.handler;

import com.codetalk.web.websocket.model.Message;
import reactor.core.publisher.Mono;

public interface MessageHandler {
    String getMessageType();
    Mono<Void> handleMessage(Message<?> message, String sessionId);
}
