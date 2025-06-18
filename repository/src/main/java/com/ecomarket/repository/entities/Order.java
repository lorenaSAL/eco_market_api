package com.ecomarket.repository.entities;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class Order {
    private Long id;
    @NotNull(message = "User ID is required")
    private Long userId;
    private BigDecimal totalPrice;
    private String status;
    private LocalDateTime createdAt;

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    @JsonProperty("user_id")
    public Long getUserId() { return userId; }
    @JsonProperty("user_id")
    public void setUserId(Long userId) { this.userId = userId; }

    @JsonProperty("total_price")
    public BigDecimal getTotalPrice() { return totalPrice; }
    @JsonProperty("total_price")
    public void setTotalPrice(BigDecimal totalPrice) { this.totalPrice = totalPrice; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    @JsonProperty("created_at")
    public LocalDateTime getCreatedAt() { return createdAt; }
    @JsonProperty("created_at")
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
