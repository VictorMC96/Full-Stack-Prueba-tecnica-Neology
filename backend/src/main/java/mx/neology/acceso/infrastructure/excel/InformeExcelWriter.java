package mx.neology.acceso.infrastructure.excel;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UncheckedIOException;
import mx.neology.acceso.application.modelo.FilaInforme;
import mx.neology.acceso.application.modelo.InformePagos;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Component;

/** Escribe el informe de pagos como libro .xlsx con la hoja {@code Pagos}. */
@Component
public class InformeExcelWriter {

  public static final String HOJA = "Pagos";

  public byte[] escribir(InformePagos informe) {
    try (XSSFWorkbook libro = new XSSFWorkbook();
        ByteArrayOutputStream salida = new ByteArrayOutputStream()) {
      Sheet hoja = libro.createSheet(HOJA);
      Font negrita = libro.createFont();
      negrita.setBold(true);
      CellStyle estiloCabecera = libro.createCellStyle();
      estiloCabecera.setFont(negrita);
      CellStyle estiloImporte = libro.createCellStyle();
      estiloImporte.setDataFormat(libro.createDataFormat().getFormat("0.00"));
      CellStyle estiloTotal = libro.createCellStyle();
      estiloTotal.setFont(negrita);
      estiloTotal.setDataFormat(libro.createDataFormat().getFormat("0.00"));

      Row cabecera = hoja.createRow(0);
      String[] titulos = {"Placa", "Minutos", "Importe"};
      for (int i = 0; i < titulos.length; i++) {
        Cell celda = cabecera.createCell(i);
        celda.setCellValue(titulos[i]);
        celda.setCellStyle(estiloCabecera);
      }
      int numero = 1;
      for (FilaInforme fila : informe.filas()) {
        Row r = hoja.createRow(numero++);
        r.createCell(0).setCellValue(fila.placa());
        r.createCell(1).setCellValue((double) fila.minutos());
        Cell importe = r.createCell(2);
        importe.setCellValue(fila.importe().doubleValue());
        importe.setCellStyle(estiloImporte);
      }
      Row total = hoja.createRow(numero);
      Cell etiqueta = total.createCell(0);
      etiqueta.setCellValue("TOTAL");
      etiqueta.setCellStyle(estiloCabecera);
      total.createCell(1).setCellValue((double) informe.totalMinutos());
      Cell totalImporte = total.createCell(2);
      totalImporte.setCellValue(informe.totalImporte().doubleValue());
      totalImporte.setCellStyle(estiloTotal);
      for (int i = 0; i < titulos.length; i++) {
        hoja.setColumnWidth(i, 16 * 256);
      }
      libro.write(salida);
      return salida.toByteArray();
    } catch (IOException e) {
      throw new UncheckedIOException("No se pudo generar el informe Excel", e);
    }
  }
}
