package com.techub.api.service;

import com.techub.api.dto.PendingStudentRegistrationDTO;
import com.techub.api.dto.UserCreateStudentRequestDTO;
import com.techub.api.exception.EmailAlredyExistsExeception;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
public class JwtService {

    @Value("${security.jwt.secret-key}")
    private String secretKey; // Keys.secretKeyFor(SignatureAlgorithm.HS256)

    // decode byte -> key (chave salva em bytes no properties)
    private Key getSignUpKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    @Value("${security.jwt.expiration-time}")
    private long jwtExpiration; // 60 * 60

    @Value("${EMAIL_CONFIRMATION_EXPIRATION}")
    private long pedingRegitrationExperation;

    public String generateToken(String email){
        return Jwts
                .builder()
                .setSubject(email)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() +  jwtExpiration))
                .signWith(getSignUpKey())
                .compact();
    }

    // Aviso de metodo deprecated
    public String extractEmail(String token){
        return Jwts
                .parserBuilder()
                .setSigningKey(getSignUpKey())
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    public Boolean isTokenValid(String token, UserDetails userDetails){
        try {
            String userName = extractEmail(token);
            return (userName.equals(userDetails.getUsername())) && !isTokenExpired(token);
        } catch (Exception e) {
            return false;
        }
    }

    public Date getExpirationTime(String token){
        return Jwts
                .parserBuilder()
                .setSigningKey(getSignUpKey())
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getExpiration();
    }


    private Boolean isTokenExpired(String token){
        Date expiration = getExpirationTime(token);
        return expiration.before(new Date());
    }

    public String generatePendingStudentRegistration(
            PendingStudentRegistrationDTO dto
    ){
        Map<String, Object> claims = new HashMap<>();

        claims.put("nome", dto.nome());
        claims.put("email", dto.email());
        claims.put("senhaHash", dto.senha());
        claims.put("semestre", dto.semestre());

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(dto.email())
                .setIssuedAt(new Date())
                .setExpiration(
                        new Date(System.currentTimeMillis() + pedingRegitrationExperation)
                )
                .signWith(getSignUpKey())
                .compact();
    }

    public PendingStudentRegistrationDTO extractPendingStudentRegistration(
            String token
    ) {

        var claims = Jwts
                .parserBuilder()
                .setSigningKey(getSignUpKey())
                .build()
                .parseClaimsJws(token)
                .getBody();

        return new PendingStudentRegistrationDTO(
                claims.get("nome", String.class),
                claims.get("email", String.class),
                claims.get("senhaHash", String.class),
                claims.get("semestre", Integer.class)
        );
    }
}
