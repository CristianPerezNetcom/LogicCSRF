package com.netcom.logintaptophone.util;

import com.netcom.logintaptophone.dto.RandomData;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.util.Arrays;
import java.util.Base64;
import java.util.Properties;

import jakarta.annotation.PostConstruct;
import org.apache.commons.crypto.random.CryptoRandomFactory;

import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.crypto.stream.CryptoInputStream;
import org.apache.commons.crypto.stream.CryptoOutputStream;
import org.apache.commons.crypto.utils.AES;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.owasp.security.logging.SecurityMarkers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope("prototype")
public final class CryptoUtil {

    @Autowired
    private StringUtil stringUtil;

    private final Logger log = LoggerFactory.getLogger(CryptoUtil.class);

    private final Properties properties = new Properties();

    private SecureRandom secureRandom;

    @PostConstruct
    public void init() {
        try {
            secureRandom = SecureRandom.getInstanceStrong();
            properties.setProperty(CryptoRandomFactory.CLASSES_KEY, CryptoRandomFactory.RandomProvider.JAVA.getClassName());
        } catch (Exception ex) {
            log.error(SecurityMarkers.EVENT_FAILURE, "Error Generated on CryptoUtil.init: {} ", stringUtil.sanitizeString(ex.getMessage()));
            log.debug(SecurityMarkers.EVENT_FAILURE, "Error Generated on CryptoUtil.init: {} ", stringUtil.sanitizeString(ExceptionUtils.getStackTrace(ex)));
        }
    }

    public RandomData generateRandomData() {
        try {
            final byte[] key = new byte[32];
            secureRandom.nextBytes(key);
            return new RandomData(key, new byte[16], null);
        } catch (Exception ex) {
            log.error(SecurityMarkers.EVENT_FAILURE, "Error Generated on CryptoUtil.generateRandomData: {} ", stringUtil.sanitizeString(ex.getMessage()));
            log.debug(SecurityMarkers.EVENT_FAILURE, "Error Generated on CryptoUtil.generateRandomData: {} ", stringUtil.sanitizeString(ExceptionUtils.getStackTrace(ex)));
            throw ex;
        }
    }

    public RandomData generateFullRandomData(String base64Key, String base64Iv) {
        try {
            byte[] key = Base64.getDecoder().decode(base64Key);
            byte[] iv = Base64.getDecoder().decode(base64Iv);
            //secureRandom.nextBytes(iv);
            final IvParameterSpec ivParameterSpec = new IvParameterSpec(iv);
            return new RandomData(key, Base64.getDecoder().decode(base64Iv), ivParameterSpec);
        } catch (Exception ex) {
            log.error(SecurityMarkers.EVENT_FAILURE, "Error Generated on CryptoUtil.generateRandomData: {} ", stringUtil.sanitizeString(ex.getMessage()));
            log.debug(SecurityMarkers.EVENT_FAILURE, "Error Generated on CryptoUtil.generateRandomData: {} ", stringUtil.sanitizeString(ExceptionUtils.getStackTrace(ex)));
            throw ex;
        }
    }

    public String encrypt(RandomData randomData, String valueToEncrypt) throws IOException {
        try {
            final SecretKeySpec key = AES.newSecretKeySpec(randomData.getKey());
            log.debug(SecurityMarkers.SECURITY_AUDIT, "Key to Encrypt: {}", stringUtil.sanitizeString(Base64.getEncoder().encodeToString(key.getEncoded())));
            secureRandom.nextBytes(randomData.getIv());
            if(randomData.getIvParameterSpec() == null) {
                randomData.setIvParameterSpec(new IvParameterSpec(randomData.getIv()));
            }
            //Encryption with CryptoOutputStream.
            final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            try (CryptoOutputStream cos = new CryptoOutputStream(AES.CBC_PKCS5_PADDING, properties, outputStream, key, randomData.getIvParameterSpec())) {
                cos.write(stringUtil.getUTF8Bytes(valueToEncrypt));
                cos.flush();
            }
            byte[] encryptedBytes = outputStream.toByteArray();
            log.debug(SecurityMarkers.SECURITY_AUDIT, "Encrypted: {}", stringUtil.sanitizeString(Arrays.toString(encryptedBytes)));
            final String encrypted = Base64.getEncoder().encodeToString(encryptedBytes);
            log.debug(SecurityMarkers.SECURITY_AUDIT, "Encryped String: {} ", stringUtil.sanitizeString(encrypted));
            return encrypted;
        } catch (Exception ex) {
            log.error(SecurityMarkers.EVENT_FAILURE, "Error Generated on CryptoUtil.encrypt: {} ", stringUtil.sanitizeString(ex.getMessage()));
            log.debug(SecurityMarkers.EVENT_FAILURE, "Error Generated on CryptoUtil.encrypt: {} ", stringUtil.sanitizeString(ExceptionUtils.getStackTrace(ex)));
            throw ex;
        }
    }


    public String decrypt(RandomData randomData, String input) throws IOException {
        try {
            final SecretKeySpec key = AES.newSecretKeySpec(randomData.getKey());
            System.out.println("Key Decrypt: " + Base64.getEncoder().encodeToString(key.getEncoded()));
            final InputStream inputStream = new ByteArrayInputStream(Base64.getDecoder().decode(input));
            CryptoInputStream cis = new CryptoInputStream(AES.CBC_PKCS5_PADDING, properties, inputStream, key, randomData.getIvParameterSpec());
            final byte[] decryptedData = new byte[1024];
            int decryptedLen = 0;
            int i;
            while ((i = cis.read(decryptedData, decryptedLen, decryptedData.length - decryptedLen)) > -1) {
                decryptedLen += i;
            }
            final String decrypted = new String(decryptedData, 0, decryptedLen, StandardCharsets.UTF_8);
            System.out.println("Decrypted: " + decrypted);
            return decrypted;
        } catch (Exception ex) {
            log.error(SecurityMarkers.EVENT_FAILURE, "Error Generated on CryptoUtil.decrypt: {} ", stringUtil.sanitizeString(ex.getMessage()));
            log.debug(SecurityMarkers.EVENT_FAILURE, "Error Generated on CryptoUtil.decrypt: {} ", stringUtil.sanitizeString(ExceptionUtils.getStackTrace(ex)));
            throw ex;
        }
    }

    public SecureRandom getSecureRandom() {
        return secureRandom;
    }

    @Override
    public String toString() {
        return "CryptoUtil{" +
                '}';
    }
}
