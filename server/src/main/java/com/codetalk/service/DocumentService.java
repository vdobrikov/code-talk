package com.codetalk.service;

import com.codetalk.model.Document;
import com.codetalk.repository.DocumentRepository;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Instant;

@Service
public class DocumentService {
    private final DocumentRepository documentRepository;

    public DocumentService(DocumentRepository documentRepository) {
        this.documentRepository = documentRepository;
    }

    public Flux<Document> findAll() {
        return documentRepository.findAll();
    }

    public Mono<Document> findById(String id) {
        return documentRepository.findById(id);
    }

    public Flux<Document> findByCreatedDateBefore(Instant date) {
        return documentRepository.findByCreatedDateBefore(date);
    }

    public Mono<Document> create(Document document) {
        document.setId(null);
        return documentRepository.save(document);
    }

    public Mono<Document> update(String id, Document document) {
        return documentRepository.findById(id)
                .map(current -> current.copyFrom(document))
                .flatMap(documentRepository::save);
    }

    public Mono<Document> update(Document document) {
        return documentRepository.save(document);
    }

    public Mono<Document> delete(String id) {
        return documentRepository.findById(id)
                .map(existing -> {
                    documentRepository.deleteById(id);
                    return existing;
                });
    }

    public Mono<Void> deleteAll(Flux<Document> documents) {
        return documentRepository.deleteAll(documents);
    }
}
