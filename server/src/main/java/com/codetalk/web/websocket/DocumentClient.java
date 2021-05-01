package com.codetalk.web.websocket;

import com.codetalk.web.websocket.model.Message;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.reactive.socket.WebSocketMessage;
import org.springframework.web.reactive.socket.WebSocketSession;
import reactor.core.publisher.FluxSink;

import java.util.Objects;

public class DocumentClient {
    private static final Logger LOG = LoggerFactory.getLogger(DocumentClient.class);

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final FluxSink<WebSocketMessage> sink;
    private final WebSocketSession session;
    private final String documentId;
    private String userId;

    public DocumentClient(FluxSink<WebSocketMessage> sink, WebSocketSession session, String documentId) {
        this(sink, session, documentId, null);
    }

    public DocumentClient(FluxSink<WebSocketMessage> sink, WebSocketSession session, String documentId, String userId) {
        Objects.requireNonNull(sink, "'sink' cannot be null");
        Objects.requireNonNull(session, "'session' cannot be null");
        Objects.requireNonNull(documentId, "'documentId' cannot be null");

        this.sink = sink;
        this.session = session;
        this.documentId = documentId;
        this.userId = userId;
    }

    public String getDocumentId() {
        return documentId;
    }

    public String getSessionId() {
        return session.getId();
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void sendData(Message<?> message) {
        LOG.debug("Send message: sessionId={}; message={}", getSessionId(), message);
        try {
            String text = objectMapper.writeValueAsString(message);
            sink.next(session.textMessage(text));
        } catch (JsonProcessingException e) {
            throw new SendMessageException(e);
        }
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
                ", userName='" + userId + '\'' +
                '}';
    }
}
