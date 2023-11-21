package com.netcom.logintaptophone.util;

import com.netcom.logintaptophone.dto.DynamicAuthenticationResponse;
import com.netcom.logintaptophone.dto.DynamicHSMResponse;
import com.netcom.logintaptophone.dto.DynamicResponseServices;
import org.springframework.stereotype.Component;

/**
 * This class is in charge to create dynamic responses of Restful Services
 *
 * <p>
 * By Netcom. All rights reserved.
 * <p>
 *
 * @since   2023-04-18
 * @version 0.0.1
 * @author  Cristian Pérez
 *
 */
@Component
public class RestfulUtil {

    /**
     * Method for return an OK dynamic response in the Restful Services
     * @param message  Message to be returned on the service.
     * @param nextStep  Next actions to be done for Front-End logic on Service Consumptions.
     * @param userMessage  Message to be shown to the user.
     * @return RestfulResponse Fully charged and ready RestfulResponse to be returned by the Services.
     */
    public DynamicResponseServices getOkResponse(String message, String nextStep, String userMessage){
        return new DynamicResponseServices((short) 0,message,nextStep,null,userMessage);
    }

    /**
     * Method for return an ERROR dynamic response in the Restful Services
     * @param message  Message to be returned on the service.
     * @param messageException  Custom or extracted message representing the description of the exception.
     * @param userMessage  Message to be shown to the user.
     * @return RestfulResponse Fully charged and ready RestfulResponse to be returned by the Services.
     */
    public DynamicResponseServices getErrorResponse(String message, String messageException, String nextStep, String userMessage){
        return new DynamicResponseServices((short) 1,message,nextStep,messageException,userMessage);
    }

    public DynamicAuthenticationResponse getAuthenticationOkResponse(String accessToken, String refreshToken, String message, String userMessage){
        return new DynamicAuthenticationResponse(accessToken, refreshToken, message, null, userMessage);
    }

    public DynamicAuthenticationResponse getAuthenticationErrorResponse(String message, String exceptionMessage, String userMessage){
        return new DynamicAuthenticationResponse(null, null, message, exceptionMessage, userMessage);
    }

    public DynamicHSMResponse getHSMOkResponse(String randomNumber, String message, String userMessage){
        return new DynamicHSMResponse(randomNumber, message, null, userMessage);
    }

    public DynamicHSMResponse getHSMErrorResponse(String message, String exceptionMessage, String userMessage){
        return new DynamicHSMResponse(null, message, exceptionMessage, userMessage);
    }
}

