package com.ecomarket.repository.entities;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;

public class CartItem {
    private Long id;

    @NotNull(message = "Cart ID is required")
    private Long cartId;

    @NotNull(message = "Product ID is required")
    private Long productId;

    private Integer quantity;

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    @JsonProperty("cart_id")
    public Long getCartId() { return cartId; }
    @JsonProperty("cart_id")
    public void setCartId(Long cartId) { this.cartId = cartId; }

    @JsonProperty("product_id")
    public Long getProductId() { return this.productId; }
    @JsonProperty("product_id")
    public void setProductId(Long productId) { this.productId = productId; }

    public Integer getQuantity() { return quantity; }
    public void setQuantity(Integer quantity) { this.quantity = quantity; }

}
