package org.rimand.doc.repo;


import org.rimand.doc.domain.Role;
import org.rimand.doc.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepo extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);

    Optional<User> findByEmail(String email);

    Optional<User> findByEmailActivationCode(String activationCode);

    Optional<User> findByPasswordResetCode(String code);

    List<User> findAllByAccountNonLockedFalse(); //blocked users

    List<User> findAllByEnabledTrue();  //activated users

    List<User> findAllByEnabledFalse();  //not activated users

    List<User> findByAuthoritiesContains(Role role);

    List<User> findAllByUsernameContains(String name);
}
