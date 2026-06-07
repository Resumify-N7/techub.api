package com.techub.api.service;

import com.techub.api.domain.ADM;
import com.techub.api.dto.PendingStudentRegistrationDTO;
import com.techub.api.email.EmailSender;
import com.techub.api.email.EmailTemplate;
import com.techub.api.exception.EmailAlredyExistsExeception;
import com.techub.api.repository.ADMRepository;
import com.techub.api.repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ADMService {

    @Value("${app.frontend-url:http://localhost:5173}")
    private String frontendUrl;

    private final ADMRepository admRepository;

    public ADMService(
            ADMRepository admRepository
    ){
        this.admRepository = admRepository;
    }

    public List<ADM> listar_adm(int limit){
        int pageSize = Math.max(1, limit);
        return admRepository.findActive(PageRequest.of(0, pageSize)).getContent();
    }
}
