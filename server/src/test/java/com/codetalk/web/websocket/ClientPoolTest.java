package com.codetalk.web.websocket;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.assertj.core.api.Assertions.assertThat;

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
        Mockito.when(testClient.getSessionId()).thenReturn(SESSION_ID);
    }

    @Test
    void getByDocumentId() {
        Mockito.when(testClient.getDocumentId()).thenReturn(DOCUMENT_ID);

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
}