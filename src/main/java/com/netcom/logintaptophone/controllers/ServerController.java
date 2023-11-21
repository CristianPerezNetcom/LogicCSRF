package com.netcom.logintaptophone.controllers;

import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.slf4j.Logger;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

@RestController
@RequestMapping(value = "/server-app")
public class ServerController {
    private final Logger log = LoggerFactory.getLogger(ServerController.class);

    @Value("${application.messages.hello}")
    public String messageHello;


    @GetMapping(value = "/data")
    public String getData() {

       /* String messageHello;
        messageHello ="Hello from Server-App-data method";*/

        String encodedHello = Base64.getEncoder().encodeToString(messageHello.getBytes(StandardCharsets.UTF_8));

        log.info("Returning data from server-app data method");
        return encodedHello;
    }

    @Override
    public String toString() {
        return "ServerController{" +
                "messageHello='" + messageHello + '\'' +
                '}';
    }
}
