package com.mafia.mafiademo.controllers;

import com.mafia.mafiademo.domain.UserVerification;
import com.mafia.mafiademo.services.EmailService;
import com.mafia.mafiademo.services.RegistrationService;
import com.mafia.mafiademo.util.dto.UserRegistrationDto;
import com.mafia.mafiademo.util.exceptions.RegistrationException;
import com.mafia.mafiademo.util.exceptions.VerificationException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

import static java.lang.String.format;
import static java.util.Collections.singletonMap;

@RestController
@RequestMapping("/registration")
public class RegistrationController {

    @Value("${com.mafia.registration.mail.enabled}")
    private boolean emailEnabled;
    private final RegistrationService registrationService;
    private final EmailService emailService;

    public RegistrationController(RegistrationService registrationService, EmailService emailService) {
        this.registrationService = registrationService;
        this.emailService = emailService;
    }

    @GetMapping("/verify/{code}")
    public ResponseEntity<Object> verifyUser(@PathVariable UUID code) {
        try {
            registrationService.verifyUser(code);
            return ResponseEntity.ok("User successfully verified and activated");
        } catch (VerificationException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/user")
    public ResponseEntity<Object> registerUser(@RequestBody UserRegistrationDto registrationDto) {
        try {
            UserVerification verification = registrationService.registerNewUser(registrationDto);
            if(emailEnabled) {
                return ResponseEntity.ok(format("Activation code was sent to %s", verification.getUser().getEmail()));
            } else {
                return verifyUser(verification.getCode());
            }
        } catch (RegistrationException e) {
            return ResponseEntity.badRequest().body(singletonMap("message", e.getMessage()));
        }
    }

}
