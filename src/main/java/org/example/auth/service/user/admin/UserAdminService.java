package org.example.auth.service.user.admin;

import org.example.auth.domain.Role;
import org.example.auth.service.user.admin.dto.UserAdminResponse;
import org.example.auth.service.user.admin.dto.UserBlockRequest;

public interface UserAdminService {

    UserAdminResponse blockUser(UserBlockRequest blockRequest);

    UserAdminResponse unblockUser(String username);

    UserAdminResponse deleteUser(String username);

    UserAdminResponse deleteUserOnly(String username);

    UserAdminResponse addUserRole(String username, Role role);

    UserAdminResponse removeUserRole(String username, Role role);
}
