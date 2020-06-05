package org.example.auth.domain;

import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import java.util.Set;

@Getter
@Setter
//@Entity
//@Table(name = "users")
public class User implements UserDetails {
  //  @Id
    //@GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String username;
    private String password;
    private String email;

    //    @ManyToMany(cascade = CascadeType.ALL)
//    @JoinTable(name = "users_roles",
//            joinColumns = {@JoinColumn(name = "users_id", referencedColumnName = "id")},
//            inverseJoinColumns ={@JoinColumn(name = "role_name", referencedColumnName = "name")} )
    private Set<Role> authorities;

    private boolean accountNonExpired = true;
    private boolean accountNonLocked = true;
    private boolean credentialsNonExpired = true;
    private boolean enabled;

}