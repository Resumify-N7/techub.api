package com.techub.api.email;
import com.techub.api.exception.EmailSendException;

public interface EmailSender {
    void send(
            String to,
            String subject,
            String html
    ) throws EmailSendException;
}