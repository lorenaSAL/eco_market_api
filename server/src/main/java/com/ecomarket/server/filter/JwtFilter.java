package com.ecomarket.server.filter;

import com.ecomarket.server.util.JwtUtil;
import io.jsonwebtoken.Claims;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.Provider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

@Provider
@JwtSecured
public class JwtFilter implements ContainerRequestFilter {
    private static final Logger LOGGER = LoggerFactory.getLogger(JwtFilter.class);

    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {
        String authHeader = requestContext.getHeaderString("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            try {
                Claims claims = JwtUtil.validateToken(token);
                // Add user info to request context
                requestContext.setProperty("userId", claims.get("userId", Long.class));
                requestContext.setProperty("username", claims.get("username", String.class));
                requestContext.setProperty("role", claims.get("role", String.class));
                LOGGER.debug("JWT validated for user: {}", claims.getSubject());
            } catch (Exception e) {
                LOGGER.warn("Invalid JWT: {}", e.getMessage());
                requestContext.abortWith(Response.status(Response.Status.UNAUTHORIZED)
                        .entity("Invalid token")
                        .build());
            }
        } else {
            LOGGER.warn("No valid Bearer token provided");
            requestContext.abortWith(Response.status(Response.Status.UNAUTHORIZED)
                    .entity("Authentication required")
                    .build());
        }
    }
}
