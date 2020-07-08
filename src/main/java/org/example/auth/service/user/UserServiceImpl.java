package org.example.auth.service.user;

import org.example.auth.domain.User;
import org.example.auth.repo.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    UserRepo userRepo;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        User user = userRepo.findByUsername(username);
              //  .orElseThrow(() -> new UsernameNotFoundException(username)); //todo repo optional
        if (user == null) {
            throw new UsernameNotFoundException("user " + username + " not found");
        }

        return user;
    }

}
