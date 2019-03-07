package br.com.fernandotaa.apdataconverter.utils;

import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.parser.PdfTextExtractor;

import java.io.IOException;
import java.util.Objects;

public interface PDFUtils {

    static String LerPDF(String file) {
        PdfReader reader = null;
        try {
            reader = new PdfReader(file);
            return PdfTextExtractor.getTextFromPage(reader, 1);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        } finally {
            if (Objects.nonNull(reader)) {
                reader.close();
            }
        }
    }

}
