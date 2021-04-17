package com.codetalk.web.websocket;

import reactor.core.publisher.Flux;

public interface MessageUnicastService {
    void onNext(Message message);
    Flux<Message> getMessages();
}
