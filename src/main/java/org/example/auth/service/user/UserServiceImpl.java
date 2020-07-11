package org.example.auth.service.user;

import org.example.auth.domain.Document;
import org.example.auth.domain.User;
import org.example.auth.repo.UserRepo;
import org.example.auth.service.document.DocumentDto;
import org.example.auth.service.util.MapperUtils;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import javax.swing.text.Utilities;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    UserRepo userRepo;

    @Autowired
    MapperUtils mapper;


    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        User user = userRepo.findByUsername(username);
        //  .orElseThrow(() -> new UsernameNotFoundException(username)); //todo repo optional
        if (user == null) {
            throw new UsernameNotFoundException("user " + username + " not found");
        }

        return user;
    }

    @Override
    public UserDto getUser(String username) {
        User user = userRepo.findByUsername(username);

        if (user == null) {
            throw new UsernameNotFoundException("user " + username + " not found");
        }

        return mapper.mapUser(user);
    }

    @Override
    public List<UserDto> findAll() {
        List<User> all = userRepo.findAll();
        return mapper.mapUserList(all);
    }

}
