package mx.neology.acceso.web;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.io.ByteArrayInputStream;
import mx.neology.acceso.soporte.IntegracionBase;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.transaction.annotation.Transactional;

@Transactional
class InformeIntegracionTest extends IntegracionBase {

  private static final String XLSX =
      "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";

  @Test
  void informeJsonDelPeriodoActivo() throws Exception {
    mvc.perform(get("/neo/residentes/pagos").accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.periodo.id").value("00000000-0000-0000-0000-000000000001"))
        .andExpect(jsonPath("$.filas.length()").value(3))
        .andExpect(jsonPath("$.filas[0].placa").value("RES001"))
        .andExpect(jsonPath("$.filas[0].minutos").value(210))
        .andExpect(jsonPath("$.filas[0].importe").value(10.50))
        .andExpect(jsonPath("$.filas[1].placa").value("RES002"))
        .andExpect(jsonPath("$.filas[2].placa").value("RES003"))
        .andExpect(jsonPath("$.totalMinutos").value(300))
        .andExpect(jsonPath("$.totalImporte").value(15.00));
  }

  @Test
  void informeExcelConCabeceraDeDescargaYContenidoLegible() throws Exception {
    byte[] bytes =
        mvc.perform(get("/neo/residentes/pagos").accept(XLSX))
            .andExpect(status().isOk())
            .andExpect(content().contentType(XLSX))
            .andExpect(
                header()
                    .string(
                        "Content-Disposition",
                        "attachment; filename=\"informe-pagos-2026-09.xlsx\""))
            .andReturn()
            .getResponse()
            .getContentAsByteArray();
    try (XSSFWorkbook libro = new XSSFWorkbook(new ByteArrayInputStream(bytes))) {
      Sheet hoja = libro.getSheet("Pagos");
      assertThat(hoja).isNotNull();
      assertThat(hoja.getRow(0).getCell(0).getStringCellValue()).isEqualTo("Placa");
      assertThat(hoja.getRow(0).getCell(1).getStringCellValue()).isEqualTo("Minutos");
      assertThat(hoja.getRow(0).getCell(2).getStringCellValue()).isEqualTo("Importe");
      assertThat(hoja.getRow(1).getCell(0).getStringCellValue()).isEqualTo("RES001");
      assertThat(hoja.getRow(2).getCell(0).getStringCellValue()).isEqualTo("RES002");
      assertThat(hoja.getRow(3).getCell(0).getStringCellValue()).isEqualTo("RES003");
      assertThat(hoja.getRow(4).getCell(0).getStringCellValue()).isEqualTo("TOTAL");
      assertThat(hoja.getRow(4).getCell(1).getCellType()).isEqualTo(CellType.NUMERIC);
      assertThat(hoja.getRow(4).getCell(1).getNumericCellValue()).isCloseTo(300, within(0.001));
      assertThat(hoja.getRow(4).getCell(2).getNumericCellValue()).isCloseTo(15.00, within(0.001));
    }
  }

  @Test
  void unResidenteSinEstanciasApareceConCeros() throws Exception {
    mvc.perform(
            post("/neo/vehiculos/residentes")
                .contentType(MediaType.APPLICATION_JSON)
                .content(cuerpo("RES000")))
        .andExpect(status().isCreated());
    mvc.perform(get("/neo/residentes/pagos").accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.filas.length()").value(4))
        .andExpect(jsonPath("$.filas[0].placa").value("RES000"))
        .andExpect(jsonPath("$.filas[0].minutos").value(0))
        .andExpect(jsonPath("$.filas[0].importe").value(0.00))
        .andExpect(jsonPath("$.totalMinutos").value(300));
  }
}
