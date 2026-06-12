package com.techub.api.seed;

import com.techub.api.domain.Role;
import com.techub.api.dto.ADMCreateRequestDTO;
import com.techub.api.exception.EmailAlreadyExistsException;
import com.techub.api.repository.UserRepository;
import com.techub.api.service.UserService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Component;

@Component
public class ADMSeedRunner implements CommandLineRunner {

    final private UserService userService;
    final private UserRepository userRepository;

    public  ADMSeedRunner(UserService userService, UserRepository userRepository){
        this.userService = userService;
        this.userRepository = userRepository;
    }

    @Override
    public void run(String... args) {
        String username = "devzevitor";
        String email = "devzevitor@gmail.com";
        String senha = "adm123";

        if (userRepository.existsByEmailIgnoreCase(email)) {
            userRepository.findByEmail(email).ifPresent(user -> {
                user.setAtivo(true);
                user.setRole(Role.ADM);
                userRepository.save(user);
            });
            return;
        }

       try {
           userService.cadastrarADM(new ADMCreateRequestDTO(email, senha, username));
       } catch (EmailAlreadyExistsException | DataIntegrityViolationException ignored) {

       }
    }
}
