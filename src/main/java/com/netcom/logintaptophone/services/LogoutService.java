package com.netcom.logintaptophone.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.netcom.logintaptophone.db.token.Token;
import com.netcom.logintaptophone.db.token.TokenRepository;
import com.netcom.logintaptophone.dto.LogoutResponse;
import com.netcom.logintaptophone.util.StringUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

@Service
@Scope("prototype")
@RequiredArgsConstructor
public class LogoutService implements LogoutHandler {

    private final TokenRepository tokenRepository;
    private final Logger log = LoggerFactory.getLogger(LogoutService.class);

    @Autowired
    private StringUtil stringUtil;

    @Value("${application.messages.proceso-exitos}")
    private String messageExitoso;

    @Value("${application.messages.token-invalido}")
    private String tokenInvalid;

    @Override
    public void logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {

        final String authHeader = request.getHeader("Authorization");
        final String jwt;

        log.info("Start Logout");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            log.info("TOKEN NULL");
            return;
        }

        jwt = authHeader.substring(7);

        //////////// Log In Debug ////////////
        log.debug("Logout Token: {}" , stringUtil.sanitizeString(authHeader));

        Token storedToken = tokenRepository.findByToken(jwt).orElseThrow(() -> new IllegalStateException(tokenInvalid));
        LogoutResponse authResponse;


        if (storedToken != null) {
            if (authHeader.equals(String.valueOf(storedToken.expired))) {

                log.debug("String.valueOf(storedToken.isExpired()): {}", storedToken.isExpired());
                storedToken.setExpired(true);
                storedToken.setRevoked(true);
                tokenRepository.save(storedToken);
                SecurityContextHolder.clearContext();
                log.debug("storedToken: {}" ,storedToken.isRevoked());
                log.info("Token Update");
                String encodedMessage = Base64.getEncoder().encodeToString(messageExitoso.getBytes(StandardCharsets.UTF_8));
                authResponse = LogoutResponse.builder().message(encodedMessage).build();

                try {
                    new ObjectMapper().writeValue(response.getOutputStream(), authResponse);
                    log.debug("Logout authResponse: {}" , stringUtil.sanitizeString(authResponse.getMessage()));
                } catch (IOException e) {
                    log.error("Exception Service Logout - {}" , stringUtil.sanitizeString(e.getMessage()));
                    throw new RuntimeException(e);
                }

            } else {

                String messageExpired = "TOKEN EXPIRADO";
                log.info("String.valueOf(storedToken.isExpired()): {}" ,storedToken.isExpired());
                log.info("Entro al Else del Token Expirado: {}" , messageExpired);
                //String encodedExpired = Base64.getEncoder().encodeToString(messageExpired.getBytes(StandardCharsets.UTF_8));
                authResponse = LogoutResponse.builder().message(messageExpired).build();

                try {
                    new ObjectMapper().writeValue(response.getOutputStream(), authResponse);
                    log.info("Logout Expired authResponse: {}" , stringUtil.sanitizeString(authResponse.getMessage()));
                } catch (IOException e) {
                    log.error("Exception Service Logout - {}", stringUtil.sanitizeString(e.getMessage()));
                }
            }
        }

    }
}