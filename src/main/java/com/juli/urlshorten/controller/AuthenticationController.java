package com.juli.urlshorten.controller;

import com.juli.urlshorten.model.api.LoginRequest;
import com.juli.urlshorten.service.LoginService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthenticationController {
    private final LoginService loginService;

    public AuthenticationController(LoginService loginService) {
        this.loginService = loginService;
    }

    @Operation(summary = "Authenticate user and generate token", description = "This endpoint authenticates a user by their username and password and returns a JWT token.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Authentication successful, token generated"),
            @ApiResponse(responseCode = "400", description = "Bad request, invalid credentials")
    })
    @PostMapping(value = "/login", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> login(@Parameter(description = "Login request containing username and password") @RequestBody LoginRequest loginRequest) {
        String token = loginService.authenticateUser(loginRequest.getUsername(), loginRequest.getPassword());
        return ResponseEntity.ok(token);
    }

    @Operation(summary = "Access admin resource", description = "This endpoint is restricted to users with the ADMIN role.")
    @PreAuthorize("hasRole('ADMIN')")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Access granted to admin resource"),
            @ApiResponse(responseCode = "403", description = "Forbidden, insufficient permissions")
    })
    @GetMapping(value = "/admin", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> admin() {
        return ResponseEntity.ok("Admin");
    }

    @Operation(summary = "Access user resource", description = "This endpoint is restricted to users with the USER role.")
    @PreAuthorize("hasRole('USER')")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Access granted to user resource"),
            @ApiResponse(responseCode = "403", description = "Forbidden, insufficient permissions")
    })
    @GetMapping(value = "/user", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> user() {
        return ResponseEntity.ok("User");
    }
}