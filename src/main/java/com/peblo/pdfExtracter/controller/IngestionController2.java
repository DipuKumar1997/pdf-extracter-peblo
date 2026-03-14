package com.peblo.pdfExtracter.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.peblo.pdfExtracter.entity.ContentChunk;
import com.peblo.pdfExtracter.entity.QuizQuestion;
import com.peblo.pdfExtracter.entity.StudentAnswer;
import com.peblo.pdfExtracter.repository.ContentChunkRepository;
import com.peblo.pdfExtracter.repository.QuizQuestionRepo;
import com.peblo.pdfExtracter.repository.StudentAnswerRepo;
import com.peblo.pdfExtracter.service.MetadataExtractor;
import com.peblo.pdfExtracter.service.OllamaService;
import com.peblo.pdfExtracter.service.PdfService;
import com.peblo.pdfExtracter.service.QuestionChunker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class IngestionController2 {
    Integer studentBase=1;
    @Autowired PdfService pdfService;
    @Autowired MetadataExtractor metadataExtractor;
    @Autowired QuestionChunker chunker;
    @Autowired ContentChunkRepository repo;
    @Autowired QuizQuestionRepo quizQuestionRepo;
    @Autowired StudentAnswerRepo studentAnswerRepo;
    @Autowired
    private OllamaService ollamaService;
    @Autowired
    private ObjectMapper objectMapper;

    @PostMapping("/ingest")
    public List<ContentChunk> ingest(@RequestParam MultipartFile file) throws Exception {
        String sourceId = "SRC_001";
        String text = pdfService.extractText(file);
        text = text.replaceAll("Peblo Sample Content.*?Topic:.*?\\n", "");
        Map<String,Object> meta = metadataExtractor.extractMetadata(text);
        List<String> chunks = chunker.chunkQuestions(text);
        List<ContentChunk> result = new ArrayList<> ();
        int i = 1;
        for(String chunk : chunks){
            ContentChunk c = new ContentChunk();
            c.setSourceId(sourceId);
            c.setChunkId(sourceId+"_CH_"+i);
            c.setGrade((Integer)meta.get("grade"));
            c.setSubject((String)meta.get("subject"));
            c.setTopic((String)meta.get("topic"));
            c.setText(chunk);
            result.add(c);
            i++;
        }
        System.out.println("Total chunks: " + chunks.size());
        repo.saveAll(result);
        return result;
    }
    //@PostMapping("/generate-quiz")
    public List<QuizQuestion> generateQuiz() {
        List<ContentChunk> chunks = repo.findAll();
        List<QuizQuestion> questions = new ArrayList<>();
        int id = 1;
        for(ContentChunk chunk : chunks){
            String text = chunk.getText();
            if(text == null) continue;
            // Skip non-question chunks
            if(!(text.contains("(MCQ)") ||
                    text.contains("(True/False)") ||
                    text.contains("(Fill)"))){
                continue;
            }
            QuizQuestion q = new QuizQuestion();
            q.setQuestionId("Q" + id);
            q.setDifficulty("easy");
            q.setSourceChunkId(chunk.getChunkId());
            // ---------- MCQ ----------
            if(text.contains("(MCQ)")){
                q.setType("MCQ");
                String question = text.split("A\\.")[0];
                question = question.substring(question.indexOf(")")+1).trim();
                q.setQuestion(question);
                List<String> options = new ArrayList<>();
                for(String line : text.split("\n")){
                    if(line.startsWith("A.")) options.add(line.substring(2).trim());
                    if(line.startsWith("B.")) options.add(line.substring(2).trim());
                    if(line.startsWith("C.")) options.add(line.substring(2).trim());
                    if(line.startsWith("D.")) options.add(line.substring(2).trim());
                }
                q.setOptions(options);
                if(text.contains("Answer:")){
                    q.setAnswer(text.split("Answer:")[1].trim());
                }
            }
            // ---------- TRUE FALSE ----------
            else if(text.contains("(True/False)")){
                q.setType("TrueFalse");
                String question = text.split("Answer:")[0];
                question = question.substring(question.indexOf(")")+1).trim();
                q.setQuestion(question);
                q.setOptions(List.of("True","False"));
                if(text.contains("Answer:")){
                    q.setAnswer(text.split("Answer:")[1].trim());
                }
            }
            // ---------- FILL ----------
            else if(text.contains("(Fill)")){
                q.setType("FillBlank");
                String question = text.split("Answer:")[0];
                question = question.substring(question.indexOf(")")+1).trim();
                q.setQuestion(question);
                q.setOptions(null);
                if(text.contains("Answer:")){
                    q.setAnswer(text.split("Answer:")[1].trim());
                }
            }
            questions.add(q);
            id++;
        }
        quizQuestionRepo.saveAll(questions);
        return questions;
    }
    @GetMapping("/quiz")
    public List<QuizQuestion> getQuiz(
            @RequestParam String type,
            @RequestParam String difficulty) {
        return quizQuestionRepo.findByTypeAndDifficulty (type,difficulty);
    }

    @PostMapping("/generate-quiz-llmm")
    public List<QuizQuestion> generateQuizLLMm() {
        List<ContentChunk> chunks = repo.findAll();
        List<QuizQuestion> questions = new ArrayList<>();
        int id = 1;
        for(ContentChunk chunk : chunks){
            QuizQuestion q = ollamaService.generateQuestion(chunk.getText());
            if(q == null || q.getQuestion() == null){
                continue; // skip bad LLM responses
            }
            q.setQuestionId("Q" + id);
            q.setDifficulty("easy");
            q.setSourceChunkId(chunk.getChunkId());
            questions.add(q);
            id++;
        }
        quizQuestionRepo.saveAll(questions);
        return questions;
    }
    @PostMapping("/submit-answer")
    public String submitAnswer(@RequestBody StudentAnswer answer){
        QuizQuestion q = quizQuestionRepo.findByQuestionId((answer.getQuestionId()));
        boolean correct = q.getAnswer().equals(answer.getSelectedAnswer());
        if(correct) {
            //student login and then the id is pre assigned
            if(answer.getStudentId ()==null){
                answer.setStudentId ( "S00"+ String.valueOf (studentBase++));
            }
            studentAnswerRepo.save ( answer );
            return "Correct";
        }
        else
            return "Incorrect";
    }
    private String extractAnswer(String text){
        if(text == null) return "";
        String[] parts = text.split("Answer:");
        if(parts.length > 1){
            return parts[1].trim();
        }
        return "";
    }
}