package com.vivek.java_testing.validator;

import com.vivek.java_testing.annotations.ValidDate;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.time.LocalDate;


public class ValidDateValidator implements ConstraintValidator<ValidDate, LocalDate> {

    private int year;
    @Override
    public void initialize(ValidDate constraintAnnotation) {
       this.year =  constraintAnnotation.year();
    }

    @Override
    public boolean isValid(LocalDate date, ConstraintValidatorContext constraintValidatorContext) {
        constraintValidatorContext.disableDefaultConstraintViolation();
        final int currentYear = date.getYear();
        if (currentYear > LocalDate.now().getYear() || year > currentYear) {
            customMessageForValidation(constraintValidatorContext, "not a valid year must greater than 1900 and less than current year");
            return false;
        }
        return true;
    }

    private void customMessageForValidation(ConstraintValidatorContext constraintContext, String message) {
        constraintContext.buildConstraintViolationWithTemplate(message).addConstraintViolation();
    }

}