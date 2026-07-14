package com.trademaster.ims.dev;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/dev/phase11-fixtures")
@ConditionalOnProperty(prefix = "techsmart.dev-fixtures", name = "enabled", havingValue = "true")
public class Phase11LocalFixtureController {
    private final Phase11LocalFixtureService service;

    public Phase11LocalFixtureController(Phase11LocalFixtureService service) {
        this.service = service;
    }

    @PostMapping("/customer-fulfillment")
    @ResponseStatus(HttpStatus.CREATED)
    public Phase11LocalFixtureService.FixtureResponse create(HttpServletRequest request) {
        if (!isLoopback(request.getRemoteAddr())) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        return service.create();
    }

    @GetMapping("/customer-fulfillment/{orderNumber}/verification")
    public Phase11LocalFixtureService.VerificationResponse verify(@PathVariable String orderNumber, HttpServletRequest request) {
        if (!isLoopback(request.getRemoteAddr())) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        return service.verify(orderNumber);
    }

    @PostMapping("/cleanup")
    public Phase11LocalFixtureService.CleanupResponse cleanup(HttpServletRequest request) {
        if (!isLoopback(request.getRemoteAddr())) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        return service.cleanup();
    }
    private boolean isLoopback(String remoteAddress) {
        return remoteAddress == null
                || "127.0.0.1".equals(remoteAddress)
                || "0:0:0:0:0:0:0:1".equals(remoteAddress)
                || "::1".equals(remoteAddress)
                || "localhost".equalsIgnoreCase(remoteAddress);
    }
}