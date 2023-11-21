package com.netcom.logintaptophone.util;

import com.netcom.logintaptophone.db.key.AllowedKeys;
import com.netcom.logintaptophone.db.key.AllowedKeysRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class DBUtil {

    @Autowired
    private AllowedKeysRepository allowedKeysRepository;

    // Por favor validar. De estas 4 validaciones, es posible que solo algunas sean correctas. Hay que verificar cual trae correctamente la IP de donde se consume,
    // Se asume que solo los ultimos dos metodos son necesarios.
    public AllowedKeys validateHost(HttpServletRequest request) {
        Optional<AllowedKeys> allowedKeys;
        if (request.getHeader("host") != null) {
            allowedKeys = allowedKeysRepository.findAllowedKeysByHostsContaining(request.getHeader("host"));
            if (allowedKeys.isPresent()) {
                return allowedKeys.get();
            }
        }
        if (request.getHeader("X-FORWARDED-FOR") != null) {
            allowedKeys = allowedKeysRepository.findAllowedKeysByHostsContaining(request.getHeader("X-FORWARDED-FOR"));
            if (allowedKeys.isPresent()) {
                return allowedKeys.get();
            }
        }
        if (request.getRemoteAddr() != null) {
            allowedKeys = allowedKeysRepository.findAllowedKeysByHostsContaining(request.getRemoteAddr());
            if (allowedKeys.isPresent()) {
                return allowedKeys.get();
            }
        }
        if (request.getRemoteHost() != null) {
            allowedKeys = allowedKeysRepository.findAllowedKeysByHostsContaining(request.getRemoteHost());
            if (allowedKeys.isPresent()) {
                return allowedKeys.get();
            }
        }
        return null;
    }

}
