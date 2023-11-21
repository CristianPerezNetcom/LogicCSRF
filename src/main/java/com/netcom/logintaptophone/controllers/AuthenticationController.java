package com.netcom.logintaptophone.controllers;

import com.netcom.logintaptophone.dto.*;
import com.netcom.logintaptophone.services.AtallaCommunicationService;
import com.netcom.logintaptophone.services.AuthenticationService;
import com.netcom.logintaptophone.services.LogoutService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthenticationController {

    private final AuthenticationService authenticationService;
    private final LogoutService serviceLogout;
    private final AtallaCommunicationService atallaCommunicationService;

    @PostMapping("/register")
    public ResponseEntity<DynamicAuthenticationResponse> register(HttpServletRequest request, @RequestBody RegisterRequest registerRequest) {
        return authenticationService.userRegister(request, registerRequest);
    }

    @PostMapping("/authenticate")
    public ResponseEntity<DynamicAuthenticationResponse> authenticate(HttpServletRequest request, @RequestBody AuthenticationRequest authenticationRequest) throws IOException {
        return authenticationService.authenticate(request, authenticationRequest);
    }

    @PostMapping("/refresh-token")
    public void refreshToken(HttpServletRequest request, HttpServletResponse response) throws IOException {
        authenticationService.refreshToken(request, response);
    }

    @PostMapping("/logout")
    public void logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        serviceLogout.logout(request, response, authentication);
    }

    @PostMapping("/randomNumber")
    public ResponseEntity<DynamicHSMResponse> getRandomNumberHSM(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        return atallaCommunicationService.sendAtallaCommand();
    }
}
