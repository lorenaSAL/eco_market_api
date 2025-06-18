package com.ecomarket.server.resources;

import com.ecomarket.repository.CartItemRepository;
import com.ecomarket.repository.entities.CartItem;
import jakarta.validation.*;
import jakarta.ws.rs.*;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.hibernate.validator.HibernateValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Path("/api/cart-items")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class CartItemResource {
    private static final Logger LOGGER = LoggerFactory.getLogger(CartItemResource.class);
    private final CartItemRepository cartItemRepository;
    private final Validator validator;

    public CartItemResource() {
        cartItemRepository = new CartItemRepository();
        Configuration<?> config = Validation.byProvider(HibernateValidator.class).configure();
        try (ValidatorFactory factory = config.buildValidatorFactory()) {
            this.validator = factory.getValidator();
        }
        LOGGER.info("CartItemResource initialized");
    }

    @POST
    @SuppressWarnings("unused")
    public Response createCartItem(CartItem cartItem) {
        LOGGER.debug("Creating cart item for cart_id: {}", cartItem.getCartId());
        try {
            CartItem savedCartItem = cartItemRepository.save(cartItem);
            return Response.status(Response.Status.CREATED).entity(savedCartItem).build();
        } catch (Exception e) {
            LOGGER.error("Failed to create cart item for cart_id: {}", cartItem.getCartId());
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Failed to create cartItem: " + e.getMessage())
                    .build();
        }
    }

    @GET
    @Path("/{id}")
    @SuppressWarnings("unused")
    public Response getCartItem(@PathParam("id") Long id) {
        try {
            return cartItemRepository.findById(id)
                    .map(cartItem -> {
                        LOGGER.info("Successfully retrieved cart item: {}", cartItem.getCartId());
                        return Response.ok(cartItem).build();
                    })
                    .orElseGet(() -> {
                        LOGGER.warn("Cart item not found: {}", id);
                        return Response.status(Response.Status.NOT_FOUND).build();
                    });
        } catch (Exception e) {
            LOGGER.error("Failed to retrieve cart item: {}", id, e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Failed to retrieve cart item: " + e.getMessage())
                    .build();
        }
    }

    @GET
    @SuppressWarnings("unused")
    public List<CartItem> getAllCartItems() {
        LOGGER.debug("Retrieving all cart items");
        List<CartItem> cartItems = cartItemRepository.findAll();
        LOGGER.debug("Retrieved {} cart items", cartItems.size());
        return cartItems;
    }

    @GET
    @Path("/cart/{cartId}")
    @SuppressWarnings("unused")
    public List<CartItem> getCartItemsByCartId(@PathParam("cartId") Long cartId) {
        LOGGER.debug("Retrieving cat item for cart_id: {}", cartId);
        try {
            List<CartItem> cartItems = cartItemRepository.findByCartId(cartId);
            LOGGER.info("Successfully retrieved {} cart items for cart_id: {}", cartItems.size(), cartId);
            return cartItems;
        } catch (Exception e) {
            LOGGER.error("Failed to retrieve cart items for cart_id: {}", cartId, e);
            throw e;
        }
    }

    @PUT
    @Path("/{id}")
    @SuppressWarnings("unused")
    public Response updateCartItem(@PathParam("id") Long id, CartItem cartItem) {
        LOGGER.debug("Updating cart item: {}", id);
        try {
            if (cartItem == null) {
                LOGGER.warn("Received null cart item in updateCartItem");
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity("Cart item cannot be null")
                        .build();
            }
            Set<ConstraintViolation<CartItem>> violations = validator.validate(cartItem);
            if (!violations.isEmpty()) {
                String errorMessage = violations.stream()
                        .map(v -> v.getPropertyPath() + ": " + v.getMessage())
                        .collect(Collectors.joining(", "));
                LOGGER.warn("Validation failed for cart item: {}", errorMessage);
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity("Validation failed: " + errorMessage)
                        .build();
            }

            return cartItemRepository.findById(id)
                    .map(existing -> {
                        cartItem.setId(id);
                        CartItem updated = cartItemRepository.save(cartItem);
                        LOGGER.info("Successfully updated cart item: {}", cartItem.getCartId());
                        return Response.ok(updated).build();
                    })
                    .orElseGet(() -> {
                        LOGGER.warn("Cart item not found for update: {}", id);
                        return Response.status(Response.Status.NOT_FOUND).build();
                    });
        } catch (Exception e) {
            LOGGER.error("Failed to update cart item: {}", id, e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Failed to update cart item: " + e.getMessage())
                    .build();
        }
    }

    @DELETE
    @Path("/{id}")
    @SuppressWarnings("unused")
    public Response deleteCartItem(@PathParam("id") Long id) {
        LOGGER.debug("Deleting cart item: {}", id);
        try {
            cartItemRepository.delete(id);
            LOGGER.info("Successfully deleted cart item: {}", id);
            return Response.noContent().build();
        } catch (Exception e) {
            LOGGER.error("Failed to delete cart item: {}", id, e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Failed to delete cart item: " + e.getMessage())
                    .build();
        }
    }
}
