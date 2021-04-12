package com.codetalk.web;

import com.codetalk.model.Document;
import com.codetalk.service.DocumentService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController()
@RequestMapping("/api/documents")
public class DocumentController {
    private final DocumentService documentService;

    public DocumentController(DocumentService documentService) {
        this.documentService = documentService;
    }

    @GetMapping
    public Flux<Document> getAll() {
        return documentService.findAll();
    }

    @GetMapping("/{id}")
    public Mono<Document> getById(@PathVariable("id") String id) {
        return documentService.findById(id)
                .switchIfEmpty(Mono.error(() -> new NotFoundResponseStatusException(id)));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<Document> create(@RequestBody Document document) {
        return documentService.create(document);
    }

    @PutMapping("/{id}")
    public Mono<Document> update(@PathVariable("id") String id, @RequestBody Document document) {
        return documentService.update(id, document)
                .switchIfEmpty(Mono.error(() -> new NotFoundResponseStatusException(id)));
    }

    @DeleteMapping("/{id}")
    public Mono<Document> delete(@PathVariable("id") String id) {
        return documentService.delete(id)
                .switchIfEmpty(Mono.error(() -> new NotFoundResponseStatusException(id)));
    }
}
