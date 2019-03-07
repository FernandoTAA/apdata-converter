package br.com.fernandotaa.apdataconverter;

import br.com.fernandotaa.apdataconverter.dto.Dia;
import br.com.fernandotaa.apdataconverter.utils.PDFUtils;
import br.com.fernandotaa.apdataconverter.utils.PlanilhaUtils;

import java.io.File;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Converter {

    private static String PASTA_PDF_PONTOS= "/home/fernandotaa/ponto/";
    private static String ARQUIVO_XLSX_PONTOS = "/home/fernandotaa/ponto/ponto.xlsx";

    public static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    public static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");
    public static final List<String> LIST_WEEK_DAYS = Arrays.asList("Dom", "Seg", "Ter", "Qua", "Qui", "Sex", "Sab");

    public static void main(String[] args) {
        File file = new File(PASTA_PDF_PONTOS);
        if (!file.exists() || !file.isDirectory()) {
            return;
        }

        Map<LocalDate, Dia> mapLinhaPorData = Arrays.stream(file.listFiles()).
                map(File::toString).
                filter(f -> f.toUpperCase().endsWith("PDF")).
                map(PDFUtils::LerPDF).
                flatMap(Converter::dividirLinhasPorDias).
                collect(Collectors.toMap(Converter::extrairData, Converter::extractPeriods));

        mapLinhaPorData = new TreeMap<>(mapLinhaPorData);

        PlanilhaUtils.gerarPlanilha(ARQUIVO_XLSX_PONTOS, mapLinhaPorData);
    }

    private static Stream<String> dividirLinhasPorDias(String fileContent) {
        String lastLine = null;
        Set<String> lines = new HashSet<>();
        for (String line : fileContent.lines().collect(Collectors.toList())) {
            if (isValidLine(line)) {
                lines.add(line + "\n" + lastLine);
            }
            lastLine = line;
        }
        return lines.stream();
    }

    private static Dia extractPeriods(String line) {
        String[] pieces = line.split("\n")[0].split(" ");
        List<LocalTime> listPeriods = Arrays.stream(pieces).filter(Converter::isValidTime).map(Converter::extractTime).collect(Collectors.toList());
        Dia day = new Dia();
        day.setPontos(listPeriods);
        day.setFeriado(line.contains("Feriado"));
        day.setAbonado(line.contains("Abonado"));
        day.setAtestado(line.contains("Atestado"));
        return day;
    }

    private static LocalTime extractTime(String piece) {
        try {
            return LocalTime.parse(piece, TIME_FORMATTER);
        } catch (Exception e) {
            return null;
        }
    }

    private static boolean isValidTime(String piece) {
        return Objects.nonNull(extractTime(piece));
    }

    private static LocalDate extrairData(String line) {
        String[] pieces = line.split(" ");
        try {
            return LocalDate.parse(pieces[0], DATE_FORMATTER);
        } catch (Exception e) {
            return null;
        }
    }

    private static boolean isValidLine(String line) {
        String[] pieces = line.split(" ");
        return Objects.nonNull(extrairData(line)) && pieces.length > 2 && LIST_WEEK_DAYS.contains(pieces[2]);
    }
}