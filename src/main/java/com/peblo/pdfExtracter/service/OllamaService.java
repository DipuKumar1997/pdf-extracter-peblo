package com.peblo.pdfExtracter.service;

import com.peblo.pdfExtracter.entity.QuizQuestion;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@Service
public class OllamaService {
    private final RestTemplate restTemplate = new RestTemplate();
    public QuizQuestion generateQuestion(String text) {

    String prompt = """
            You are a quiz generator. Create ONE multiple choice question about the content below. 
            CONTENT: """ + text + """
            Respond using EXACTLY this format, nothing else:
            QUESTION: your question
            TYPE: MCQ
            OPTIONS:
            A) option
            B) option
            C) option
            D) option
            ANSWER: X
            Replace X with the single correct letter (A, B, C, or D). Always include the ANSWER line.
            """;

    Map<String, Object> body = new HashMap<>();
    body.put("model", "llama3.2:3b");
    body.put("prompt", prompt);
    body.put("stream", false);
    body.put("options", Map.of(
            "num_predict", 300,      // enough tokens to always reach ANSWER line
            "temperature", 0.3       // lower = more consistent formatting
    ));

    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);

    ResponseEntity<Map> response = restTemplate.postForEntity(
            "http://localhost:11434/api/generate",
            new HttpEntity<>(body, headers),
            Map.class
    );

    System.out.println(response);
    if (response.getBody() == null) return null;

    String llmOutput = response.getBody().get("response").toString();
    System.out.println("=== LLM RAW OUTPUT ===\n" + llmOutput + "\n======================");

    return parseLLMResponse(llmOutput);
}

    private QuizQuestion parseLLMResponse(String response) {
        QuizQuestion q = new QuizQuestion();
        List<String> options = new ArrayList<>();
        String[] lines = response.split("\\n");
        for (String line : lines) {
            line = line.trim();
            if (line.toUpperCase().startsWith("QUESTION:")) {
                q.setQuestion(line.substring(line.indexOf(":") + 1).trim());
            }
            else if (line.toUpperCase().startsWith("TYPE:")) {
                q.setType(line.substring(line.indexOf(":") + 1).trim());
            }
            else if (line.matches("^[A-Da-d][).].*")) {
                options.add(line.substring(2).trim());
            }
            else if (line.toUpperCase().startsWith("ANSWER:")) {
                String ans = line.substring(line.indexOf(":") + 1).trim();

                if (ans.contains("|")) {
                    ans = ans.split("\\|")[0].trim();
                }
                ans = ans.replaceAll("[().]", "").trim();

                if (!ans.isEmpty()) {
                    char first = Character.toUpperCase(ans.charAt(0));
                    if (first >= 'A' && first <= 'D') {
                        ans = String.valueOf(first);
                    }
                }
                q.setAnswer(ans);
            }
        }

        // Fallback defaults
        if (q.getType() == null || q.getType().isEmpty()) {
            q.setType("MCQ");
        }

        //  if answer is still null, ask the model again just for the answer
        if (q.getAnswer() == null && q.getQuestion() != null && options.size() == 4) {
            q.setAnswer(inferAnswer(q.getQuestion(), options));
        }

        q.setOptions(options);
        return q;
    }

    // Called only when ANSWER line was missing from first response
    private String inferAnswer(String question, List<String> options) {
        String prompt = "Question: " + question + "\n"
                + "A) " + options.get(0) + "\n"
                + "B) " + options.get(1) + "\n"
                + "C) " + options.get(2) + "\n"
                + "D) " + options.get(3) + "\n"
                + "Which option is correct? Reply with a single letter only: A, B, C, or D";

        Map<String, Object> body = new HashMap<>();
        body.put("model", "llama3.2:3b");
        body.put("prompt", prompt);
        body.put("stream", false);
        body.put("options", Map.of("num_predict", 5, "temperature", 0.0));

        try {
            ResponseEntity<Map> response = restTemplate.postForEntity(
                    "http://localhost:11434/api/generate",
                    new HttpEntity<>(body, new HttpHeaders() {{ setContentType(MediaType.APPLICATION_JSON); }}),
                    Map.class
            );
            if (response.getBody() == null) return null;

            String ans = response.getBody().get("response").toString().trim();
            ans = ans.replaceAll("[^A-Da-d]", "").toUpperCase();
            return ans.isEmpty() ? null : String.valueOf(ans.charAt(0));

        } catch (Exception e) {
            System.out.println("inferAnswer failed: " + e.getMessage());
            return null;
        }
    }
}