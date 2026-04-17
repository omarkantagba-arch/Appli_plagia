package com.plagicheck.service;

import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Service
public class TextExtractionService {

    public String extractText(MultipartFile file) throws IOException {
        String contentType = file.getContentType();
        
        if ("application/pdf".equals(contentType)) {
            return extractFromPdf(file);
        } else if ("application/vnd.openxmlformats-officedocument.wordprocessingml.document".equals(contentType)) {
            return extractFromDocx(file);
        }
        
        throw new RuntimeException("Unsupported file type: " + contentType);
    }

    private String extractFromPdf(MultipartFile file) throws IOException {
        try (PDDocument document = Loader.loadPDF(file.getBytes())) {
            PDFTextStripper stripper = new PDFTextStripper();
            return stripper.getText(document);
        }
    }

    private String extractFromDocx(MultipartFile file) throws IOException {
        try (XWPFDocument document = new XWPFDocument(file.getInputStream())) {
            List<XWPFParagraph> paragraphs = document.getParagraphs();
            StringBuilder text = new StringBuilder();
            
            for (XWPFParagraph paragraph : paragraphs) {
                text.append(paragraph.getText()).append("\n");
            }
            
            return text.toString();
        }
    }
}
