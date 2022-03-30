package com.example.bezbednost.bezbednost.model;

import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import java.util.Collection;
import java.util.List;

@Entity
@Table(name = "users")
public class User implements UserDetails {

    @Id
    @Column(name = "user_id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Getter @Setter private Integer id;

    @Column(name = "name")
    @Getter @Setter private String name;

    @Column(name = "username")
    @Getter @Setter private String username;

    @Column(name = "password")
    @Getter @Setter private String password;

    @Column(name = "country")
    @Getter @Setter private String country;

    @Column(name = "email")
    @Getter @Setter private String email;

    @Column(name = "is_approved")
    @Getter @Setter private boolean isApproved;

    @ManyToMany(targetEntity = Role.class, cascade = {CascadeType.MERGE}, fetch = FetchType.EAGER)
    @Column(name = "role_id")
    @Getter @Setter private List<Role> roles;

    public User() { }

    public User(Integer id, String name, String username, String password, String country, String email, boolean isApproved, List<Role> roles) {
        this.id = id;
        this.name = name;
        this.username = username;
        this.password = password;
        this.country = country;
        this.email = email;
        this.isApproved = isApproved;
        this.roles = roles;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return roles;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return isApproved;
    }
}
