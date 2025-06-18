package com.ecomarket.server.resources;

import com.ecomarket.repository.ProductRepository;
import com.ecomarket.repository.entities.Product;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

@Path("/api/products")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ProductResource {
    private static final Logger LOGGER = LoggerFactory.getLogger(ProductResource.class);
    private final ProductRepository productRepository;

    public ProductResource() {
        this.productRepository = new ProductRepository();
        LOGGER.info("ProductResource initialized");
    }

    @POST
    @SuppressWarnings("unused")
    public Response createProduct(Product product) {
        LOGGER.debug("Creating product: {}", product.getName());
        try {
            Product savedProduct = productRepository.save(product);
            return Response.status(Response.Status.CREATED).entity(savedProduct).build();
        } catch (Exception e) {
            LOGGER.error("Failed to create product: {}", product.getName(), e);
            throw e;
        }
    }

    @GET
    @Path("/{id}")
    @SuppressWarnings("unused")
    public Response getProductById(@PathParam("id") Long id) {
        try {
            return productRepository.findById(id)
                    .map(product -> {
                        LOGGER.info("Successfully retrieved product: {}", product.getId());
                        return Response.ok(product).build();
                    })
                    .orElseGet(() -> {
                        LOGGER.warn("Product not found: {}", id);
                        return Response.status(Response.Status.NOT_FOUND).build();
                    });
        } catch (Exception e) {
            LOGGER.error("Failed to retrieve product: {}", id, e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Failed to retrieve product: " + e.getMessage())
                    .build();
        }
    }

    @GET
    @SuppressWarnings("unused")
    public List<Product> getAllProducts() {
        LOGGER.debug("Retrieving all products");
        List<Product> products = productRepository.findAll();
        LOGGER.debug("Retrieved {} products", products.size());
        return products;
    }
}
