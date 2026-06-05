package com.techub.api.email;

import org.springframework.stereotype.Component;

@Component
public class EmailTemplate {
    public String confirmationTemplate(
            String name,
            String confirmationLink
    ) {
        return """
        <h1>Bem-vindo ao Resumify</h1>

        <p>Olá %s</p>

        <p>Clique abaixo para confirmar seu email:</p>

        <a href="%s">
            Confirmar Email
        </a>
        """
        .formatted(name, confirmationLink);
    }
}
