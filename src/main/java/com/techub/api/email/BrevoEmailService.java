package com.techub.api.email;

import com.techub.api.exception.DominioEmailInvalidoException;
import com.techub.api.exception.EmailSendException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

@Service
public class BrevoEmailService implements EmailSender {

    @Value("${brevo.api-key}")
    private String apiKey;

    @Value("${brevo.sender-email}")
    private String senderEmail;

    @Value("${brevo.sender-name:Resumify}")
    private String senderName;

    private final RestTemplate restTemplate = new RestTemplate();

    @Override
    public void send(String to, String subject, String html) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("api-key", apiKey);

            Map<String, Object> body = Map.of(
                    "sender", Map.of("email", senderEmail, "name", senderName),
                    "to", List.of(Map.of("email", to)),
                    "subject", subject,
                    "htmlContent", html
            );

            HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);

            ResponseEntity<String> response = restTemplate.postForEntity(
                    "https://api.brevo.com/v3/smtp/email",
                    request,
                    String.class
            );

            if (!response.getStatusCode().is2xxSuccessful()) {
                throw new EmailSendException("Brevo retornou status: " + response.getStatusCode(), null);
            }

        } catch (EmailSendException e) {
            throw e;
        } catch (Exception e) {
            throw new EmailSendException("Erro ao enviar email via Brevo: " + e.getMessage(), e);
        }
    }

    @Override
    public void validarDominioInstitucional(String email) throws EmailSendException {
        String emailLower = email.toLowerCase();
        boolean valido = DOMINIOS_PERMITIDOS.stream().anyMatch(emailLower::endsWith);
        if (!valido) {
            throw new DominioEmailInvalidoException();
        }
    }

    private static final List<String> DOMINIOS_PERMITIDOS = List.of(
            "@aluno.cps.sp.gov.br",
            "@fatec.sp.gov.br",
            "@cps.sp.gov.br"
    );
}