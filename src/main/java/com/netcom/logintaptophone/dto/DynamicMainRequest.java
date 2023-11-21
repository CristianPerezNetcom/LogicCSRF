package com.netcom.logintaptophone.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

public final class DynamicMainRequest {
    @JsonProperty("stepRequested")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String stepRequested;
    @JsonProperty("currentStep")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String currentStep;
    @JsonProperty("additionalInfo")
    private String additionalInfo;

    public DynamicMainRequest(String stepRequested, String currentStep, String additionalInfo) {
        this.stepRequested = stepRequested;
        this.currentStep = currentStep;
        this.additionalInfo = additionalInfo;
    }

    public String getStepRequested() {
        return stepRequested;
    }

    public String getCurrentStep() {
        return currentStep;
    }

    public String getAdditionalInfo() {
        return additionalInfo;
    }

    @Override
    public String toString() {
        return "DynamicMainRequest{" +
                "stepRequested='" + stepRequested + '\'' +
                ", currentStep='" + currentStep + '\'' +
                ", additionalInfo='" + additionalInfo + '\'' +
                '}';
    }
}
