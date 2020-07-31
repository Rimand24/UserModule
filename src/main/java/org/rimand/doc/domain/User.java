package org.rimand.doc.domain;

import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PastOrPresent;
import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "users")
public class User implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @NotBlank(message = "username cannot be blank")
    private String username;
    @NotBlank(message = "password cannot be blank")
    private String password;
    @NotBlank(message = "email cannot be blank")
    @Email(message = "Email should be valid")
    private String email;
    @NotNull(message = "registrationDate cannot be null")
    @PastOrPresent(message = "registration date cannot be in the future")
    private LocalDate registrationDate;
    @ElementCollection(targetClass = Role.class, fetch = FetchType.EAGER)
    @Enumerated(EnumType.STRING)
    @CollectionTable(name = "users_role", joinColumns = @JoinColumn(name = "users_id"))
    private Set<Role> authorities;
    @OneToMany(mappedBy = "uploader", fetch = FetchType.LAZY)
    private List<Document> createdDocuments;
    private boolean accountNonExpired = true;
    private boolean accountNonLocked = true;

    //todo Block Details
    private String accountBlockerName;
    private String accountBlockReason;
    @PastOrPresent(message = "block date cannot be in the future")
    private LocalDate accountBlockDate;

    private boolean credentialsNonExpired = true;
    private boolean enabled;
    private String emailActivationCode;
    private String passwordResetCode;


    public Collection<Role> getRoles() {
        return authorities;
    }

}