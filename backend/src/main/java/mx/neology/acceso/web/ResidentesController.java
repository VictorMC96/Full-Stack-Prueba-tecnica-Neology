package mx.neology.acceso.web;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.time.Clock;
import java.time.format.DateTimeFormatter;
import mx.neology.acceso.application.GenerarInformePagos;
import mx.neology.acceso.application.modelo.InformePagos;
import mx.neology.acceso.infrastructure.excel.InformeExcelWriter;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/neo/residentes")
@Tag(name = "Residentes", description = "Informe de pagos de residentes del periodo activo")
public class ResidentesController {

  public static final String XLSX =
      "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";

  private static final DateTimeFormatter MES = DateTimeFormatter.ofPattern("yyyy-MM");

  private final GenerarInformePagos generarInformePagos;
  private final InformeExcelWriter excel;
  private final Clock clock;

  public ResidentesController(
      GenerarInformePagos generarInformePagos, InformeExcelWriter excel, Clock clock) {
    this.generarInformePagos = generarInformePagos;
    this.excel = excel;
    this.clock = clock;
  }

  @GetMapping(value = "/pagos", produces = MediaType.APPLICATION_JSON_VALUE)
  @Operation(summary = "Informe de pagos del periodo activo en JSON")
  public InformePagosResponse pagosJson() {
    return Respuestas.informe(generarInformePagos.ejecutar());
  }

  @GetMapping(value = "/pagos", produces = XLSX)
  @Operation(summary = "Informe de pagos del periodo activo como archivo Excel (.xlsx)")
  public ResponseEntity<byte[]> pagosExcel() {
    InformePagos informe = generarInformePagos.ejecutar();
    String mes = informe.periodo().inicio().atZoneSameInstant(clock.getZone()).format(MES);
    return ResponseEntity.ok()
        .header(
            HttpHeaders.CONTENT_DISPOSITION,
            "attachment; filename=\"informe-pagos-" + mes + ".xlsx\"")
        .contentType(MediaType.parseMediaType(XLSX))
        .body(excel.escribir(informe));
  }
}
