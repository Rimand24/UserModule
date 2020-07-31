package org.rimand.doc.repo;

import org.rimand.doc.domain.Document;
import org.rimand.doc.domain.Tag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DocumentRepo extends JpaRepository<Document, Long> {
    Optional<Document> findByDocId(String docId);

    List<Document> findByUploader_Username(String name);

    List<Document> findByDocNameContains(String name);

    List<Document> findByUploader_UsernameContains(String username);

    List<Document> findByDocNameContainsAndUploader_UsernameContains(String docName, String username);

    List<Document>findByTagsContaining(Tag tag);
}