package org.example.auth.service.user.search;

import org.example.auth.domain.UserDto;

import java.util.List;

public interface UserSearchService {
    UserDto getUserByUsername(String username);

    UserDto getUserByEmail(String email);

    List<UserDto> findAll();

    List<UserDto> findAllActivated();

    List<UserDto> findAllNotActivated();

    List<UserDto> findAllBlocked();

    List<UserDto> searchUsersByName(String name);

//    List<UserDto> getUsers(int page, int limit);

}
