package br.com.fernandotaa.apdataconverter.utils;

import br.com.fernandotaa.apdataconverter.dto.Dia;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public final class PlanilhaUtils {

    private PlanilhaUtils() {
    }

    public static void gerarPlanilha(String filePath, Map<LocalDate, Dia> mapLineByDate) {
        Integer quantidadeMaximaPOntos = mapLineByDate.entrySet().stream().
                map(Map.Entry::getValue).
                map(Dia::getPontos).
                mapToInt(List::size).
                max().
                orElse(0);

        XSSFWorkbook workbook = new XSSFWorkbook();
        XSSFSheet sheet = workbook.createSheet("Pontos");

        CreationHelper createHelper = workbook.getCreationHelper();
        CellStyle cellStyleDate = workbook.createCellStyle();
        cellStyleDate.setDataFormat(createHelper.createDataFormat().getFormat("dd/mm/yy"));

        CellStyle cellStyleTime = workbook.createCellStyle();
        cellStyleTime.setDataFormat(createHelper.createDataFormat().getFormat("hh:mm"));

        CellStyle cellStyleBoolean = workbook.createCellStyle();
        cellStyleBoolean.setDataFormat(createHelper.createDataFormat().getFormat("BOOLEAN"));

        AtomicInteger rowIndex = new AtomicInteger();
        addRowHeader(quantidadeMaximaPOntos, sheet, rowIndex);
        mapLineByDate.entrySet().forEach(entry -> addRow(entry, rowIndex, sheet, cellStyleDate, cellStyleTime, cellStyleBoolean));

        try {
            FileOutputStream outputStream = new FileOutputStream(filePath);
            workbook.write(outputStream);
            workbook.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void addRowHeader(Integer quantidadeMaximaPOntos, XSSFSheet sheet, AtomicInteger rowIndex) {
        Integer colNum = 0;
        Row row = sheet.createRow(rowIndex.getAndIncrement());

        row.createCell(colNum++).setCellValue("Data");
        row.createCell(colNum++).setCellValue("Feriado");
        row.createCell(colNum++).setCellValue("Abonado");
        row.createCell(colNum++).setCellValue("Atestado");

        for (int i = 0; i < quantidadeMaximaPOntos; i++) {
            row.createCell(colNum++).setCellValue(i % 2 == 0 ? "Entrada" : "Saída");
        }
    }

    private static void addRow(Map.Entry<LocalDate, Dia> entry, AtomicInteger rowIndex, XSSFSheet sheet, CellStyle cellStyleDate, CellStyle cellStyleTime, CellStyle cellStyleBoolean) {
        Row row = sheet.createRow(rowIndex.getAndIncrement());
        Integer colNum = 0;

        Cell cellDate = row.createCell(colNum++);
        cellDate.setCellType(CellType.NUMERIC);
        cellDate.setCellStyle(cellStyleDate);
        cellDate.setCellValue(Date.from(entry.getKey().atStartOfDay(ZoneId.systemDefault()).toInstant()));

        Cell cellFeriado = row.createCell(colNum++);
        cellFeriado.setCellType(CellType.BOOLEAN);
        cellFeriado.setCellStyle(cellStyleBoolean);
        cellFeriado.setCellValue(entry.getValue().isFeriado());

        Cell cellAbonado = row.createCell(colNum++);
        cellAbonado.setCellType(CellType.BOOLEAN);
        cellAbonado.setCellStyle(cellStyleBoolean);
        cellAbonado.setCellValue(entry.getValue().isAbonado());

        Cell cellAtestado = row.createCell(colNum++);
        cellAtestado.setCellType(CellType.BOOLEAN);
        cellAtestado.setCellStyle(cellStyleBoolean);
        cellAtestado.setCellValue(entry.getValue().isAtestado());

        for (LocalTime time : entry.getValue().getPontos()) {
            String cellFormula = String.format("TIME(%d,%d,%d)", time.getHour(), time.getMinute(), time.getSecond());

            Cell cellTime = row.createCell(colNum++);
            cellTime.setCellStyle(cellStyleTime);
            cellTime.setCellFormula(cellFormula);
            cellTime.setCellType(CellType.FORMULA);
        }
    }
}
