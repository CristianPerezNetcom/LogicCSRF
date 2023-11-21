package com.netcom.logintaptophone.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

public final class DynamicHSMResponse {
    @JsonProperty("randomNumber")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private final String randomNumber;
    @JsonProperty("message")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private final String message;
    @JsonProperty("exceptionMessage")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private final String exceptionMessage;
    @JsonProperty("userMessage")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private final String userMessage;

    public DynamicHSMResponse(String randomNumber, String message, String exceptionMessage, String userMessage) {
        this.randomNumber = randomNumber;
        this.message = message;
        this.exceptionMessage = exceptionMessage;
        this.userMessage = userMessage;
    }

    public String getRandomNumber() {
        return randomNumber;
    }

    public String getMessage() {
        return message;
    }

    public String getExceptionMessage() {
        return exceptionMessage;
    }

    public String getUserMessage() {
        return userMessage;
    }
}
