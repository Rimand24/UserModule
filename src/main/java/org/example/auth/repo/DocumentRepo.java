package org.example.auth.repo;

import org.example.auth.domain.Document;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DocumentRepo extends JpaRepository<Document, Long> {
    Document findByDocId(String docId);

    List<Document> findByNameContains(String name);

    List<Document> findByAuthor_Username(String name);
}