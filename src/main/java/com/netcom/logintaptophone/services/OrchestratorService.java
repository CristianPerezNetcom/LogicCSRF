package com.netcom.logintaptophone.services;


import com.netcom.logintaptophone.dto.DynamicMainRequest;
import com.netcom.logintaptophone.dto.DynamicResponseServices;
import com.netcom.logintaptophone.dto.RandomData;
import com.netcom.logintaptophone.util.CryptoCache;
import com.netcom.logintaptophone.util.CryptoUtil;
import com.netcom.logintaptophone.util.RestfulUtil;
import com.netcom.logintaptophone.util.StringUtil;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.owasp.security.logging.SecurityMarkers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.crypto.spec.IvParameterSpec;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.SecureRandom;
import java.util.Base64;


@Service
@Scope("prototype")
public class OrchestratorService {


    @Autowired
    private StringUtil stringUtil;

    @Autowired
    private CryptoUtil cryptoUtil;

    @Autowired
    private CryptoCache cryptoCache;

    private final Logger log = LoggerFactory.getLogger(OrchestratorService.class);

    @Autowired
    private RestfulUtil restfulUtil;

    private RandomData randomData;

    @PostConstruct
    public void init() {
        randomData = cryptoUtil.generateRandomData();
    }

    public ResponseEntity<DynamicResponseServices> processNextStep(HttpServletRequest request, DynamicMainRequest dynamicMainRequest) {
        try {
            switch (dynamicMainRequest.getStepRequested()) {
                case "Encrypt":
                    cryptoCache.addCache(request.getHeader("Authorization"), randomData);
                    final String encrypted = cryptoUtil.encrypt(randomData, dynamicMainRequest.getAdditionalInfo());
                    return new ResponseEntity<>(restfulUtil.getOkResponse(encrypted, "2", "Revisión Tecnica iniciada correctamente."), HttpStatus.OK);
                case "Decrypt":
                    final String decrypted = cryptoUtil.decrypt(randomData, dynamicMainRequest.getAdditionalInfo());
                    return new ResponseEntity<>(restfulUtil.getOkResponse(decrypted, "3", "Revisión Tecnica procesando correctamente."), HttpStatus.OK);
                case "LastRandoms":
                    return new ResponseEntity<>(restfulUtil.getOkResponse(Base64.getEncoder().encodeToString(randomData.getKey()) + " / " + Base64.getEncoder().encodeToString(randomData.getIvParameterSpec().getIV()), "4", "Revisión Tecnica finalizada correctamente."), HttpStatus.OK);
                case "CustomEncrypt-Admin":
                    randomData = cryptoUtil.generateFullRandomData(request.getHeader("x-fragment-key"), request.getHeader("x-fragment-value"));
                    final String encryptedAdmin = cryptoUtil.encrypt(randomData, dynamicMainRequest.getAdditionalInfo());
                    return new ResponseEntity<>(restfulUtil.getOkResponse(encryptedAdmin, "4", "Revisión Tecnica finalizada correctamente."), HttpStatus.OK);
                case "GenerateNewRandoms":
                    randomData = cryptoUtil.generateRandomData();
                    cryptoUtil.getSecureRandom().nextBytes(randomData.getIv());
                    if(randomData.getIvParameterSpec() == null) {
                        randomData.setIvParameterSpec(new IvParameterSpec(randomData.getIv()));
                    }
                    return new ResponseEntity<>(restfulUtil.getOkResponse(Base64.getEncoder().encodeToString(randomData.getKey()) + " / " + Base64.getEncoder().encodeToString(randomData.getIvParameterSpec().getIV()), "4", "Revisión Tecnica finalizada correctamente."), HttpStatus.OK);
                default:
                    return new ResponseEntity<>(restfulUtil.getErrorResponse("Review step requested", "Step not found.", "showErrorPopUp", "Se ha generado un error en el proceso. Repita el proceso o comuniquese con soporte."), HttpStatus.INTERNAL_SERVER_ERROR);
            }
        } catch (Exception ex) {
            log.error(SecurityMarkers.EVENT_FAILURE, "Error Generated on processNextStep: {} ", stringUtil.sanitizeString(ExceptionUtils.getStackTrace(ex)));
            return new ResponseEntity<>(restfulUtil.getErrorResponse("Error Generated on processNextStep", ex.getMessage(), "showErrorPopUp", "Se ha generado un error en el proceso. Repita el proceso o comuniquese con soporte."), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public String toString() {
        return "OrchestratorService{" + '}';
    }
}
