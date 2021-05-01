package com.codetalk.web.websocket;

import com.codetalk.web.websocket.model.UserNameMessage;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ClientPoolTest {
    private static final String SESSION_ID = "session-id";
    private static final String DOCUMENT_ID = "document-id";

    private ClientPool clientPool;

    @Mock
    private DocumentClient testClient;

    @BeforeEach
    public void setUp() {
        clientPool = new ClientPool();
        when(testClient.getSessionId()).thenReturn(SESSION_ID);
    }

    @Test
    void getByDocumentId() {
        when(testClient.getDocumentId()).thenReturn(DOCUMENT_ID);

        clientPool.add(testClient);
        Flux<DocumentClient> clientFlux = clientPool.getByDocumentId(DOCUMENT_ID);
        StepVerifier.create(clientFlux)
                .assertNext(client -> assertThat(client).isEqualTo(testClient))
                .verifyComplete();
    }

    @Test
    void getByDocumentIdNonexistentClient() {
        clientPool.add(testClient);
        Flux<DocumentClient> clientFlux = clientPool.getByDocumentId("non-existent");
        StepVerifier.create(clientFlux)
                .expectNextCount(0)
                .verifyComplete();
    }

    @Test
    void getBySessionId() {
        clientPool.add(testClient);
        Mono<DocumentClient> clientMono = clientPool.getBySessionId(SESSION_ID);
        StepVerifier.create(clientMono)
                .assertNext(client -> assertThat(client).isEqualTo(testClient))
                .verifyComplete();
    }

    @Test
    void getBySessionIdNonexistentClient() {
        clientPool.add(testClient);
        Mono<DocumentClient> clientMono = clientPool.getBySessionId("non-existent");
        StepVerifier.create(clientMono)
                .expectNextCount(0)
                .verifyComplete();
    }

    @Test
    void add() {
        assertThat(clientPool.getSize()).isEqualTo(0);
        clientPool.add(testClient);
        assertThat(clientPool.getSize()).isEqualTo(1);
    }

    @Test
    void remove() {
        clientPool.add(testClient);
        assertThat(clientPool.getSize()).isEqualTo(1);
        clientPool.remove(SESSION_ID);
        assertThat(clientPool.getSize()).isEqualTo(0);
    }

    @Test
    void getSize() {
        assertThat(clientPool.getSize()).isEqualTo(0);
        clientPool.add(testClient);
        assertThat(clientPool.getSize()).isEqualTo(1);
    }

    @Test
    void broadcast() {
        UserNameMessage message = new UserNameMessage("some-user");

        DocumentClient receiverClient = mock(DocumentClient.class);
        when(testClient.getDocumentId()).thenReturn("document-id");
        when(receiverClient.getDocumentId()).thenReturn("document-id");
        when(receiverClient.getSessionId()).thenReturn("other-session-id");
        doNothing().when(receiverClient).sendData(message);

        clientPool.add(testClient);
        clientPool.add(receiverClient);

        Mono<Void> result = clientPool.broadcast(message, SESSION_ID);

        StepVerifier.create(result)
                .verifyComplete();
        verify(receiverClient, times(1)).sendData(message);
    }

    @Test
    void broadcastIgnoreTheSameClient() {
        UserNameMessage message = new UserNameMessage("some-user");
        when(testClient.getDocumentId()).thenReturn("document-id");
        clientPool.add(testClient);

        Mono<Void> result = clientPool.broadcast(message, SESSION_ID);

        StepVerifier.create(result)
                .verifyComplete();
        verify(testClient, never()).sendData(any());
    }

    @Test
    void handleMessageNonexistentClient() {
        UserNameMessage message = new UserNameMessage("some-user");
        clientPool.add(testClient);

        Mono<Void> result = clientPool.broadcast(message, "non-existent");

        StepVerifier.create(result)
                .verifyComplete();
        verify(testClient, never()).sendData(any());
    }
}