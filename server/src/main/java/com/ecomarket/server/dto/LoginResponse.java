package com.ecomarket.server.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class LoginResponse {
    private String token;
    private Long userId;
    private String username;
    private String role;

    public LoginResponse(String token, Long userId, String username, String role) {
        this.token = token;
        this.userId = userId;
        this.username = username;
        this.role = role;
    }

    public String getToken() { return token; }
    @JsonProperty("userId") // Force camel case in JSON
    public Long getUserId() { return userId; }
    public String getUsername() { return username; }
    public String getRole() { return role; }
}

// Using @JsonProperty annotation
// Why is "userId" mapped to "user_id" on login in UI?
// - By default, Jackson derives JSON property name from getter methods (e.g. getUserId() -> userId)
// - With PropertyNamingStrategies.SNAKE_CASE, Jackson converts camelCase to snake_case (e.g. userId -> user_id)
// - If the getter is getUserid(), Jackson treats it as 'userid' and converts it to 'userid'