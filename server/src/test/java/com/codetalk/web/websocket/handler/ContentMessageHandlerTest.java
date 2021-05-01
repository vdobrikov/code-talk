package com.codetalk.web.websocket.handler;

import com.codetalk.model.Document;
import com.codetalk.service.DocumentService;
import com.codetalk.web.websocket.ClientPool;
import com.codetalk.web.websocket.DocumentClient;
import com.codetalk.web.websocket.model.ContentMessage;
import com.codetalk.web.websocket.model.UserNameMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
class ContentMessageHandlerTest {
    private static final String SESSION_ID = "session-id";
    private static final String DOCUMENT_ID = "session-id";
    private static final String CONTENT = "some-content";

    private ContentMessageHandler sut;

    @Mock
    private ClientPool clientPool;
    @Mock
    private DocumentService documentService;
    @Mock
    private DocumentClient documentClient;

    private final Document document = new Document();
    private final ContentMessage message = new ContentMessage(CONTENT);


    @BeforeEach
    public void setUp() {
        sut = new ContentMessageHandler(clientPool, documentService);
    }

    @Test
    void testGetMessageType() {
        assertThat(sut.getMessageType()).isEqualTo(ContentMessage.TYPE);
    }

    @Test
    void testHandle() {
        when(documentClient.getSessionId()).thenReturn(SESSION_ID);
        when(documentClient.getDocumentId()).thenReturn(DOCUMENT_ID);
        when(clientPool.getBySessionId(SESSION_ID)).thenReturn(Mono.just(documentClient));
        when(documentService.findById(DOCUMENT_ID)).thenReturn(Mono.just(document));
        when(documentService.update(document)).thenReturn(Mono.just(document));

        Mono<Void> mono = sut.handleMessage(message, SESSION_ID);

        StepVerifier.create(mono)
                .verifyComplete();
        assertThat(document.getContent()).isEqualTo(CONTENT);
    }

    @Test
    void testHandleWrongMessageType() {
        UserNameMessage messageWithWrongType = new UserNameMessage("some-user");
        Mono<Void> mono = sut.handleMessage(messageWithWrongType, SESSION_ID);

        StepVerifier.create(mono)
                .verifyComplete();
        verify(clientPool, never()).broadcast(any(), any());
        assertThat(document.getContent()).isEqualTo("");
    }

    @Test
    void testHandleWrongMessageDataType() {
        ContentMessage messageWithWrongDataType = new ContentMessage(null);
        Mono<Void> mono = sut.handleMessage(messageWithWrongDataType, SESSION_ID);

        StepVerifier.create(mono)
                .verifyComplete();
        verify(clientPool, never()).broadcast(any(), any());
        assertThat(document.getContent()).isEqualTo("");
    }

    @Test
    void testHandleNonexistentDocument() {
        when(documentClient.getSessionId()).thenReturn(SESSION_ID);
        when(documentClient.getDocumentId()).thenReturn(DOCUMENT_ID);
        when(clientPool.getBySessionId(SESSION_ID)).thenReturn(Mono.empty());

        Mono<Void> mono = sut.handleMessage(message, SESSION_ID);

        StepVerifier.create(mono)
                .verifyComplete();
        assertThat(document.getContent()).isEqualTo("");
    }
}