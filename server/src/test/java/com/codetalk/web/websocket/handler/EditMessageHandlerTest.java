package com.codetalk.web.websocket.handler;

import com.codetalk.web.websocket.ClientPool;
import com.codetalk.web.websocket.model.EditMessage;
import com.codetalk.web.websocket.model.Message;
import com.codetalk.web.websocket.model.UserNameMessage;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
class EditMessageHandlerTest {
    private static final String SESSION_ID = "session-id";

    private EditMessageHandler sut;

    @Mock
    private ClientPool clientPool;

    @Captor
    private ArgumentCaptor<Message<?>> dataCaptor;

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
    void handleMessage() {
        when(clientPool.broadcast(dataCaptor.capture(), eq(SESSION_ID))).thenReturn(Mono.empty());

        Mono<Void> mono = sut.handleMessage(message, SESSION_ID);

        StepVerifier.create(mono)
                .verifyComplete();
        verify(clientPool).broadcast(dataCaptor.capture(), eq(SESSION_ID));
        assertThat(dataCaptor.getValue()).isEqualTo(message);
    }

    @Test
    void testHandleWrongMessageType() {
        UserNameMessage messageWithWrongType = new UserNameMessage("some-user");
        Mono<Void> mono = sut.handleMessage(messageWithWrongType, SESSION_ID);

        StepVerifier.create(mono)
                .verifyComplete();
        verify(clientPool, never()).broadcast(any(), any());
    }

    @Test
    void testHandleWrongMessageDataType() {
        EditMessage messageWithWrongData = new EditMessage(null);
        Mono<Void> mono = sut.handleMessage(messageWithWrongData, SESSION_ID);

        StepVerifier.create(mono)
                .verifyComplete();
        verify(clientPool, never()).broadcast(any(), any());
    }
}