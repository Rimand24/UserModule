package org.example.auth.repo;

import org.example.auth.domain.User;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepo extends PagingAndSortingRepository<User, Long> {
    User findByUsername(String username); //todo optional
    User findByEmail(String email);  //todo optional
}
