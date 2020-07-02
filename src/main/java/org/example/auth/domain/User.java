package org.example.auth.domain;

import lombok.Data;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
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
    private String username;
    @Column(nullable = false)
    private String password;
    @Column(nullable = false)
    private String email;

    @ElementCollection(targetClass = Role.class, fetch = FetchType.EAGER)
    @Enumerated(EnumType.STRING)
    @CollectionTable(name = "users_role", joinColumns = @JoinColumn(name = "users_id"))
    private Set<Role> authorities;

    @OneToMany(mappedBy = "createdBy", fetch = FetchType.LAZY)
    private Set<Document> createdDocuments;

    private boolean accountNonExpired = true;
    private boolean accountNonLocked = true;
    private boolean credentialsNonExpired = true;
    private boolean enabled;
}