package com.bank.api_gateway.controller;

import com.bank.api_gateway.service.TraceService;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

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

    @GetMapping("/traces")
    public Mono<ResponseEntity<JsonNode>> getRecentTraces(
            @RequestParam(defaultValue = "20") int limit
    ) {

        int safeLimit = Math.min(Math.max(limit, 1), 100);

        return traceService
                .getRecentTraces(safeLimit)
                .map(ResponseEntity::ok);
    }

    @GetMapping("/traces/{traceId}")
    public Mono<ResponseEntity<JsonNode>> getTrace(
            @PathVariable String traceId
    ) {

        return traceService
                .getTrace(traceId)
                .map(ResponseEntity::ok);
    }
}