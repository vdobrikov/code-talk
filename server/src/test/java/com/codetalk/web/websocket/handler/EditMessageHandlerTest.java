package com.codetalk.web.websocket.handler;

import com.codetalk.model.Document;
import com.codetalk.web.websocket.ClientPool;
import com.codetalk.web.websocket.DocumentClient;
import com.codetalk.web.websocket.model.EditMessage;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
class EditMessageHandlerTest {
    private static final String SESSION_ID = "session-id";
    private static final String DOCUMENT_ID = "session-id";
    private static final String CONTENT = "some-content";

    private EditMessageHandler sut;

    @Mock
    private ClientPool clientPool;
    @Mock
    private DocumentClient documentClient;

    @Captor
    private ArgumentCaptor<String> dataCaptor;

    private final ObjectMapper objectMapper = new ObjectMapper();
    private EditMessage message;

    @BeforeEach
    void setUp() {
        sut = new EditMessageHandler(clientPool);
        ObjectNode node = objectMapper.createObjectNode().put("some-key", "some-value");
        message = new EditMessage(node);

    }

    @Test
    void getMessageType() {
        assertThat(sut.getMessageType()).isEqualTo(EditMessage.TYPE);
    }

    @Test
    void handleMessage() throws JsonProcessingException {
        String stringNode = objectMapper.writeValueAsString(message);

        when(documentClient.getSessionId()).thenReturn("another-session-id");
        when(documentClient.getDocumentId()).thenReturn(DOCUMENT_ID);
        doNothing().when(documentClient).sendData(dataCaptor.capture());

        when(clientPool.getBySessionId(SESSION_ID)).thenReturn(Mono.just(documentClient));
        when(clientPool.getByDocumentId(DOCUMENT_ID)).thenReturn(Flux.just(documentClient));

        Mono<Void> mono = sut.handleMessage(message, SESSION_ID);

        StepVerifier.create(mono)
                .verifyComplete();
        verify(documentClient).sendData(dataCaptor.capture());
        assertThat(dataCaptor.getValue()).isEqualTo(stringNode);
    }

    @Test
    void handleMessageIgnoreTheSameClient() {
        when(documentClient.getSessionId()).thenReturn(SESSION_ID);
        when(documentClient.getDocumentId()).thenReturn(DOCUMENT_ID);

        when(clientPool.getBySessionId(SESSION_ID)).thenReturn(Mono.just(documentClient));
        when(clientPool.getByDocumentId(DOCUMENT_ID)).thenReturn(Flux.just(documentClient));

        Mono<Void> mono = sut.handleMessage(message, SESSION_ID);

        StepVerifier.create(mono)
                .verifyComplete();
        verify(documentClient, never()).sendData(any());
    }

    @Test
    void handleMessageNonexistentClient() {
        when(documentClient.getSessionId()).thenReturn(SESSION_ID);
        when(documentClient.getDocumentId()).thenReturn(DOCUMENT_ID);

        when(clientPool.getBySessionId(SESSION_ID)).thenReturn(Mono.empty());

        Mono<Void> mono = sut.handleMessage(message, SESSION_ID);

        StepVerifier.create(mono)
                .verifyComplete();
        verify(documentClient, never()).sendData(any());
    }

    @Test
    void testHandleWrongMessageType() {
        message.setType("wrong-type");
        Mono<Void> mono = sut.handleMessage(message, SESSION_ID);

        StepVerifier.create(mono)
                .verifyComplete();
    }

    @Test
    void testHandleWrongMessageDataType() {
        message.setData(null);
        Mono<Void> mono = sut.handleMessage(message, SESSION_ID);

        StepVerifier.create(mono)
                .verifyComplete();
    }
}