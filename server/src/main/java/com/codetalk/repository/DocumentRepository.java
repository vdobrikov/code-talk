package com.codetalk.repository;

import com.codetalk.model.Document;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

import java.time.Instant;

@Repository
public interface DocumentRepository extends ReactiveCrudRepository<Document, String> {
    Flux<Document> findByCreatedDateBefore(Instant date);
}
