package com.peblo.pdfExtracter.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Data;

@Data
@Entity
public class StudentAnswer {
    @Id
    private String studentId;
    private String questionId;
    private String selectedAnswer;
}