package com.netcom.logintaptophone.controllers;


import com.netcom.logintaptophone.dto.DynamicMainRequest;
import com.netcom.logintaptophone.dto.DynamicResponseServices;
import com.netcom.logintaptophone.services.OrchestratorService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.security.GeneralSecurityException;

@RestController
@RequestMapping("Orchestrator")
public class MainOrchestrator {

    /**
     * Process Object in charge to handle and manage request coming from Front End.
     */
    @Autowired
    private OrchestratorService orchestratorService;

    /**
     * Method for expose endpoint for process next step requested by Front End.
     *
     * @param dynamicMainRequest Object with the information requested for the processing of the next step.
     * @return RestfulResponse Response with the next step to be followed by Front End.
     */
    @PostMapping(value = "/getNextStep")
    @Operation(summary = "Getting next step to be passed to Front End.")
    public @ResponseBody
    ResponseEntity<DynamicResponseServices> processNextStep(HttpServletRequest request, @RequestBody DynamicMainRequest dynamicMainRequest) throws GeneralSecurityException, IOException {
        return orchestratorService.processNextStep(request, dynamicMainRequest);
    }
}
