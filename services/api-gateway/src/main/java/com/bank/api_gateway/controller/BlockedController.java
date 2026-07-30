package com.bank.api_gateway.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * Local target for routes that must not be reachable from outside the gateway
 * (e.g. internal service-to-service endpoints). See api-gateway.yml in the
 * config repo for the routes that forward here.
 */
@RestController
public class BlockedController {

    @RequestMapping("/__blocked")
    public ResponseEntity<Map<String, Object>> blocked() {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Map.of(
                        "status", 404,
                        "error", "Not Found"
                ));
    }
}
