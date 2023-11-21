package com.netcom.logintaptophone;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class Info {

    @Value("${server.port}")
    int port;

    @EventListener(ApplicationReadyEvent.class)
    public void contextRefreshedEvent() {
        System.out.println("The following endpoints are available by default :-");
        System.out.println("  Health        : https://localhost:" + port + "/health");
        System.out.println("  JWT Register         : https://localhost:" + port + "/api/v1/auth/register");
        System.out.println("  JWT Authenticate         : https://localhost:" + port + "/api/v1/auth/authenticate");
        System.out.println("  JWT Refresh Token         : https://localhost:" + port + "/api/v1/auth/refresh-token");
        System.out.println("  JWT LogOut         : https://localhost:" + port + "/api/v1/auth/logout");
        System.out.println("  ServerHello        : https://localhost:" + port + "/server-app/data");
        System.out.println("  Orchestrator        : https://localhost:" + port + "/Orchestrator");
    }
}
