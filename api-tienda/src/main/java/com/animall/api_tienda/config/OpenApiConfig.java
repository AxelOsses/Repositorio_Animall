package com.animall.api_tienda.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI apiTiendaOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("API Tienda de Mascotas")
                        .description("API REST para aplicación móvil de tienda de mascotas. Incluye usuarios, catálogo, carrito, pedidos y soporte.")
                        .version("1.0"));
    }
}
