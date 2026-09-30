package mx.neology.acceso.soporte;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.web.servlet.MockMvc;

/** Contexto compartido por las pruebas de integración (una sola arranque de Spring). */
@SpringBootTest
@AutoConfigureMockMvc
@Import(RelojDePrueba.Configuracion.class)
public abstract class IntegracionBase {

  @Autowired protected MockMvc mvc;
  @Autowired protected JdbcTemplate jdbc;
  @Autowired protected RelojDePrueba reloj;
  @PersistenceContext protected EntityManager entityManager;

  /** Vuelca los cambios pendientes de JPA para que JdbcTemplate los vea en la misma transacción. */
  protected void sincronizar() {
    entityManager.flush();
    entityManager.clear();
  }

  protected int contar(String tabla) {
    Integer n = jdbc.queryForObject("select count(*) from " + tabla, Integer.class);
    return n == null ? 0 : n;
  }

  protected static String cuerpo(String placa) {
    return "{\"placa\":\"" + placa + "\"}";
  }
}
