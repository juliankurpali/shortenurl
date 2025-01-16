package com.juli.urlshorten.configuration.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.juli.urlshorten.exception.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;
import java.io.IOException;
import java.time.LocalDateTime;

@Component
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException {
        ErrorResponse errorResponse = new ErrorResponse();
        errorResponse.setTimestamp(LocalDateTime.now());
        errorResponse.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        errorResponse.setError("Unauthorized");
        errorResponse.setMessage("Full authentication is required to access this resource.");

        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.getWriter().write(toJson(errorResponse));
    }

    private String toJson(ErrorResponse errorResponse) {
        try {
            return new ObjectMapper().writeValueAsString(errorResponse);
        } catch (IOException e) {
            throw new RuntimeException("Error converting ErrorResponse to JSON", e);
        }
    }
}
