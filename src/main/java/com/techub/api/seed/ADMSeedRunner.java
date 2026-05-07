package com.techub.api.seed;

import com.techub.api.domain.ADM;
import com.techub.api.domain.User;
import com.techub.api.dto.ADMCreateRequestDTO;
import com.techub.api.exception.EmailAlredyExistsExeception;
import com.techub.api.repository.ADMRepository;
import com.techub.api.repository.UserRepository;
import com.techub.api.service.UserService;
import jakarta.transaction.Transactional;
import org.springframework.boot.CommandLineRunner;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Component;

@Component
public class ADMSeedRunner implements CommandLineRunner {

    private UserService userService;
    private UserRepository userRepository;
    private ADMRepository admRepository;

    public  ADMSeedRunner(ADMRepository admRepository, UserService userService, UserRepository userRepository){
        this.admRepository = admRepository;
        this.userService = userService;
        this.userRepository = userRepository;
    }

    @Override
    public void run(String... args) {
        String username = "devzevitor";
        String email = "devzevitor@gmail.com";
        String senha = "adm123";

        boolean jaExiste = admRepository.existsByUsernameIgnoreCase(username)
                || userRepository.existsByEmailIgnoreCase(email);

        if(jaExiste) {
            return;
        }

       try {
           userService.cadastrarADM(new ADMCreateRequestDTO(email, senha, username));
       } catch (EmailAlredyExistsExeception | DataIntegrityViolationException ignored) {

       }
    }
}
