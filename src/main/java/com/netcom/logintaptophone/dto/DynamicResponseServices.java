package com.netcom.logintaptophone.dto;

public class DynamicResponseServices {

    private int code;
    private String message;
    private String nextStep;
    private String exceptionMessage;
    private String userMessage;

    public DynamicResponseServices(int code, String message, String nextStep, String exceptionMessage, String userMessage) {
        this.code = code;
        this.message = message;
        this.nextStep = nextStep;
        this.exceptionMessage = exceptionMessage;
        this.userMessage = userMessage;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getNextStep() {
        return nextStep;
    }

    public void setNextStep(String nextStep) {
        this.nextStep = nextStep;
    }

    public String getExceptionMessage() {
        return exceptionMessage;
    }

    public void setExceptionMessage(String exceptionMessage) {
        this.exceptionMessage = exceptionMessage;
    }

    public String getUserMessage() {
        return userMessage;
    }

    public void setUserMessage(String userMessage) {
        this.userMessage = userMessage;
    }

    @Override
    public String toString() {
        return "DynamicResponseServices{" +
                "code=" + code +
                ", message='" + message + '\'' +
                ", nextStep='" + nextStep + '\'' +
                ", exceptionMessage='" + exceptionMessage + '\'' +
                ", userMessage='" + userMessage + '\'' +
                '}';
    }
}
