package org.example.auth.service.user.admin;

import lombok.extern.slf4j.Slf4j;
import org.example.auth.domain.Document;
import org.example.auth.domain.Role;
import org.example.auth.domain.User;
import org.example.auth.repo.UserRepo;
import org.example.auth.service.document.DocumentService;
import org.example.auth.service.user.admin.dto.UserAdminResponse;
import org.example.auth.service.user.admin.dto.UserAdminServiceResponseCode;
import org.example.auth.service.user.admin.dto.UserBlockRequest;
import org.example.auth.service.util.MapperUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.*;

import static org.example.auth.service.user.admin.dto.UserAdminServiceResponseCode.*;

@Slf4j
@Service
public class UserAdminServiceImpl implements UserAdminService {

    @Autowired
    UserRepo userRepo;

    @Autowired
    DocumentService documentService;

    @Override
    public UserAdminResponse blockUser(@Valid UserBlockRequest request) {
        Optional<User> optional  = userRepo.findByUsername(request.getUsername());
        if (optional.isEmpty()){
            log.debug("User block error:{}:{}", USERNAME_NOT_FOUND, request.getUsername());
            return new UserAdminResponse(USERNAME_NOT_FOUND);
        }

        User user = optional.get();
        if(!user.isAccountNonLocked()){
            log.debug("User block error:{}:{}", USER_ALREADY_BLOCKED, request.getUsername());
            return new UserAdminResponse(USER_ALREADY_BLOCKED);
        }
        user.setAccountNonLocked(false);
        user.setAccountBlockerName(request.getBlocker());
        user.setAccountBlockReason(request.getReason());
        user.setAccountBlockDate(LocalDate.now());
        userRepo.save(user);

        log.info("User blocked:(username:{}, email:{})", user.getUsername(), user.getEmail() );
        return new UserAdminResponse(OK);
    }

    @Override
    public UserAdminResponse unblockUser(String username) {
        Optional<User> optional  = userRepo.findByUsername(username);
        if (optional.isEmpty()){
            log.debug("User unblock error:{}:{}", USERNAME_NOT_FOUND, username);
            return new UserAdminResponse(USERNAME_NOT_FOUND);
        }

        User user = optional.get();
        if(user.isAccountNonLocked()){
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

    @Override
    public UserAdminResponse deleteUser(String username) {
        Optional<User> optional  = userRepo.findByUsername(username);
        if (optional.isEmpty()){
            log.debug("User delete error:{}:{}", USERNAME_NOT_FOUND, username);
            return new UserAdminResponse(USERNAME_NOT_FOUND);
        }

        User user = optional.get();
        List<Document> docs = user.getCreatedDocuments();
        if (!docs.isEmpty()) {
            docs.forEach(document -> documentService.deleteDocument(document.getDocId()));
        }
        userRepo.delete(user);

        log.info("User deleted:(username:{}, email:{})", user.getUsername(), user.getEmail() );
        return new UserAdminResponse(OK);
    }

    @Override
    public UserAdminResponse deleteUserOnly(String username) {
        Optional<User> optional  = userRepo.findByUsername(username);
        if (optional.isEmpty()){
            log.debug("User only delete error:{}:{}", USERNAME_NOT_FOUND, username);
            return new UserAdminResponse(UserAdminServiceResponseCode.USERNAME_NOT_FOUND);
        }

        User user = optional.get();
        List<Document> docs = user.getCreatedDocuments();
        if (!docs.isEmpty()) {
            docs.forEach(document -> documentService.changeOwnerToDeleted(document.getDocId()));
        }
        userRepo.delete(user);

        log.info("User deleted:(username:{}, email:{})", user.getUsername(), user.getEmail() );
        return new UserAdminResponse(OK);
    }

    @Override
    public UserAdminResponse addUserRole(String username, Role role) {
        Optional<User> optional  = userRepo.findByUsername(username);
        if (optional.isEmpty()){
            log.debug("User add role error:{}:{}", USERNAME_NOT_FOUND, username);
            return new UserAdminResponse(UserAdminServiceResponseCode.USERNAME_NOT_FOUND);
        }

        User user = optional.get();
        Set<Role> roles = user.getAuthorities();
        if(roles.contains(role)){
            log.debug("User add role error:{}:(username:{}, role:{})", USER_ALREADY_HAVE_THIS_ROLE, username, role);
            return new UserAdminResponse(USER_ALREADY_HAVE_THIS_ROLE);
        }

        roles.add(role);
        user.setAuthorities(roles);
        userRepo.save(user);

        log.info("User role added:(username:{}, role:{})", user.getUsername(), role );
        return new UserAdminResponse(OK);
    }

    @Override
    public UserAdminResponse removeUserRole(String username, Role role) {

        if(role == Role.USER){
            log.debug("User remove role error:{}:{}", BASIC_USER_ROLE_CANNOT_BE_REMOVED, username);
            return new UserAdminResponse(BASIC_USER_ROLE_CANNOT_BE_REMOVED);
        }

        Optional<User> optional  = userRepo.findByUsername(username);
        if (optional.isEmpty()){
            log.debug("User remove role error:{}:{}", USERNAME_NOT_FOUND, username);
            return new UserAdminResponse(USERNAME_NOT_FOUND);
        }

        User user = optional.get();
        Set<Role> roles = user.getAuthorities();
        if(!roles.contains(role)){
            log.debug("User remove role error:{}:(username:{}, role:{})", USER_DO_NOT_HAVE_THIS_ROLE, username, role);
            return new UserAdminResponse(USER_DO_NOT_HAVE_THIS_ROLE);
        }

        roles.remove(role);
        user.setAuthorities(roles);
        userRepo.save(user);

        log.info("User role removed:(username:{}, role:{})", user.getUsername(), role );
        return new UserAdminResponse(OK);
    }
}
