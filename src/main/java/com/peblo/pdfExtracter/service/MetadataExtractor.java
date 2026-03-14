package com.peblo.pdfExtracter.service;

import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class MetadataExtractor {

    public Map<String, Object> extractMetadata(String text) {

        Map<String, Object> meta = new HashMap<> ();

        Pattern gradePattern = Pattern.compile("Grade\\s+(\\d+)\\s+(\\w+)");
        Matcher gradeMatcher = gradePattern.matcher(text);

        if (gradeMatcher.find()) {
            meta.put("grade", Integer.parseInt(gradeMatcher.group(1)));
            meta.put("subject", gradeMatcher.group(2));
        }

        Pattern topicPattern = Pattern.compile("Topic:\\s*(.*)");
        Matcher topicMatcher = topicPattern.matcher(text);

        if (topicMatcher.find()) {
            meta.put("topic", topicMatcher.group(1));
        }

        return meta;
    }
}