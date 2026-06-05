package com.techub.api.email;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = InstituicaoEmailValidator.class)
@Target({ ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidInstituicaoEmail {
    String message() default "E-mail deve ser de um domínio institucional (@aluno.cps.sp.gov.br, @fatec.sp.gov.br ou @cps.sp.gov.br)";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}