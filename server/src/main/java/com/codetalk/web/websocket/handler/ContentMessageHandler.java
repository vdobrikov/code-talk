package com.codetalk.web.websocket.handler;

import com.codetalk.service.DocumentService;
import com.codetalk.web.websocket.ClientPool;
import com.codetalk.web.websocket.DocumentClient;
import com.codetalk.web.websocket.model.ContentMessage;
import com.codetalk.web.websocket.model.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
public class ContentMessageHandler implements MessageHandler {
    private static final Logger LOG = LoggerFactory.getLogger(ContentMessageHandler.class);

    private final ClientPool clientPool;
    private final DocumentService documentService;

    public ContentMessageHandler(ClientPool clientPool, DocumentService documentService) {
        this.clientPool = clientPool;
        this.documentService = documentService;
    }

    @Override
    public String getMessageType() {
        return ContentMessage.TYPE;
    }

    @Override
    public Mono<Void> handleMessage(Message<?> message, String sessionId) {
        Object data = message.getData();
        if (!(ContentMessage.TYPE.equals(message.getType()) && data instanceof String)) {
            LOG.warn("Unexpected message: message={}", message);
            return Mono.empty();
        }

        return clientPool.getBySessionId(sessionId)
                .map(DocumentClient::getDocumentId)
                .flatMap(documentService::findById)
                .doOnNext(document -> document.setContent((String) data))
                .flatMap(documentService::update)
                .then(clientPool.broadcast(message, sessionId));
    }
}
