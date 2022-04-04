package com.mafia.mafiademo.services;

import com.mafia.mafiademo.domain.User;
import com.mafia.mafiademo.repositories.UserRepository;
import com.mafia.mafiademo.util.exceptions.NotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(rollbackFor = Exception.class)
public class UserService {

    private final UserRepository repository;

    public UserService(UserRepository repository) {
        this.repository = repository;
    }

    public boolean existByEmail(String email) {
        return repository.existsByEmail(email);
    }

    public User readByEmail(String email) throws NotFoundException {
        return repository
                .findByEmail(email)
                .orElseThrow(() -> new NotFoundException(String.format("There is no user with email=%s", email)));
    }

    public User save(User user) {
        return repository.save(user);
    }
}
