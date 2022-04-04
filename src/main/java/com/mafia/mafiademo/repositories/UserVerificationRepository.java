package com.mafia.mafiademo.repositories;

import com.mafia.mafiademo.domain.UserVerification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface UserVerificationRepository extends JpaRepository<UserVerification, UUID> {
}
