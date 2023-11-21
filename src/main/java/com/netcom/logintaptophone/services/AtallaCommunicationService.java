package com.netcom.logintaptophone.services;


import com.netcom.logintaptophone.dto.AtallaHostDTO;
import com.netcom.logintaptophone.dto.DynamicHSMResponse;
import com.netcom.logintaptophone.util.RestfulUtil;
import com.netcom.logintaptophone.util.SocketUtil;
import com.netcom.logintaptophone.util.StringUtil;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.owasp.security.logging.SecurityMarkers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Service
@Scope("prototype")
public class AtallaCommunicationService {

    private final Logger log = LoggerFactory.getLogger(AtallaCommunicationService.class);

    @Autowired
    private SocketUtil socketUtil;

    @Autowired
    private RestfulUtil restfulUtil;

    @Autowired
    private StringUtil stringUtil;

    @Value("${application.atalla.command.randomNumbers}")
    private String tramaString;

    @Value("${application.messages.user.genericError}")
    private String genericErrorMessage;

    public ResponseEntity<DynamicHSMResponse> sendAtallaCommand() {
        try {
            byte[] trama = tramaString.getBytes(StandardCharsets.UTF_8);
            AtallaHostDTO atallaHost = new AtallaHostDTO("127.0.0.1", 443, 10000);
            socketUtil.createAndConnectSocket(atallaHost);
            byte[] response = socketUtil.sendTransaction(trama, atallaHost.getIp(), atallaHost.getTimeOut());
            if (response == null) {
                throw new IOException("La respuesta de la caja Atalla llego vacia o nula.");
            }
            return new ResponseEntity<>(restfulUtil.getHSMOkResponse(new String(response, socketUtil.getEncoding()), null, null), HttpStatus.OK);
        } catch (Exception ex) {
            log.error(SecurityMarkers.EVENT_FAILURE, "Error Generated on Authenticate: {} ", stringUtil.sanitizeString(ExceptionUtils.getStackTrace(ex)));
            return new ResponseEntity<>(restfulUtil.getHSMErrorResponse(genericErrorMessage, ex.getMessage(), String.format(genericErrorMessage, "Autenticación Usuario")), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
