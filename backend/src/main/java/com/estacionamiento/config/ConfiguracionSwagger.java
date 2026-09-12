package com.estacionamiento.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ConfiguracionSwagger {

    @Bean
    public OpenAPI documentacion() {
        return new OpenAPI().info(new Info()
                .title("API de gestion de estacionamiento")
                .version("1.0.0")
                .description("Registro de entradas y salidas, calculo de cobros e informe de pagos"));
    }
}
