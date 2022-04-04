package com.mafia.mafiademo.domain;

import javax.persistence.*;
import java.util.UUID;

@Entity
@Table(name = "user_verifications")
public class UserVerification {
    @Id
    private UUID code;

    @OneToOne
    private User user;

    protected UserVerification() {}

    public UserVerification(User user) {
        this.user = user;
    }

    public UUID getCode() {
        return code;
    }

    public User getUser() {
        return user;
    }

    @PrePersist
    private void onPersist() { code = UUID.randomUUID(); }
}
