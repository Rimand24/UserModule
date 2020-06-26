package org.example.auth.domain;

import com.sun.istack.Nullable;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "users")
public class User implements UserDetails {
    private static final long serialVersionUID = 372821945756910420L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(nullable = false)
    private String username;
    @Column(nullable = false)
    private String password;
    @Column(nullable = false)
    private String email;

    @ElementCollection(targetClass = Role.class, fetch = FetchType.EAGER)
    @Enumerated(EnumType.STRING)
    @CollectionTable(name = "users_role", joinColumns = @JoinColumn(name = "users_id"))
    private Set<Role> authorities;

    private boolean accountNonExpired = true;
    private boolean accountNonLocked = true;
    private boolean credentialsNonExpired = true;
    private boolean enabled;

}