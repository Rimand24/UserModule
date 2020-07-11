package org.example.auth.service.user;

import org.example.auth.domain.User;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.List;

public interface UserService extends UserDetailsService {
    UserDto getUser(String username);

    List<UserDto> findAll();


//    UserDetails updatePassword(User user, String newPassword);
//    boolean resetPassword(String token);

//    UserDto getUserByEmail(String email);
//    UserDto getUserByUserId(String userId);
//
//    UserDto updateUser(String userId, UserDto userDto); //fixme dto consist userId, 1param is rudendant
//
//    List<UserDto> getUsers(int page, int limit);
//
//    boolean sendResetPasswordEmail(String email);
//
//    boolean changePassword(UserDto userDto);

}
