package com.ecomarket.server;


import com.ecomarket.server.filter.CorsFilter;
import com.ecomarket.server.filter.JwtFilter;
import com.ecomarket.server.provider.CustomObjectMapperProvider;
import com.ecomarket.server.resources.*;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.jersey.server.ResourceConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.bridge.SLF4JBridgeHandler;

import java.net.URI;
import java.util.logging.LogManager;

public class MainServer {

    static {
        LogManager.getLogManager().reset();
        SLF4JBridgeHandler.install();
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(MainServer.class);
    private static final String BASE_URI = "http://localhost:8080";

    public static void main(String[] args) {
        LOGGER.info("Starting HTTP server");

        ResourceConfig config = new ResourceConfig();
        config.register(CorsFilter.class);
        config.register(JwtFilter.class);
        config.register(ProductResource.class);
        config.register(UserResource.class);
        config.register(CartResource.class);
        config.register(CartItemResource.class);
        config.register(OrderResource.class);
        config.register(AuthResource.class);
        config.register(CustomObjectMapperProvider.class);

        GrizzlyHttpServerFactory.createHttpServer(URI.create(BASE_URI), config);
    }
}
