package org.example.auth.domain;

import lombok.Data;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;

@Data
@Entity
@Table(name = "users")
public class User implements UserDetails {
    private static final long serialVersionUID = 372821945756910420L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @NotBlank
    private String password;
    @NotBlank
    private String username;
    @NotBlank
    @Email
    private String email;
    @NotNull
    private LocalDate registrationDate;

    @ElementCollection(targetClass = Role.class, fetch = FetchType.EAGER)
    @Enumerated(EnumType.STRING)
    @CollectionTable(name = "users_role", joinColumns = @JoinColumn(name = "users_id"))
    private Set<Role> authorities;

    @OneToMany(mappedBy = "createdBy", fetch = FetchType.LAZY)
    private List<Document> createdDocuments;

    private boolean accountNonExpired = true;
    private boolean accountNonLocked = true;
    private String accountLockerName;
    private String accountLockReason;
    private boolean credentialsNonExpired = true;
    private boolean enabled;
    private String activationCode;
    private String passwordResetCode;
}