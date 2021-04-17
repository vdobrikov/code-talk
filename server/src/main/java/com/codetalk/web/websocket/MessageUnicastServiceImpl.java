package com.codetalk.web.websocket;

import org.springframework.stereotype.Service;
import reactor.core.publisher.EmitterProcessor;
import reactor.core.publisher.Flux;

@Service
public class MessageUnicastServiceImpl implements MessageUnicastService {

    private EmitterProcessor<Message> emitterProcessor = EmitterProcessor.create(); //TODO: Replace with sink (https://projectreactor.io/docs/core/release/api/reactor/core/publisher/EmitterProcessor.html)

    @Override
    public void onNext(Message message) {
        emitterProcessor.onNext(message);
    }

    @Override
    public Flux<Message> getMessages() {
        return emitterProcessor.publish().autoConnect();
    }
}
