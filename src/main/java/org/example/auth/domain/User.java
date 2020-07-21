package org.example.auth.domain;

import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import javax.validation.constraints.*;
import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Set;

@Getter
@Setter
@EqualsAndHashCode
@NoArgsConstructor
@Entity
@Table(name = "users")
public class User implements UserDetails {
    private static final long serialVersionUID = 372821945756910420L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotBlank (message = "username cannot be blank")
    private String username;

    @NotBlank (message = "password cannot be blank")
    private String password;

    @NotBlank (message = "email cannot be blank")
    @Email(message = "Email should be valid")
    private String email;

    @NotNull(message = "registrationDate cannot be null")
    @PastOrPresent(message = "registration Date cannot be in the future")
    private LocalDate registrationDate;

    @ElementCollection(targetClass = Role.class, fetch = FetchType.EAGER)
    @Enumerated(EnumType.STRING)
    @CollectionTable(name = "users_role", joinColumns = @JoinColumn(name = "users_id"))
    private Set<Role> authorities;

    @OneToMany(mappedBy = "createdBy", fetch = FetchType.LAZY)
   // @OneToMany(cascade = CascadeType.ALL, mappedBy = "createdBy", orphanRemoval = true)
    private List<Document> createdDocuments;

    private boolean accountNonExpired = true;

    private boolean accountNonLocked = true;

    private String accountLockerName;

    private String accountLockReason;

    private boolean credentialsNonExpired = true;

    private boolean enabled;

    private String emailActivationCode;

    private String passwordResetCode;



    public Collection<Role> getRoles() {
        return authorities;
    }

}