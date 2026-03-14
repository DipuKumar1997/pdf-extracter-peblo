package com.peblo.pdfExtracter.controller;

import com.peblo.pdfExtracter.entity.ContentChunk;
import com.peblo.pdfExtracter.repository.ContentChunkRepository;
import com.peblo.pdfExtracter.service.ContentBuilder;
import com.peblo.pdfExtracter.service.MetadataExtractor;
import com.peblo.pdfExtracter.service.PdfService;
import com.peblo.pdfExtracter.service.QuestionChunker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;
@Profile ( "volatile" )
@RestController
@RequestMapping("/content")
public class IngestionController {

    @Autowired
    PdfService pdfService;
    @Autowired
    MetadataExtractor metadataExtractor;
    @Autowired
    QuestionChunker chunker;
    @Autowired
    ContentBuilder builder;
    @Autowired
    ContentChunkRepository contentChunkRepository;
    @PostMapping("/upload")
    public List<ContentChunk> ingest(@RequestParam MultipartFile file) throws Exception {
        String sourceId = "SRC_001";
        String raw = pdfService.extractText(file);
        Map<String, Object> meta = metadataExtractor.extractMetadata(raw);
        List<String> chunks = chunker.chunkQuestions(raw);
        List<ContentChunk> builtChunks = builder.build(
                sourceId,
                (int) meta.get("grade"),
                (String) meta.get("subject"),
                (String) meta.get("topic"),
                chunks
        );
        contentChunkRepository.saveAll(builtChunks);
        return builtChunks;
    }
}