package org.example.auth.service.user.search;

import lombok.extern.slf4j.Slf4j;
import org.example.auth.domain.Role;
import org.example.auth.domain.User;
import org.example.auth.domain.UserDto;
import org.example.auth.repo.UserRepo;
import org.example.auth.service.util.MapperUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class UserSearchService {

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private MapperUtils mapper;

    public Optional<UserDto> findByUsername(@NotNull String username) {
        Optional<User> optional = userRepo.findByUsername(username);
        if (optional.isEmpty()) {
            log.debug("User not found by name error:{}", username);
            return Optional.empty();
        }

        log.debug("User found by name:{}", username);
        return Optional.of(mapper.mapUser(optional.get()));
    }

    public Optional<UserDto> findByEmail(@Email String email) {
        Optional<User> optional = userRepo.findByEmail(email);
        if (optional.isEmpty()) {
            log.debug("User not found by email error:{}", email);
            return Optional.empty();
        }
        log.debug("User found by email:{}", email);
        return Optional.of(mapper.mapUser(optional.get()));
    }

    public List<UserDto> findAll() {
        List<User> users = userRepo.findAll();
        log.debug("found all users: {} records", users.size());
        return mapper.mapUserList(users);
    }

    public List<UserDto> findAllActivated() {
        List<User> users = userRepo.findAllByEnabledTrue();
        log.debug("found activated users: {} records", users.size());
        return mapper.mapUserList(users);
    }

    public List<UserDto> findAllNotActivated() {
        List<User> users = userRepo.findAllByEnabledFalse();
        log.debug("found not activated users: {} records", users.size());
        return mapper.mapUserList(users);
    }

    public List<UserDto> findAllBlocked() {
        List<User> users = userRepo.findAllByAccountNonLockedFalse();
        log.debug("found blocked users: {} records", users.size());
        return mapper.mapUserList(users);
    }

    public List<UserDto> searchUsersByName(String name) {
        List<User> users = userRepo.findAllByUsernameContains(name);
        log.debug("searched for name:{} - {} records",name, users.size());
        return mapper.mapUserList(users);
    }

    public List<UserDto> findAllByRole(Role role) {
        List<User> users = userRepo.findByAuthoritiesContains(role);
        log.debug("found by role:{} - {} records",role, users.size());
        return mapper.mapUserList(users);
    }
}
