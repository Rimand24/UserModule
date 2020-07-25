package org.example.auth.service.user.search;

import org.example.auth.domain.UserDto;

import java.util.List;
import java.util.Optional;

public interface UserSearchService {
    Optional<UserDto> findByUsername(String username);

    Optional<UserDto> findByEmail(String email);

    List<UserDto> findAll();

    List<UserDto> findAllActivated();

    List<UserDto> findAllNotActivated();

    List<UserDto> findAllBlocked();

    List<UserDto> searchUsersByName(String name);

}
