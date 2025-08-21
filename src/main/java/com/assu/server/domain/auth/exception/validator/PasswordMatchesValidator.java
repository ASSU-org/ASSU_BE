package com.assu.server.domain.auth.exception.validator;

import com.assu.server.domain.auth.exception.annotation.PasswordMatches;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.lang.reflect.Field;

public class PasswordMatchesValidator implements ConstraintValidator<PasswordMatches, Object> {

    private String passwordField;
    private String confirmField;

    @Override
    public void initialize(PasswordMatches constraintAnnotation) {
        this.passwordField = constraintAnnotation.password();
        this.confirmField = constraintAnnotation.confirm();
    }

    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {
        try {
            Field pw = value.getClass().getDeclaredField(passwordField);
            Field cf = value.getClass().getDeclaredField(confirmField);
            pw.setAccessible(true);
            cf.setAccessible(true);
            Object p = pw.get(value);
            Object c = cf.get(value);
            if (p == null || c == null) return false;
            return p.equals(c);
        } catch (Exception e) {
            return false;
        }
    }
}

