package mx.neology.acceso.infrastructure.excel;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;
import mx.neology.acceso.application.modelo.FilaInforme;
import mx.neology.acceso.application.modelo.InformePagos;
import mx.neology.acceso.domain.Periodo;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.jupiter.api.Test;

class InformeExcelWriterTest {

  @Test
  void escribeCabecerasFilasYTotalComoCeldasNumericas() throws IOException {
    OffsetDateTime inicio = OffsetDateTime.parse("2026-09-01T00:00:00-06:00");
    Periodo periodo = new Periodo(UUID.randomUUID(), inicio, null, Boolean.TRUE, inicio, inicio);
    InformePagos informe =
        new InformePagos(
            periodo,
            List.of(
                new FilaInforme("RES001", 210, new BigDecimal("10.50")),
                new FilaInforme("RES002", 0, new BigDecimal("0.00"))),
            210,
            new BigDecimal("10.50"));

    byte[] bytes = new InformeExcelWriter().escribir(informe);

    try (XSSFWorkbook libro = new XSSFWorkbook(new ByteArrayInputStream(bytes))) {
      Sheet hoja = libro.getSheet("Pagos");
      assertThat(hoja).isNotNull();
      assertThat(hoja.getRow(0).getCell(0).getStringCellValue()).isEqualTo("Placa");
      assertThat(hoja.getRow(0).getCell(1).getStringCellValue()).isEqualTo("Minutos");
      assertThat(hoja.getRow(0).getCell(2).getStringCellValue()).isEqualTo("Importe");
      assertThat(libro.getFontAt(hoja.getRow(0).getCell(0).getCellStyle().getFontIndex()).getBold())
          .isTrue();
      assertThat(hoja.getRow(1).getCell(0).getStringCellValue()).isEqualTo("RES001");
      assertThat(hoja.getRow(1).getCell(1).getCellType()).isEqualTo(CellType.NUMERIC);
      assertThat(hoja.getRow(1).getCell(1).getNumericCellValue()).isCloseTo(210, within(0.001));
      assertThat(hoja.getRow(1).getCell(2).getNumericCellValue()).isCloseTo(10.5, within(0.001));
      assertThat(hoja.getRow(1).getCell(2).getCellStyle().getDataFormatString()).isEqualTo("0.00");
      assertThat(hoja.getRow(3).getCell(0).getStringCellValue()).isEqualTo("TOTAL");
      assertThat(hoja.getRow(3).getCell(1).getNumericCellValue()).isCloseTo(210, within(0.001));
      assertThat(hoja.getRow(3).getCell(2).getNumericCellValue()).isCloseTo(10.5, within(0.001));
      assertThat(hoja.getLastRowNum()).isEqualTo(3);
    }
  }
}
