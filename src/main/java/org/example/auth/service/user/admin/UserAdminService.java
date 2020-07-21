package org.example.auth.service.user.admin;

import org.example.auth.domain.UserDto;

import java.util.List;

public interface UserAdminService {
    List<UserDto> findAllBlocked();

//    List<UserDto> findAllNotActivated();

    boolean blockUser(String username, String blocker, String reason);

    boolean unblockUser(String username);

    boolean deleteUser(String username);
}
