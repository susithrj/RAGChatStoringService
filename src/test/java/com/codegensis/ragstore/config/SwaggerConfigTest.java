package com.codegensis.ragstore.config;

import io.swagger.v3.oas.models.OpenAPI;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SwaggerConfigTest {

    @Test
    void customOpenAPI_ReturnsConfiguredOpenAPI() {
        // Given
        SwaggerConfig swaggerConfig = new SwaggerConfig();

        // When
        OpenAPI openAPI = swaggerConfig.customOpenAPI();

        // Then
        assertNotNull(openAPI);
        assertNotNull(openAPI.getInfo());
        assertEquals("RAG Chat Storage Service API", openAPI.getInfo().getTitle());
        assertEquals("1.0.0", openAPI.getInfo().getVersion());
        assertNotNull(openAPI.getInfo().getContact());
        assertEquals("susith@gmail.com", openAPI.getInfo().getContact().getEmail());
        assertNotNull(openAPI.getInfo().getLicense());
        assertEquals("Apache 2.0", openAPI.getInfo().getLicense().getName());
    }
}
