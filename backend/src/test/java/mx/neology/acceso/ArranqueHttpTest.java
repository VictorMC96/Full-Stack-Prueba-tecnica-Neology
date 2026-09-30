package mx.neology.acceso;

import static org.assertj.core.api.Assertions.assertThat;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ArranqueHttpTest {

  @Value("${local.server.port}")
  int puerto;

  private HttpResponse<String> obtener(String ruta) throws Exception {
    HttpRequest peticion =
        HttpRequest.newBuilder(URI.create("http://localhost:" + puerto + ruta)).GET().build();
    return HttpClient.newHttpClient().send(peticion, HttpResponse.BodyHandlers.ofString());
  }

  @Test
  void laSaludRespondeUp() throws Exception {
    HttpResponse<String> respuesta = obtener("/actuator/health");
    assertThat(respuesta.statusCode()).isEqualTo(200);
    assertThat(respuesta.body()).contains("UP");
  }

  @Test
  void laDocumentacionOpenApiEstaPublicada() throws Exception {
    HttpResponse<String> respuesta = obtener("/v3/api-docs");
    assertThat(respuesta.statusCode()).isEqualTo(200);
    assertThat(respuesta.body()).contains("openapi");
  }
}
