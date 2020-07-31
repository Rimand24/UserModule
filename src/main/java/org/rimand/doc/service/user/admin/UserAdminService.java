package org.rimand.doc.service.user.admin;

import lombok.extern.slf4j.Slf4j;
import org.rimand.doc.domain.Document;
import org.rimand.doc.domain.Role;
import org.rimand.doc.domain.User;
import org.rimand.doc.repo.UserRepo;
import org.rimand.doc.service.document.DocumentService;
import org.rimand.doc.service.user.admin.dto.UserAdminResponse;
import org.rimand.doc.service.user.admin.dto.UserBlockRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.rimand.doc.service.user.admin.dto.UserAdminServiceResponseCode.*;


@Slf4j
@Service
public class UserAdminService {

    private final UserRepo userRepo;
    private final DocumentService documentService;

    @Autowired
    public UserAdminService(UserRepo userRepo, DocumentService documentService) {
        this.userRepo = userRepo;
        this.documentService = documentService;
    }


    public UserAdminResponse blockUser(@Valid UserBlockRequest request) {
        Optional<User> optional = userRepo.findByUsername(request.getUsername());
        if (optional.isEmpty()) {
            log.debug("User block error:{}:{}", USERNAME_NOT_FOUND, request.getUsername());
            return new UserAdminResponse(USERNAME_NOT_FOUND);
        }

        User user = optional.get();
        if (!user.isAccountNonLocked()) {
            log.debug("User block error:{}:{}", USER_ALREADY_BLOCKED, request.getUsername());
            return new UserAdminResponse(USER_ALREADY_BLOCKED);
        }
        user.setAccountNonLocked(false);
        user.setAccountBlockerName(request.getBlocker());
        user.setAccountBlockReason(request.getReason());
        user.setAccountBlockDate(LocalDate.now());
        userRepo.save(user);

        log.info("User blocked:(username:{}, email:{})", user.getUsername(), user.getEmail());
        return new UserAdminResponse(OK);
    }

    public UserAdminResponse unblockUser(String username) {
        Optional<User> optional = userRepo.findByUsername(username);
        if (optional.isEmpty()) {
            log.debug("User unblock error:{}:{}", USERNAME_NOT_FOUND, username);
            return new UserAdminResponse(USERNAME_NOT_FOUND);
        }

        User user = optional.get();
        if (user.isAccountNonLocked()) {
            log.debug("User unblock error:{}:{}", USER_NOT_BLOCKED, username);
            return new UserAdminResponse(USER_NOT_BLOCKED);
        }

        user.setAccountNonLocked(true);
        user.setAccountBlockerName(null);
        user.setAccountBlockReason(null);
        user.setAccountBlockDate(null);
        userRepo.save(user);

        log.info("User unblocked:(username:{}, email:{})", user.getUsername(), user.getEmail());
        return new UserAdminResponse(OK);
    }

    public UserAdminResponse deleteUserWithUploads(String username) {
        Optional<User> optional = userRepo.findByUsername(username);
        if (optional.isEmpty()) {
            log.debug("User delete error:{}:{}", USERNAME_NOT_FOUND, username);
            return new UserAdminResponse(USERNAME_NOT_FOUND);
        }

        User user = optional.get();
        List<Document> docs = user.getCreatedDocuments();
        if (!docs.isEmpty()) {
            docs.forEach(document -> documentService.delete(document.getDocId(), new User() {{ //fixme костыль c User.Roles
                Set<Role> roles = new HashSet<>();
                roles.add(Role.ROLE_ADMIN_DOCUMENT);
                setAuthorities(roles);
            }}));
        }
        userRepo.delete(user);

        log.info("User deleted:(username:{}, email:{})", user.getUsername(), user.getEmail());
        return new UserAdminResponse(OK);
    }

    public UserAdminResponse deleteUserOnly(String username) {
        Optional<User> optional = userRepo.findByUsername(username);
        if (optional.isEmpty()) {
            log.debug("User only delete error:{}:{}", USERNAME_NOT_FOUND, username);
            return new UserAdminResponse(USERNAME_NOT_FOUND);
        }

        User user = optional.get();
        List<Document> docs = user.getCreatedDocuments();
        if (!docs.isEmpty()) {
            docs.forEach(document -> documentService.changeOwnerToDeleted(document.getDocId()));
        }
        userRepo.delete(user);

        log.info("User deleted:(username:{}, email:{})", user.getUsername(), user.getEmail());
        return new UserAdminResponse(OK);
    }

    public UserAdminResponse addUserRole(String username, Role role) {
        Optional<User> optional = userRepo.findByUsername(username);
        if (optional.isEmpty()) {
            log.debug("User add role error:{}:{}", USERNAME_NOT_FOUND, username);
            return new UserAdminResponse(USERNAME_NOT_FOUND);
        }

        User user = optional.get();
        Set<Role> roles = user.getAuthorities();
        if (roles.contains(role)) {
            log.debug("User add role error:{}:(username:{}, role:{})", USER_ALREADY_HAVE_THIS_ROLE, username, role);
            return new UserAdminResponse(USER_ALREADY_HAVE_THIS_ROLE);
        }

        roles.add(role);
        user.setAuthorities(roles);
        userRepo.save(user);

        log.info("User role added:(username:{}, role:{})", user.getUsername(), role);
        return new UserAdminResponse(OK);
    }

    public UserAdminResponse removeUserRole(String username, Role role) {

        if (role == Role.ROLE_USER) {
            log.debug("User remove role error:{}:{}", BASIC_USER_ROLE_CANNOT_BE_REMOVED, username);
            return new UserAdminResponse(BASIC_USER_ROLE_CANNOT_BE_REMOVED);
        }

        Optional<User> optional = userRepo.findByUsername(username);
        if (optional.isEmpty()) {
            log.debug("User remove role error:{}:{}", USERNAME_NOT_FOUND, username);
            return new UserAdminResponse(USERNAME_NOT_FOUND);
        }

        User user = optional.get();
        Set<Role> roles = user.getAuthorities();
        if (!roles.contains(role)) {
            log.debug("User remove role error:{}:(username:{}, role:{})", USER_DO_NOT_HAVE_THIS_ROLE, username, role);
            return new UserAdminResponse(USER_DO_NOT_HAVE_THIS_ROLE);
        }

        roles.remove(role);
        user.setAuthorities(roles);
        userRepo.save(user);

        log.info("User role removed:(username:{}, role:{})", user.getUsername(), role);
        return new UserAdminResponse(OK);
    }
}
