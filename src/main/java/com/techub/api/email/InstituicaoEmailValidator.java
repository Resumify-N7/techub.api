package com.techub.api.email;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.List;

public class InstituicaoEmailValidator implements ConstraintValidator<ValidInstituicaoEmail, String> {

    private static final List<String> DOMINIOS_PERMITIDOS = List.of(
            "@aluno.cps.sp.gov.br",
            "@fatec.sp.gov.br",
            "@cps.sp.gov.br"
    );

    @Override
    public boolean isValid(String email, ConstraintValidatorContext context) {
        if (email == null || email.isBlank()) return false;
        String emailLower = email.toLowerCase();
        return DOMINIOS_PERMITIDOS.stream().anyMatch(emailLower::endsWith);
    }
}