package com.ecomarket.repository.entities;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;

public class Cart {
    private Long id;

    @NotNull(message = "User ID is required")
    private Long userId;

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    @JsonProperty("user_id")
    public Long getUserId() { return userId; }
    @JsonProperty("user_id")
    public void setUserId(Long userId) { this.userId = userId; }
}
