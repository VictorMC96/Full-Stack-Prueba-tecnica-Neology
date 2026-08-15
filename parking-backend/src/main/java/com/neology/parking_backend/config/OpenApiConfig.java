package com.neology.parking_backend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI parkingOpenApi() {
        return new OpenAPI()
                .info(new Info()
                        .title("Parking API")
                        .description("API para la gestión de acceso de vehículos a un estacionamiento: "
                                + "alta de vehículos, registro de entradas/salidas, cálculo de cobros "
                                + "y reportes de pagos de residentes.")
                        .version("v1.0")
                        .contact(new Contact()
                                .name("Neology - Prueba Técnica")
                                .email("alex_molinero123@hotmail.com")));
    }

}
