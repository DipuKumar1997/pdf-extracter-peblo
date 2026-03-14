package com.peblo.pdfExtracter.service;

import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class QuestionChunker {

    public List<String> chunkQuestions(String text) {
        List<String> chunks = new ArrayList<>();
        String[] parts = text.split("(?=\\d+\\.\\s)");
        for (String part : parts) {
            String cleaned = part.trim();
            if (!cleaned.isEmpty()) {
                chunks.add(cleaned);
            }
        }
        return chunks;
    }
}