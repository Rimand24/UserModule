package org.example.auth.repo;

import org.example.auth.domain.Tag;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TagRepo extends CrudRepository<Tag, Long> {
}
