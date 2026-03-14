package com.peblo.pdfExtracter.service;

import com.peblo.pdfExtracter.entity.ContentChunk;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ContentBuilder {

    public List<ContentChunk> build(
            String sourceId,
            int grade,
            String subject,
            String topic,
            List<String> chunks) {

        List<ContentChunk> list = new ArrayList<> ();
        int index = 1;

        for (String chunk : chunks) {

            ContentChunk c = new ContentChunk();

            c.setSourceId(sourceId);
            c.setChunkId(sourceId + "_CH_" + index++);

            c.setGrade(grade);
            c.setSubject(subject);
            c.setTopic(topic);

            c.setText(chunk);

            list.add(c);
        }

        return list;
    }
}