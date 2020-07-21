package org.example.auth.service.user.search;

import org.example.auth.domain.User;
import org.example.auth.domain.UserDto;
import org.example.auth.repo.UserRepo;
import org.example.auth.service.util.MapperUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserSearchServiceImpl implements UserSearchService {

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private MapperUtils mapper;

    @Override
    public UserDto getUserByUsername(String username) {
        User user = userRepo.findByUsername(username)
                .orElseThrow(() -> new UserServiceException("user " + username + " not found"));
        return mapper.mapUser(user);
    }

    @Override
    public UserDto getUserByEmail(String email) {
        User user = userRepo.findByEmail(email)
                .orElseThrow(() -> new UserServiceException("user email " + email + " not found"));
        return mapper.mapUser(user);
    }

    @Override
    public List<UserDto> findAll() {
        List<User> all = userRepo.findAll();
        return mapper.mapUserList(all);
    }

    @Override
    public List<UserDto> findAllActivated() {
        List<User> users = userRepo.findAllByEnabledTrue();
        return mapper.mapUserList(users);
    }

    @Override
    public List<UserDto> findAllNotActivated() {
        List<User> users = userRepo.findAllByEnabledFalse();
        return mapper.mapUserList(users);
    }


    @Override
    public List<UserDto> findAllBlocked() {
        List<User> users = userRepo.findAllByAccountNonLockedFalse();
        return mapper.mapUserList(users);
    }

    @Override
    public List<UserDto> searchUsersByName(String name) {
        List<User> users = userRepo.findAllByUsernameContains(name);
        return mapper.mapUserList(users);
    }

}
