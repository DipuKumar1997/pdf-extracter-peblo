//package com.peblo.pdfExtracter.entity;
//
//import jakarta.persistence.*;
//import lombok.Data;
//
//import java.util.List;
//@Data
//@Entity
//public class QuizQuestion {
//    @Id
////    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    @Column(name = "question_id")
//    private String questionId;
//    private String question;
//    private String type;
//    @Column(columnDefinition = "TEXT")
//    private List<String> options;
//    private String answer;
//    private String difficulty;
//    private String sourceChunkId;
//}
package com.peblo.pdfExtracter.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

@Data
@Entity
public class QuizQuestion {

    @Id
    @Column(name = "question_id")
    private String questionId;

    private String question;

    private String type;

    @ElementCollection
    @CollectionTable(
            name = "quiz_question_options",
            joinColumns = @JoinColumn(name = "question_id")
    )
    @Column(name = "option_text")
    private List<String> options;

    private String answer;

    private String difficulty;

    private String sourceChunkId;
}