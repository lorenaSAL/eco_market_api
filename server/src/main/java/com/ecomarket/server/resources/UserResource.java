package com.ecomarket.server.resources;

import com.ecomarket.repository.UserRepository;
import com.ecomarket.repository.entities.User;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

@Path("/api/users")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class UserResource {
    private static final Logger LOGGER = LoggerFactory.getLogger(UserResource.class);
    private final UserRepository userRepository;

    public UserResource() {
        userRepository = new UserRepository();
        LOGGER.info("UserResource initialized");
    }

    @POST
    @SuppressWarnings("unused")
    public Response createUser(User user) {
        LOGGER.debug("Creating user: {}", user.getUsername());
        try {
            User savedUser = userRepository.save(user);
            return Response.status(Response.Status.CREATED).entity(savedUser).build();
        } catch (Exception e) {
            LOGGER.error("Failed to create user: {}", user.getUsername(), e);
            throw e;
        }
    }

    @GET
    @Path("/{id}")
    @SuppressWarnings("unused")
    public Response getUserById(@PathParam("id") Long id) {
        LOGGER.debug("Retrieving user by ID: {}", id);
        try {
            return userRepository.findById(id)
                    .map(user -> {
                        LOGGER.info("Successfully retrieved user: {}", user.getUsername());
                        return Response.ok(user).build();
                    })
                    .orElseGet(() -> {
                        LOGGER.debug("User not found: {}", id);
                        return Response.status(Response.Status.NOT_FOUND).build();
                    });
        } catch (Exception e) {
            LOGGER.error("Failed to retrieve user: {}", id, e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Failed to retrieve user: " + e.getMessage())
                    .build();
        }
    }

    @GET
    @SuppressWarnings("unused")
    public List<User> getAllUsers() {
        LOGGER.debug("Retrieving all users");
        List<User> users = userRepository.findAll();
        LOGGER.debug("Retrieved {} users", users.size());
        return users;
    }

}
