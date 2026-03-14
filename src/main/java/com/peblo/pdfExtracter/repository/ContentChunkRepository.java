package com.peblo.pdfExtracter.repository;

import com.peblo.pdfExtracter.entity.ContentChunk;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ContentChunkRepository extends JpaRepository<ContentChunk,Long> {
}
