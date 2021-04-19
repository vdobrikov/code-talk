package com.codetalk.web.websocket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.reactive.socket.WebSocketMessage;
import org.springframework.web.reactive.socket.WebSocketSession;
import reactor.core.publisher.FluxSink;

import java.util.Objects;

public class DocumentClient {
    private static final Logger LOG = LoggerFactory.getLogger(DocumentClient.class);

    private final FluxSink<WebSocketMessage> sink;
    private final WebSocketSession session;
    private final String documentId;

    public DocumentClient(FluxSink<WebSocketMessage> sink, WebSocketSession session, String documentId) {
        Objects.requireNonNull(sink, "'sink' cannot be null");
        Objects.requireNonNull(session, "'session' cannot be null");
        Objects.requireNonNull(documentId, "'documentId' cannot be null");

        this.sink = sink;
        this.session = session;
        this.documentId = documentId;
    }

    public String getDocumentId() {
        return documentId;
    }

    public String getSessionId() {
        return session.getId();
    }

    public void sendData(String data) {
        LOG.debug("Send data: sessionId={}; data={}", getSessionId(), data);
        sink.next(session.textMessage(data));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        DocumentClient that = (DocumentClient) o;

        return session.getId().equals(that.session.getId());
    }

    @Override
    public int hashCode() {
        return session.getId().hashCode();
    }

    @Override
    public String toString() {
        return "DocumentClient{" +
                "sessionId=" + session.getId() +
                ", documentId='" + documentId + '\'' +
                '}';
    }
}
