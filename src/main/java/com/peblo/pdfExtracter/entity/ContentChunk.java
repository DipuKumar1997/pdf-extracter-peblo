package com.peblo.pdfExtracter.entity;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
public class ContentChunk {
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private String sourceId;
    @Id
    private String chunkId;
    private int grade;
    private String subject;
    private String topic;
    @Column(length = 2000)
    private String text;
}