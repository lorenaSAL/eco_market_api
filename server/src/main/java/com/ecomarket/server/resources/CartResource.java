package com.ecomarket.server.resources;

import com.ecomarket.repository.CartRepository;
import com.ecomarket.repository.entities.Cart;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

@Path("/api/carts")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class CartResource {
    private static final Logger LOGGER = LoggerFactory.getLogger(CartResource.class);
    private final CartRepository cartRepository;

    public CartResource() {
        this.cartRepository = new CartRepository();
        LOGGER.info("CartResource initialized");
    }

    @POST
    @SuppressWarnings("unused")
    public Response createCart(Cart cart) {
        LOGGER.debug("Creating cart for user_id: {}", cart.getUserId());
        try {
            Cart cartSaved = cartRepository.save(cart);
            return Response.status(Response.Status.CREATED).entity(cartSaved).build();
        } catch (Exception e) {
           LOGGER.error("Failed to create cart for user_id: {}", cart.getUserId(), e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Failed to create cart : " + e.getMessage())
                    .build();
        }
    }

    @GET
    @Path("/{id}")
    @SuppressWarnings("unused")
    public Response getCart(@PathParam("id") Long id) {
        try {
            return cartRepository.findById(id)
                    .map(cart -> {
                        LOGGER.info("Successfully retrieved cart: {}", id);
                        return Response.ok(cart).build();
                    })
                    .orElseGet(() -> {
                       LOGGER.warn("Cart not found: {}", id);
                       return Response.status(Response.Status.NOT_FOUND).build();
                    });
        } catch (Exception e) {
            LOGGER.error("Failed to retrieve cart: {}", id, e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Failed to retrieve cart: " + e.getMessage())
                    .build();
        }
    }

    @GET
    @SuppressWarnings("unused")
    public List<Cart> getAllCarts() {
        LOGGER.debug("Retrieving all carts");
        List<Cart> carts = cartRepository.findAll();
        LOGGER.debug("Retrieved {} carts", carts.size());
        return carts;
    }

    @GET
    @Path("/user/{userId}")
    @SuppressWarnings("unused")
    public Response getCartByUserId(@PathParam("userId") Long userId) {
        LOGGER.debug("Retrieving cart for user_id: {}", userId);
        try {
            return cartRepository.findByUserId(userId)
                    .map(cart -> {
                        LOGGER.info("Successfully retrieved cart for user_id: {}", userId);
                        return Response.ok(cart).build();
                    })
                    .orElseGet(() -> {
                       LOGGER.warn("Cart not found for user_id: {}", userId);
                       return Response.status(Response.Status.NOT_FOUND).build();
                    });
        } catch (Exception e) {
            LOGGER.error("Failed to retrieved cart for user_id: {}", userId, e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Failed to retrieve card: " + e.getMessage())
                    .build();
        }
    }
}
