package com.mafia.mafiademo.util.dto;

import java.util.Objects;

public class UserRegistrationDto {
    private String email;
    private String password;
    private String confirmPassword;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getConfirmPassword() {
        return confirmPassword;
    }

    public void setConfirmPassword(String confirmPassword) {
        this.confirmPassword = confirmPassword;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof UserRegistrationDto)) return false;
        UserRegistrationDto that = (UserRegistrationDto) o;
        return Objects.equals(getEmail(), that.getEmail()) && Objects.equals(getPassword(), that.getPassword()) && Objects.equals(getConfirmPassword(), that.getConfirmPassword());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getEmail(), getPassword(), getConfirmPassword());
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("UserRegistrationDto{");
        sb.append("email='").append(email).append('\'');
        sb.append(", password='").append(password).append('\'');
        sb.append(", confirmPassword='").append(confirmPassword).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
