package com.resumematch.service;

import com.resumematch.exception.DocumentExtractionException;
import com.resumematch.exception.DocumentSecurityException;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class PdfExtractionService {

    public String extractText (byte[] pdfBytes){

        if(pdfBytes == null || pdfBytes.length == 0){
            throw new DocumentExtractionException("pdfBytes is null or empty");
        }

        try (PDDocument document = Loader.loadPDF(pdfBytes)) {

            if (document.isEncrypted()) {
                throw new DocumentSecurityException("document is encrypted");
            }

            String extractedText = new PDFTextStripper().getText(document);

            if(extractedText == null || extractedText.isBlank()){
                throw new DocumentExtractionException("document is empty or unreadable/ scanned PDFs require OCR");
            }

            return extractedText.trim();

        } catch (IOException | IllegalArgumentException e) {
            throw new DocumentExtractionException("failed to extract text from PDF");
        }
    }
}
