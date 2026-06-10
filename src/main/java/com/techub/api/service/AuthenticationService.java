package com.techub.api.service;

import com.techub.api.domain.User;
import com.techub.api.dto.UserLoginDataDTO;
import com.techub.api.exception.InvalidCredentialsException;
import com.techub.api.exception.UserDesactivatedException;
import com.techub.api.repository.UserRepository;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Service;

@Service
public class AuthenticationService {

    private final UserRepository userRepository;
    private final AuthenticationManager authenticationManager;

    public AuthenticationService(
        UserRepository userRepository,
        AuthenticationManager authenticationManager
    ) {
        this.userRepository =  userRepository;
        this.authenticationManager = authenticationManager;
    }

    public User authentication(UserLoginDataDTO input){
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            input.email(),
                            input.senha()
                    )
            );

        }  catch (DisabledException ex) {
            throw new UserDesactivatedException();
        } catch (AuthenticationException ex) {
            throw new InvalidCredentialsException();
        }
        User user = userRepository.findByEmail(input.email())
                .orElseThrow(InvalidCredentialsException::new);

        if(user.getAtivo().equals(Boolean.FALSE)){
            throw new UserDesactivatedException();
        }

        return user;
    }
}
