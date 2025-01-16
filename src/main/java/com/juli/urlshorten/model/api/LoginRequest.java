package com.juli.urlshorten.model.api;

import lombok.Data;

@Data
public class LoginRequest {
    private String username;
    private String password;
}
