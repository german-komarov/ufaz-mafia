package com.mafia.mafiademo.services;

import com.mafia.mafiademo.domain.User;
import com.mafia.mafiademo.domain.UserVerification;
import com.mafia.mafiademo.util.constants.UserRole;
import com.mafia.mafiademo.util.dto.UserRegistrationDto;
import com.mafia.mafiademo.util.exceptions.NotFoundException;
import com.mafia.mafiademo.util.exceptions.RegistrationException;
import com.mafia.mafiademo.util.exceptions.ValidationException;
import com.mafia.mafiademo.util.exceptions.VerificationException;
import com.mafia.mafiademo.util.validation.RegistrationValidator;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@Transactional(rollbackFor = Exception.class)
public class RegistrationService {

    private final UserService userService;
    private final RegistrationValidator registrationValidator;
    private final BCryptPasswordEncoder passwordEncoder;
    private final VerificationService verificationService;

    public RegistrationService(
            UserService userService,
            RegistrationValidator registrationValidator,
            BCryptPasswordEncoder passwordEncoder,
            VerificationService verificationService) {
        this.userService = userService;
        this.registrationValidator = registrationValidator;
        this.passwordEncoder = passwordEncoder;
        this.verificationService = verificationService;
    }

    public void verifyUser(UUID code) throws VerificationException {
        try {
            UserVerification verification = verificationService.readByCode(code);
            User user = verification.getUser();
            user.enable();
            userService.save(user);
            verificationService.deleteByCode(code);
        } catch (NotFoundException e) {
            throw new VerificationException(String.format("Verification failed with such message : %s", e.getMessage()));
        }
    }

    public UserVerification registerNewUser(UserRegistrationDto registrationDto) throws RegistrationException {
        try {
            registrationValidator.validate(registrationDto);
        } catch (ValidationException e) {
            throw new RegistrationException(e.getMessage());
        }
        String email = registrationDto.getEmail();
        if(userService.existByEmail(email)) {
            throw new RegistrationException(String.format("User with email=%s already exists", email));
        }
        String password = passwordEncoder.encode(registrationDto.getPassword());
        User user = userService.save(new User(email, password, UserRole.USER));
        return verificationService.createVerification(user);
    }
}
