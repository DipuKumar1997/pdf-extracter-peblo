package com.peblo.pdfExtracter.repository;

import com.peblo.pdfExtracter.entity.QuizQuestion;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
public interface QuizQuestionRepo extends JpaRepository<QuizQuestion,Long> {
    List<QuizQuestion> findByTypeAndDifficulty(String type, String difficulty);
    QuizQuestion findByQuestionId(String questionId);
}