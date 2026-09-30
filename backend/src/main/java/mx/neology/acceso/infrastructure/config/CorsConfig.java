package mx.neology.acceso.infrastructure.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/** Permite al frontend de desarrollo (ng serve) llamar a la API sin pasar por el proxy. */
@Configuration
public class CorsConfig implements WebMvcConfigurer {

  @Override
  public void addCorsMappings(CorsRegistry registry) {
    registry
        .addMapping("/neo/**")
        .allowedOrigins("http://localhost:4200")
        .allowedMethods("GET", "POST", "OPTIONS")
        .allowedHeaders("*")
        .exposedHeaders("Location", "Content-Disposition");
  }
}
