package com.mafia.mafiademo.domain;

import com.mafia.mafiademo.util.constants.UserRole;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import java.util.Collection;
import java.util.Collections;
import java.util.UUID;

@Entity
@Table(name = "users")
public class User implements UserDetails {

    @Id
    private UUID id;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    private String password;
    private boolean enabled;

    @Enumerated(EnumType.STRING)
    private UserRole role;

    protected User() {}

    public User(String email, String password, UserRole role) {
        this.email = email;
        this.password = password;
        this.role = role;
    }

    public UUID getId() { return id; }
    public String getEmail() { return email; }
    public String getUsername() { return getEmail(); }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public Collection<? extends GrantedAuthority> getAuthorities() { return Collections.singletonList(()->role.name()); }
    public boolean isEnabled() { return enabled; }
    public boolean isAccountNonExpired() {
        return true;
    }
    public boolean isAccountNonLocked() {
        return true;
    }
    public boolean isCredentialsNonExpired() {
        return true;
    }
    public void enable() { enabled = true; }
    public void disable() { enabled = false; }

    @PrePersist
    private void onPersist() {
        this.id = UUID.randomUUID();
    }
}
