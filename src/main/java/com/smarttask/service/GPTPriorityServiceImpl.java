package com.smarttask.service;

import com.smarttask.entity.Task;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class GPTPriorityServiceImpl implements GPTPriorityService {

    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${openai.api.key}")
    private String openAiKey;

    @Override
    public String predictPriority(Task task) {
        String prompt = "Given this task, predict the priority as HIGH, MEDIUM, or LOW.\n"
                + "Title: " + task.getTitle() + "\n"
                + "Description: " + task.getDescription();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(openAiKey);

        Map<String, Object> requestBody = Map.of(
                "model", "gpt-3.5-turbo",
                "messages", List.of(Map.of("role", "user", "content", prompt)),
                "temperature", 0
        );

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

        Map<String, Object> response = restTemplate.postForObject(
                "https://api.openai.com/v1/chat/completions",
                entity,
                Map.class
        );

        List<Map<String,Object>> choices = (List<Map<String,Object>>) response.get("choices");
        String text = (String)((Map<String,Object>)choices.get(0).get("message")).get("content");

        return text.trim().toUpperCase(); // HIGH, MEDIUM, LOW
    }
}
