package org.example.auth.service.user;

import org.example.auth.domain.User;
import org.example.auth.repo.UserRepo;
import org.example.auth.service.mail.Mail;
import org.example.auth.service.mail.MailService;
import org.example.auth.service.util.MapperUtils;
import org.example.auth.service.util.RandomGeneratorUtils;
import org.example.auth.service.util.TokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    UserRepo userRepo;

    @Autowired
    MapperUtils mapper;

    @Autowired
    PasswordEncoder encoder;

    @Autowired
    TokenService tokenService;

    @Autowired
    RandomGeneratorUtils generator;

    @Autowired
    MailService mailService;

    @Value("${server.address}")
    private String serverAddress;

    @Value("${server.port}")
    private String serverPort;

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
    public UserDto getUserByUsername(String username) {
        User user = userRepo.findByUsername(username);
        if (user == null) {
            System.out.println("user " + username + " not found");//fixme use logger
            throw new UsernameNotFoundException("user " + username + " not found");
        }
        return mapper.mapUser(user);
    }

    @Override
    public UserDto getUserByEmail(String email) {
        User user = userRepo.findByEmail(email);
        if (user == null) {
            throw new UsernameNotFoundException("email " + email + " not found");
        }

        return mapper.mapUser(user);
    }

    @Override
    public List<UserDto> findAll() {
        List<User> all = userRepo.findAll();
        return mapper.mapUserList(all);
    }

    @Override
    public List<UserDto> findAllNotBlocked() {
        List<User> all = userRepo.findAllByAccountNonLockedTrue();
        return mapper.mapUserList(all);
    }

    @Override
    public List<UserDto> findAllBlocked() {
        List<User> all = userRepo.findAllByAccountNonLockedFalse();
        return mapper.mapUserList(all);
    }

    @Override
    public List<UserDto> searchUsersByName(String name) {
        List<User> all = userRepo.findAllByUsernameContains(name);
        return mapper.mapUserList(all);
    }

    @Override
    public boolean changePassword(ChangePasswordRequest request) {
        User user = userRepo.findByUsername(request.getUsername());
        if (user == null) {
            throw new UsernameNotFoundException("user " + request.getUsername() + " not found");
        }
        if (!user.getPassword().equals(encoder.encode(request.getOldPassword()))) {
            throw new UserServiceException("old password incorrect");
        }

        user.setPassword(encoder.encode(request.getPassword()));
        userRepo.save(user);
        return true;
    }

    @Override
    public boolean sendResetPasswordCode(String username) {
        User user = userRepo.findByUsername(username);
        if (user == null) {
            throw new UsernameNotFoundException("user " + username + " not found");
        }
        String code = tokenService.generatePasswordResetToken();
        user.setPasswordResetCode(code);
        String email = user.getEmail();
        sendResetPasswordCode(username, email, code);
        return false;
    }

    @Override
    public boolean resetPassword(String code) {
        User user = userRepo.findByPasswordResetCode(code);
        if (user != null) {
            if (tokenService.verifyToken(code)) {
                String newPassword = generator.generatePassword();
                user.setPassword(encoder.encode(newPassword));
                user.setPasswordResetCode(null);
                sendNewPassword(user.getUsername(), user.getEmail(), newPassword);
                userRepo.save(user);
                return true;
            }
        }
        return false;
    }

    private void sendResetPasswordCode(String username, String email, String code) {
        String message = username + ", follow the link bellow to reset password \n\r " +
                "http://" + serverAddress +":"+ serverPort + "/resetPassword/" + code +
                "if you dont sent reset password request, contact with support";

        sendMail(email, "Password reset", message);
    }

    private void sendNewPassword(String username, String email, String password) {
        String message = username + ", your password had been successfully changed \n\r " +
                "Your new password: " + password;

        sendMail(email, "Password changed", message);
    }



    private void sendMail(String email, String subject, String message) {
        Mail mail = new Mail() {{
            setTo(email);
            setSubject("Welcome"); //Welcome to Qwiklabs®
            setContent(message);
        }};

        mailService.send(mail);
    }


//
//    /////////
//    /**
//     * Populates the roles. This method is a shortcut for calling
//     * {@link #authorities(String...)}, but automatically prefixes each entry with
//     * "ROLE_". This means the following:
//     *
//     * <code>
//     *     builder.roles("USER","ADMIN");
//     * </code>
//     *
//     * is equivalent to
//     *
//     * <code>
//     *     builder.authorities("ROLE_USER","ROLE_ADMIN");
//     * </code>
//     *
//     * <p>
//     * This attribute is required, but can also be populated with
//     * {@link #authorities(String...)}.
//     * </p>
//     *
//     * @param roles the roles for this user (i.e. USER, ADMIN, etc). Cannot be null,
//     * contain null values or start with "ROLE_"
//     * @return the {@link org.springframework.security.core.userdetails.User.UserBuilder} for method chaining (i.e. to populate
//     * additional attributes for this user)
//     */
//    public org.springframework.security.core.userdetails.User.UserBuilder roles(String... roles) {
//        List<GrantedAuthority> authorities = new ArrayList<>(
//                roles.length);
//        for (String role : roles) {
//            Assert.isTrue(!role.startsWith("ROLE_"), () -> role
//                    + " cannot start with ROLE_ (it is automatically added)");
//            authorities.add(new SimpleGrantedAuthority("ROLE_" + role));
//        }
//        return authorities(authorities);
//    }
//    ///////////////////////////
}
