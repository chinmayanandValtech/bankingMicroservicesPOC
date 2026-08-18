package com.bank.api_gateway.controller;

import com.bank.api_gateway.service.TraceService;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/observability")
@CrossOrigin(
        origins = {
                "http://localhost:3000",
                "http://localhost:5173",
                "https://banking-management-frontend.vercel.app"
        }
)
public class TraceController {

    private final TraceService traceService;

    public TraceController(TraceService traceService) {
        this.traceService = traceService;
    }

    @GetMapping("/traces/{traceId}")
    public ResponseEntity<JsonNode> getTrace(
            @PathVariable String traceId
    ) {

        JsonNode trace = traceService.getTrace(traceId);

        return ResponseEntity.ok(trace);
    }
}