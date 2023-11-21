package com.netcom.logintaptophone.services;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.netcom.logintaptophone.db.key.AllowedKeys;
import com.netcom.logintaptophone.db.token.Token;
import com.netcom.logintaptophone.db.token.TokenRepository;
import com.netcom.logintaptophone.db.token.TokenType;
import com.netcom.logintaptophone.db.user.Role;
import com.netcom.logintaptophone.db.user.User;
import com.netcom.logintaptophone.db.user.UserRepository;
import com.netcom.logintaptophone.dto.*;
import com.netcom.logintaptophone.util.CryptoUtil;
import com.netcom.logintaptophone.util.DBUtil;
import com.netcom.logintaptophone.util.RestfulUtil;
import com.netcom.logintaptophone.util.StringUtil;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.owasp.security.logging.SecurityMarkers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Optional;

@Service
@Scope("prototype")
@RequiredArgsConstructor
public class AuthenticationService {

    @Autowired
    private RestfulUtil restfulUtil;

    @Autowired
    private StringUtil stringUtil;

    @Autowired
    private CryptoUtil cryptoUtil;

    @Autowired
    private DBUtil dbUtil;

    @Value("${application.messages.proceso-exitos}")
    private String messageExitoso;

    @Value("${application.messages.token-null}")
    private String messageTokenNull;

    @Value("${application.messages.usuario-incorrecto}")
    private String userPasswordIncorrect;

    @Value("${application.messages.user.duplicatedUser}")
    private String duplicatedUserMessage;

    @Value("${application.messages.front.duplicatedUser}")
    private String duplicatedUserMessageFront;

    @Value("${application.messages.user.genericError}")
    private String genericErrorMessage;

    private final UserRepository userRepository;
    private final TokenRepository tokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final Logger log = LoggerFactory.getLogger(AuthenticationService.class);
    private RandomData randomData;

    @PostConstruct
    public void init() {

    }

    public ResponseEntity<DynamicAuthenticationResponse> userRegister(HttpServletRequest request, RegisterRequest registerRequest) {
        log.info("Iniciando registro de nuevo Usuario");
        AllowedKeys allowedKeys;
        try {
            allowedKeys = dbUtil.validateHost(request);
            if (allowedKeys == null) {
                return new ResponseEntity<>(restfulUtil.getAuthenticationErrorResponse("Host no valido.", "NoHostValidationException", "Solicitud no permitida. Por favor comuniquese con soporte tecnico."), HttpStatus.INTERNAL_SERVER_ERROR);
            } else if (!allowedKeys.getClient().isEnable()) {
                return new ResponseEntity<>(restfulUtil.getAuthenticationErrorResponse("Cliente no Disponible - Cliente ya no se encuentra habilitado para realizar transacciones.", "NoHostValidationException", "Señor cliente, ud ya no se encuentra habilitado para realizar transacciones. Si desea volver a realizar transacciones por favor comuniquese con soporte."), HttpStatus.INTERNAL_SERVER_ERROR);
            }
            randomData = cryptoUtil.generateFullRandomData(allowedKeys.getAesKey(), allowedKeys.getIvKey());
            final String finalEmail = cryptoUtil.decrypt(randomData, registerRequest.getEmail());
            log.info("Email: {}", stringUtil.sanitizeString(finalEmail));
            if (userRepository.findByEmail(finalEmail).isPresent()) {
                log.info("Usuario Duplicado y encontrado en la DB - Retornando Error");
                return new ResponseEntity<>(restfulUtil.getAuthenticationErrorResponse(duplicatedUserMessageFront, "SQLIntegrityConstraintViolationException", duplicatedUserMessage), HttpStatus.INTERNAL_SERVER_ERROR);
            } else {
                log.info("Usuario Nuevo - Inciando Registro");
                User user = User.builder()
                        .firstname(registerRequest.getFirstname())
                        .lastname(registerRequest.getLastname())
                        .email(finalEmail)
                        .password(passwordEncoder.encode(cryptoUtil.decrypt(randomData, registerRequest.getPassword())))
                        .role(Role.USER)
                        .build();
                User savedUser = userRepository.save(user);
                log.info("Usuario almacenado en DB correctamente - Inciando Creacion de Tokens JWT");
                String jwtToken = jwtService.generateToken(user);
                String refreshToken = jwtService.generateRefreshToken(user);
                log.info("Tokens generados correctamente - Inciando Almacenamiento de Tokens");
                saveUserToken(savedUser, jwtToken);
                log.info("Almacenamiento de Tokens realizado correctamente - Respondiendo a FRONT");
                return new ResponseEntity<>(restfulUtil.getAuthenticationOkResponse(jwtToken, refreshToken, messageExitoso, messageExitoso), HttpStatus.OK);
            }
        } catch (Exception ex) {
            log.error(SecurityMarkers.EVENT_FAILURE, "Error Generated on register: {} ", stringUtil.sanitizeString(ExceptionUtils.getStackTrace(ex)));
            return new ResponseEntity<>(restfulUtil.getAuthenticationErrorResponse(messageExitoso, ex.getMessage(), String.format(genericErrorMessage, "Registro de Nuevo Usuario")), HttpStatus.INTERNAL_SERVER_ERROR);
        } finally {
            allowedKeys = null;
            randomData = null;
            log.debug("Nulificadas las llaves de DB {}", allowedKeys);
        }
    }

    public ResponseEntity<DynamicAuthenticationResponse> authenticate(HttpServletRequest request, AuthenticationRequest authenticationRequest) {
        AllowedKeys allowedKeys;
        try {
            allowedKeys = dbUtil.validateHost(request);
            if (allowedKeys == null) {
                return new ResponseEntity<>(restfulUtil.getAuthenticationErrorResponse("Host no valido.", "NoHostValidationException", "Solicitud no permitida. Por favor comuniquese con soporte tecnico."), HttpStatus.INTERNAL_SERVER_ERROR);
            } else if (!allowedKeys.getClient().isEnable()) {
                return new ResponseEntity<>(restfulUtil.getAuthenticationErrorResponse("Cliente no Disponible - Cliente ya no se encuentra habilitado para realizar transacciones.", "NoHostValidationException", "Señor cliente, ud ya no se encuentra habilitado para realizar transacciones. Si desea volver a realizar transacciones por favor comuniquese con soporte."), HttpStatus.INTERNAL_SERVER_ERROR);
            }
            randomData = cryptoUtil.generateFullRandomData(allowedKeys.getAesKey(), allowedKeys.getIvKey());
            final String finalEmail = cryptoUtil.decrypt(randomData, authenticationRequest.getEmail());
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            finalEmail,
                            cryptoUtil.decrypt(randomData, authenticationRequest.getPassword())
                    )
            );
            User user = userRepository.findByEmail(finalEmail).orElseThrow();
            String jwtToken = jwtService.generateToken(user);
            String refreshToken = jwtService.generateRefreshToken(user);
            revokeAllUserTokens(user);
            saveUserToken(user, jwtToken);
            return new ResponseEntity<>(restfulUtil.getAuthenticationOkResponse(jwtToken, refreshToken, messageExitoso, messageExitoso), HttpStatus.OK);
        } catch (Exception ex) {
            log.error(SecurityMarkers.EVENT_FAILURE, "Error Generated on Authenticate: {} ", stringUtil.sanitizeString(ExceptionUtils.getStackTrace(ex)));
            return new ResponseEntity<>(restfulUtil.getAuthenticationErrorResponse(genericErrorMessage, ex.getMessage(), String.format(genericErrorMessage, "Autenticación Usuario")), HttpStatus.INTERNAL_SERVER_ERROR);
        } finally {
            allowedKeys = null;
            randomData = null;
            log.debug("Nulificadas las llaves de DB {}", allowedKeys);
        }
    }


    public void refreshToken(HttpServletRequest request, HttpServletResponse response) throws IOException {
        final String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        final String refreshToken;
        final String userEmail;
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            String encodedTokenNull = Base64.getEncoder().encodeToString(messageTokenNull.getBytes(StandardCharsets.UTF_8));
            log.info("TOKEN NULL: {}", stringUtil.sanitizeString(encodedTokenNull));
            var authResponse = AuthenticationResponse.builder()
                    .message(encodedTokenNull)
                    .build();
            new ObjectMapper().writeValue(response.getOutputStream(), authResponse);
            return;
        }

        refreshToken = authHeader.substring(7);
        userEmail = jwtService.extractUsername(refreshToken);

        if (userEmail != null) {
            var user = this.userRepository.findByEmail(userEmail).orElseThrow(() -> new IllegalStateException("TOKEN EXPIRED!!"));
            if (jwtService.isTokenValid(refreshToken, user)) {
                // var accessToken = jwtService.generateToken(user);
                var accessToken = jwtService.generateRefreshToken(user);
                revokeAllUserTokens(user);
                saveUserToken(user, accessToken);
                log.info("Refresh Token Correct");
                String encodedMessage = Base64.getEncoder().encodeToString(messageExitoso.getBytes(StandardCharsets.UTF_8));
                var authResponse = AuthenticationResponse.builder()
                        .accessToken(accessToken)
                        .refreshToken(refreshToken)
                        .message(encodedMessage)
                        .build();
                new ObjectMapper().writeValue(response.getOutputStream(), authResponse);
            }
        }
    }


    private void saveUserToken(User user, String jwtToken) {
        try {
            var token = Token.builder()
                    .user(user)
                    .token(jwtToken)
                    .tokenType(TokenType.BEARER)
                    .expired(false)
                    .revoked(false)
                    .build();
            tokenRepository.save(token);
        } catch (Exception ex) {
            log.error(SecurityMarkers.EVENT_FAILURE, "Error Generated on saveUserToken: {} ", stringUtil.sanitizeString(ExceptionUtils.getStackTrace(ex)));
            throw ex;
        }
    }

    private void revokeAllUserTokens(User user) {
        var validUserTokens = tokenRepository.findAllValidTokenByUser(user.getId());
        if (validUserTokens.isEmpty())
            return;
        validUserTokens.forEach(token -> {
            token.setExpired(true);
            token.setRevoked(true);
        });
        tokenRepository.saveAll(validUserTokens);
    }


}
