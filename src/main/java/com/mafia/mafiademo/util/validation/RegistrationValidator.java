package com.mafia.mafiademo.util.validation;

import com.mafia.mafiademo.util.dto.UserRegistrationDto;
import com.mafia.mafiademo.util.exceptions.ValidationException;
import org.springframework.stereotype.Component;

@Component
public class RegistrationValidator {

    public void validate(UserRegistrationDto registrationDto) throws ValidationException {
        String email = registrationDto.getEmail();
        if(email==null || email.trim().isEmpty()) {
            throw new ValidationException("Email cannot be null, empty or blank");
        }
        if(!email.contains("@")) {
            throw new ValidationException("Email must contain @ sign");
        }
        String password = registrationDto.getPassword();
        if(password==null) {
            throw new ValidationException("Password cannot be null");
        }
        if(password.length()<3) {
            throw new ValidationException("Password's length cannot be less than 3");
        }
        if(!password.equals(registrationDto.getConfirmPassword())) {
            throw new ValidationException("Password and its confirm do not match");
        }
    }

}
