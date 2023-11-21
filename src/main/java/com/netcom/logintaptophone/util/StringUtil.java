package com.netcom.logintaptophone.util;

import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;

@Component
public class StringUtil {
    public String sanitizeString(String toSanitize){
        return toSanitize.replaceAll("\n", "").replaceAll("\r", "");
    }

    public byte[] getUTF8Bytes(final String input) {
        return input.getBytes(StandardCharsets.UTF_8);
    }
}
