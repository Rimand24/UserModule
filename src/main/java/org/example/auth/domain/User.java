package org.example.auth.domain;

import lombok.Data;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDate;
import java.util.Set;

@Data
@Entity
@Table(name = "users")
public class User implements UserDetails {
    private static final long serialVersionUID = 372821945756910420L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(nullable = false)
//    @Size(min = 3, max = 32) //fixme 3-32
    private String password;
    @Column(nullable = false)
//    @Size(min = 3, max = 32)
    private String username;
    @Column(nullable = false)
    @Email
    private String email;
    @ElementCollection(targetClass = Role.class, fetch = FetchType.EAGER)
    @Enumerated(EnumType.STRING)
    @CollectionTable(name = "users_role", joinColumns = @JoinColumn(name = "users_id"))
    private Set<Role> authorities;

   // @NotNull
    private LocalDate registrationDate;

    @OneToMany(mappedBy = "createdBy", fetch = FetchType.LAZY)
    private Set<Document> createdDocuments;

    private boolean accountNonExpired = true;
    private boolean accountNonLocked = true;
    private String accountLockerName;
    private String accountLockReason;
    private boolean credentialsNonExpired = true;
    private boolean enabled;
    private String activationCode;
    private String passwordResetCode;
}