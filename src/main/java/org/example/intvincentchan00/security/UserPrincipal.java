package org.example.intvincentchan00.security;

import lombok.Getter;
import org.example.intvincentchan00.entity.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;

public class UserPrincipal implements UserDetails {
    // Getters for user-specific fields
    @Getter
    private final Long id;
    @Getter
    private final String name;
    private final String email;
    private final String password;
    private final Collection<? extends GrantedAuthority> authorities;

    // Constructor that takes your User entity and converts it
    public UserPrincipal(User user) {
        this.id = user.getId();
        this.name = user.getName();
        this.email = user.getEmail();
        this.password = user.getPassword();

        // For a simple system with no complex roles
        this.authorities = Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"));
    }

    // Static factory method
    public static UserPrincipal create(User user) {
        return new UserPrincipal(user);
    }

    // UserDetails interface methods
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        // Username is the email in your application
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true; // Or based on user status
    }

    @Override
    public boolean isAccountNonLocked() {
        return true; // Or based on user status
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true; // Or based on user status
    }

    @Override
    public boolean isEnabled() {
        return true; // Or based on user status
    }
}