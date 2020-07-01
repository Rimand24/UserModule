package org.example.auth.repo;

import org.example.auth.domain.Document;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DocumentRepo extends JpaRepository<Document, Long> {
    Document findByHashedName(String name);

}