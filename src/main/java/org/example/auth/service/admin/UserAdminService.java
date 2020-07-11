package org.example.auth.service.admin;

import org.example.auth.service.user.UserDto;

import java.util.List;

public interface UserAdminService {
    List<UserDto> findAllBlocked();

//    List<UserDto> findAllNotActivated();

    boolean blockUser(String username, String blocker, String reason);

    boolean unblockUser(String username);

    boolean deleteUser(String username);
}
