package com.peblo.pdfExtracter.repository;

import com.peblo.pdfExtracter.entity.StudentAnswer;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StudentAnswerRepo extends JpaRepository<StudentAnswer,String> {
}
