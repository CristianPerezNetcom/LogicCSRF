package com.netcom.logintaptophone.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

public final class DynamicAuthenticationResponse {
    @JsonProperty("access_token")
    private final String accessToken;
    @JsonProperty("refresh_token")
    private final String refreshToken;
    @JsonProperty("message")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private final String message;
    @JsonProperty("exceptionMessage")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private final String exceptionMessage;
    @JsonProperty("userMessage")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private final String userMessage;

    public DynamicAuthenticationResponse(String accessToken, String refreshToken, String message, String exceptionMessage, String userMessage) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.message = message;
        this.exceptionMessage = exceptionMessage;
        this.userMessage = userMessage;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public String getRefreshToken() {
        return refreshToken;
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
