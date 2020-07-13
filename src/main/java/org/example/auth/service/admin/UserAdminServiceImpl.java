package org.example.auth.service.admin;

import org.example.auth.domain.Document;
import org.example.auth.domain.User;
import org.example.auth.repo.DocumentRepo;
import org.example.auth.repo.UserRepo;
import org.example.auth.service.admin.UserAdminService;
import org.example.auth.service.document.DocumentService;
import org.example.auth.service.user.UserDto;
import org.example.auth.service.util.MapperUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserAdminServiceImpl implements UserAdminService {

    @Autowired
    UserRepo userRepo;

    @Autowired
    DocumentService documentService;

    @Autowired
    MapperUtils mapper;

    @Override
    public List<UserDto> findAllBlocked() {
        List<User> all = userRepo.findAllByAccountNonLockedFalse();
        return mapper.mapUserList(all);
    }

    @Override
    public boolean blockUser(String username, String blocker, String reason) {
        User user = userRepo.findByUsername(username);

        if (user == null) {
            throw new UsernameNotFoundException("user " + username + " not found");
        }

        user.setAccountNonLocked(false);
        user.setAccountLockerName(blocker);
        user.setAccountLockReason(reason);
        userRepo.save(user);
        return true;
    }

    @Override
    public boolean unblockUser(String username) {
        User user = userRepo.findByUsername(username);

        if (user == null) {
            throw new UsernameNotFoundException("user " + username + " not found");
        }

        user.setAccountNonLocked(true);
        user.setAccountLockerName(null);
        user.setAccountLockReason(null);
        userRepo.save(user);
        return true;
    }

    @Override
    public boolean deleteUser(String username) {
        User user = userRepo.findByUsername(username);
        if (user == null) throw new UsernameNotFoundException("user " + username + " not found");


        List<Document> docs = user.getCreatedDocuments();
        if (!docs.isEmpty()) {

            docs.forEach(document -> documentService.deleteDocument(document.getDocId()));
           // for (Document d : docs) documentService.deleteDocument(d.getDocId());
        }

        userRepo.delete(user);
        return true;
    }
}
