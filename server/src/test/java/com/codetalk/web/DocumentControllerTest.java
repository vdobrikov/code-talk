package com.codetalk.web;

import com.codetalk.WebSecurityConfiguration;
import com.codetalk.model.Document;
import com.codetalk.repository.DocumentRepository;
import com.codetalk.service.DocumentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.web.reactive.function.BodyInserters.fromValue;

@ExtendWith(SpringExtension.class)
@WebFluxTest(controllers = DocumentController.class)
@Import(WebSecurityConfiguration.class)
public class DocumentControllerTest {
    private static final String ID = "some-id";
    private static final String TITLE = "some-title";
    private static final String MODE = "some-mode";
    private static final String CODE = "some-code";

    @Autowired
    private WebTestClient webTestClient;

    @MockBean
    private DocumentService documentService;

    @MockBean
    private DocumentRepository documentRepository;

    private Document document;

    @BeforeEach
    void setUp() {
        document = new Document(TITLE, MODE, CODE);
        document.setId(ID);
    }

    @Test
    public void testFindAll() {
        when(documentService.findAll())
                .thenReturn(Flux.just(document));
        webTestClient
                .get().uri("/api/documents")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$[0].title").isEqualTo(TITLE)
                .jsonPath("$[0].syntax").isEqualTo(MODE)
                .jsonPath("$[0].code").isEqualTo(CODE);
    }

    @Test
    public void testFindById() {
        when(documentService.findById(ID))
                .thenReturn(Mono.just(document));
        webTestClient
                .get().uri("/api/documents/some-id")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.title").isEqualTo(TITLE)
                .jsonPath("$.syntax").isEqualTo(MODE)
                .jsonPath("$.code").isEqualTo(CODE);
    }

    @Test
    public void testFindByIdNonExistent() {
        when(documentService.findById(ID))
                .thenReturn(Mono.empty());
        webTestClient
                .get().uri("/api/documents/some-id")
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    public void testCreate() {
        when(documentService.create(any(Document.class)))
                .thenReturn(Mono.just(document));
        webTestClient
                .post().uri("/api/documents")
                .contentType(MediaType.APPLICATION_JSON)
                .body(fromValue(document))
                .exchange()
                .expectStatus().isCreated()
                .expectBody()
                .jsonPath("$.title").isEqualTo(TITLE)
                .jsonPath("$.syntax").isEqualTo(MODE)
                .jsonPath("$.code").isEqualTo(CODE);
    }

    @Test
    public void testUpdate() {
        when(documentService.update(eq(ID), any(Document.class)))
                .thenReturn(Mono.just(document));
        webTestClient
                .put().uri("/api/documents/some-id")
                .contentType(MediaType.APPLICATION_JSON)
                .body(fromValue(document))
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.title").isEqualTo(TITLE)
                .jsonPath("$.syntax").isEqualTo(MODE)
                .jsonPath("$.code").isEqualTo(CODE);
    }

    @Test
    public void testUpdateNonExistent() {
        when(documentService.update(eq(ID), any(Document.class)))
                .thenReturn(Mono.empty());
        webTestClient
                .put().uri("/api/documents/some-id")
                .contentType(MediaType.APPLICATION_JSON)
                .body(fromValue(document))
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    public void testDelete() {
        when(documentService.delete(ID))
                .thenReturn(Mono.just(document));
        webTestClient
                .delete().uri("/api/documents/some-id")
                .exchange()
                .expectStatus().isOk();
    }

    @Test
    public void testDeleteNonExistent() {
        when(documentService.delete(ID))
                .thenReturn(Mono.empty());
        webTestClient
                .delete().uri("/api/documents/some-id")
                .exchange()
                .expectStatus().isNotFound();
    }

}