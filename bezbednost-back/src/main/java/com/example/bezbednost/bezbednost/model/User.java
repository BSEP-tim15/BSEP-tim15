package com.example.bezbednost.bezbednost.model;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Entity
@Table(name = "users")
@Data
public class User implements UserDetails {

    @Id
    @Column(name = "user_id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "name")
    private String name;

    @Column(name = "username")
    private String username;

    @Column(name = "password")
    private String password;

    @Column(name = "country")
    private String country;

    @Column(name = "email")
    private String email;

    @Column(name = "is_approved")
    private boolean isApproved;

    @ManyToMany(targetEntity = Role.class, cascade = {CascadeType.MERGE}, fetch = FetchType.EAGER)
    @Column(name = "role_id")
    private List<Role> roles;

    @Column(name = "verification_code", updatable = false)
    private String verificationCode;

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
        List<GrantedAuthority> grantedAuthorities = new ArrayList<>();
        for (Role r : roles) {
            grantedAuthorities.add(new SimpleGrantedAuthority(r.getName()));
            for (Permission permission : r.getPermissions()) {
                grantedAuthorities.add(new SimpleGrantedAuthority(permission.getName()));
            }
        }
        return grantedAuthorities;
    }

    public List<String> getRoleNames(){
        List<String> roleNames = new ArrayList<>();
        for(Role role: roles){
            roleNames.add(role.getName());
        }
        return roleNames;
    }

    public List<String> getPermissionNames(){
        List<String> permissionNames = new ArrayList<>();
        for(Role role : roles){
            for(Permission permission: role.getPermissions()){
                permissionNames.add(permission.getName());
            }
        }
        return permissionNames;
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
