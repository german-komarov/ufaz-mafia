package com.mafia.mafiademo.services;

import com.mafia.mafiademo.domain.User;
import com.mafia.mafiademo.domain.UserVerification;
import com.mafia.mafiademo.repositories.UserVerificationRepository;
import com.mafia.mafiademo.util.exceptions.NotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@Transactional(rollbackFor = Exception.class)
public class VerificationService {

    private final UserVerificationRepository repository;

    public VerificationService(UserVerificationRepository repository) {
        this.repository = repository;
    }

    public UserVerification readByCode(UUID code) throws NotFoundException {
        return repository.findById(code).orElseThrow(()->new NotFoundException("There is no verification with such code"));
    }

    public UserVerification createVerification(User user) {
        return repository.save(new UserVerification(user));
    }

    public void deleteByCode(UUID code) {
        repository.deleteById(code);
    }
}
