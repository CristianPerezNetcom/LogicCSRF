package com.netcom.logintaptophone.dto;

import jakarta.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "login")
public class DynamicMainRequestSoap {
    private String stepRequested;
    private String currentStep;
    private String additionalInfo;

    public String getStepRequested() {
        return stepRequested;
    }

    public DynamicMainRequestSoap(DynamicMainRequest dynamicMainRequest) {
        this.stepRequested = dynamicMainRequest.getStepRequested();
        this.currentStep = dynamicMainRequest.getCurrentStep();
        this.additionalInfo = dynamicMainRequest.getAdditionalInfo();
    }

    public void setStepRequested(String stepRequested) {
        this.stepRequested = stepRequested;
    }

    public String getCurrentStep() {
        return currentStep;
    }

    public void setCurrentStep(String currentStep) {
        this.currentStep = currentStep;
    }

    public String getAdditionalInfo() {
        return additionalInfo;
    }

    public void setAdditionalInfo(String additionalInfo) {
        this.additionalInfo = additionalInfo;
    }

    @Override
    public String toString() {
        return "DynamicMainRequestSoap{" +
                "stepRequested='" + stepRequested + '\'' +
                ", currentStep='" + currentStep + '\'' +
                ", additionalInfo='" + additionalInfo + '\'' +
                '}';
    }
}
