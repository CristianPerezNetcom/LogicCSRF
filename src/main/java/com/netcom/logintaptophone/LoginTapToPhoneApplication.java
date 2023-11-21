package com.netcom.logintaptophone;

import com.ulisesbocchio.jasyptspringboot.annotation.EnableEncryptableProperties;
import org.springdoc.core.GroupedOpenApi;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
@EnableEncryptableProperties
public class LoginTapToPhoneApplication {

    @Bean
    public GroupedOpenApi actuatorOpenApi() {
        return GroupedOpenApi.builder()
                .group("api")
                .pathsToMatch("/health")
                .build();
    }
    public static void main(String[] args) {
        SpringApplication.run(LoginTapToPhoneApplication.class, args);
    }
    
}
