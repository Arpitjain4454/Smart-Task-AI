package com.smarttask.controller;

import com.smarttask.entity.Task;
import com.smarttask.repository.TaskRepository;
import com.smarttask.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;


@RestController
@RequestMapping("/tasks")
@RequiredArgsConstructor
public class TaskController {

    private final TaskRepository taskRepository;
    private final JwtUtil jwtUtil;

    @PostMapping
    public ResponseEntity<?> createTask(@RequestBody Task task,
                                        @CookieValue(value = "jwt") String token) {
        String creator = jwtUtil.extractUsername(token);
        task.setCreatedBy(creator);
        task.setStatus("PENDING");
        return ResponseEntity.ok(taskRepository.save(task));
    }

    @GetMapping
    public ResponseEntity<?> getTasks(@CookieValue(value = "jwt") String token) {
        String user = jwtUtil.extractUsername(token);
        return ResponseEntity.ok(taskRepository.findByAssignedToOrCreatedBy(user, user));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateTask(@PathVariable Long id, @RequestBody Task task) {
        Task existing = taskRepository.findById(String.valueOf(id)).orElseThrow();
        existing.setStatus(task.getStatus());
        existing.setTitle(task.getTitle());
        existing.setDescription(task.getDescription());
        return ResponseEntity.ok(taskRepository.save(existing));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteTask(@PathVariable Long id) {
        taskRepository.deleteById(String.valueOf(id));
        return ResponseEntity.ok(Map.of("message", "Task deleted"));
    }
}

