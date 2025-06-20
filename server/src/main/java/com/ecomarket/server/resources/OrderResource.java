package com.ecomarket.server.resources;

import com.ecomarket.repository.OrderRepository;
import com.ecomarket.repository.entities.Order;
import com.ecomarket.server.filter.JwtSecured;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

@Path("/api/orders")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class OrderResource {
    private static final Logger LOGGER = LoggerFactory.getLogger(OrderResource.class);
    private final OrderRepository orderRepository;

    public OrderResource() {
        orderRepository = new OrderRepository();
        LOGGER.info("OrderResource initialized");
    }

    @POST
    @SuppressWarnings("unused")
    public Response creteOrder(Order order) {
        LOGGER.debug("Creating order for user_id: {}", order.getUserId());
        try {
            Order savedOrder = orderRepository.save(order);
            return Response.status(Response.Status.CREATED).entity(savedOrder).build();
        } catch (Exception e) {
            LOGGER.error("Failed to crete order for user_id: {}", order.getUserId(), e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Failed to create order: " + e.getMessage())
                    .build();
        }
    }

    @GET
    @SuppressWarnings("unused")
    public List<Order> getAllOrders() {
        LOGGER.debug("Retrieving all orders");
        List<Order> orders = orderRepository.findAll();
        LOGGER.debug("Retrieved {} orders", orders.size());
        return orders;
    }

    @GET
    @Path("/user/{userId}")
    @JwtSecured
    @SuppressWarnings("unused")
    public List<Order> getOrdersByUserId(@PathParam("userId") Long userId) {
        LOGGER.debug("Retrieving orders for user id: {}", userId);
        try {
            List<Order> orders = orderRepository.findByUserId(userId);
            LOGGER.info("Successfully retrieved {} order for user_id: {}", orders.size(), userId);
            return orders;
        } catch (Exception e) {
            LOGGER.error("Failed to retrieve orders for user id: {}", userId, e);
            throw e;
        }
    }
}
