package com.codetalk.repository;

import com.codetalk.model.Document;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DocumentRepository extends ReactiveCrudRepository<Document, String> {

}
