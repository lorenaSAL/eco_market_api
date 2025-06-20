package com.ecomarket.server.resources;

import com.ecomarket.repository.UserRepository;
import com.ecomarket.server.dto.LoginRequest;
import com.ecomarket.server.dto.LoginResponse;
import com.ecomarket.server.util.JwtUtil;
import jakarta.validation.*;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.hibernate.validator.HibernateValidator;
import org.mindrot.jbcrypt.BCrypt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Set;
import java.util.stream.Collectors;

@Path("/api/auth")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class AuthResource {
    private static final Logger LOGGER = LoggerFactory.getLogger(AuthResource.class);
    private final UserRepository userRepository;
    private final Validator validator;

    public AuthResource() {
        this.userRepository = new UserRepository();
        Configuration<?> config = Validation.byProvider(HibernateValidator.class).configure();
        try (ValidatorFactory validatorFactory = config.buildValidatorFactory()) {
            this.validator = validatorFactory.getValidator();
        }
        LOGGER.info("AuthResource initialized");
    }

    @POST
    @Path("/login")
    public Response login(LoginRequest request) {
        LOGGER.debug("Login attempt for email: {}", request.getEmail());
        Set<ConstraintViolation<LoginRequest>> violations = validator.validate(request);
        if (!violations.isEmpty()) {
            String errorMessage = violations.stream()
                    .map(v -> v.getPropertyPath() + ": " + v.getMessage())
                    .collect(Collectors.joining(", "));
            LOGGER.warn("Validation failed for login: {}", errorMessage);
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("Validation failed: " + errorMessage)
                    .build();
        }

        return userRepository.findByEmail(request.getEmail())
                .map(user -> {
                    if (BCrypt.checkpw(request.getPassword(), user.getPassword())) {
                        String token = JwtUtil.generateToken(user);
                        LOGGER.info("Login successful for email: {}", user.getEmail());
                        return Response.ok(new LoginResponse(token, user.getId(), user.getUsername(), user.getRole())).build();
                    } else {
                        LOGGER.warn("Invalid password for email: {}", user.getEmail());
                        return Response.status(Response.Status.UNAUTHORIZED)
                                .entity("Invalid email or password")
                                .build();

                    }
                })
                .orElseGet(() -> {
                    LOGGER.warn("No user found for email: {}", request.getEmail());
                    return Response.status(Response.Status.NOT_FOUND)
                            .entity("No user found")
                            .build();
                });
    }

    @POST
    @Path("/logout")
    public Response logout() {
        LOGGER.debug("Logout request received");
        // Client-side logout clears token
        return Response.ok("Logged out successfully").build();
    }

}
