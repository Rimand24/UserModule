package org.example.auth.service.user;

import org.example.auth.domain.User;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.List;

public interface UserService extends UserDetailsService {
    UserDto getUserByUsername(String username);

    UserDto getUserByEmail(String email);

    List<UserDto> findAll();


//    UserDto updateUserInfo(String userId, UserDto userDto); //fixme dto consist userId, 1param is rudendant

//    List<UserDto> getUsers(int page, int limit);

}
