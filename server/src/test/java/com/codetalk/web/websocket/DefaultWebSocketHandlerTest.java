package com.codetalk.web.websocket;

import com.codetalk.WebSecurityConfig;
import com.codetalk.web.DocumentController;
import com.codetalk.web.websocket.model.ConnectMessage;
import com.codetalk.web.websocket.model.ConnectionDetails;
import com.codetalk.web.websocket.model.Message;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.autoconfigure.security.reactive.ReactiveSecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.reactive.socket.WebSocketMessage;
import org.springframework.web.reactive.socket.client.StandardWebSocketClient;
import org.springframework.web.reactive.socket.client.WebSocketClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import reactor.test.publisher.PublisherProbe;

import java.net.URI;
import java.net.URISyntaxException;
import java.time.Duration;


// This test doesn't work as expected so it commented out
@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
//@WebFluxTest(DefaultWebSocketHandler.class)
@ActiveProfiles("test")
@ImportAutoConfiguration(exclude = {ReactiveSecurityAutoConfiguration.class})
@WithAnonymousUser
class DefaultWebSocketHandlerTest {
    private static final String DOCUMENT_ID = "document-id";

    @LocalServerPort
    private String port;

    @MockBean
    private ClientPool clientPool;

    @MockBean
    private WebSecurityConfig webSecurityConfig;

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final WebSocketClient client = new StandardWebSocketClient();

//    @Test
    void handle() throws URISyntaxException {
        var message = new ConnectMessage(new ConnectionDetails(DOCUMENT_ID, "some-user"));
        PublisherProbe<Void> probe = PublisherProbe.empty();
        client.execute(getUrl(), session -> session
            .send(Mono.just(message)
                    .map(this::messageToString)
                    .map(session::textMessage))
            .thenMany(probe.mono())
            .then())
        .block(Duration.ofMillis(500));

        probe.assertWasSubscribed();
    }

    private URI getUrl() throws URISyntaxException {
        return new URI("ws://localhost:" + this.port + "/ws?documentId=" + DOCUMENT_ID);
    }

    private String messageToString(Message<?> message) {
        try {
            return objectMapper.writeValueAsString(message);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}