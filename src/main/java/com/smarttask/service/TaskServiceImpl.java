package com.smarttask.service;

import com.smarttask.entity.Task;
import com.smarttask.repository.TaskRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class TaskServiceImpl implements TaskService {

    private final TaskRepository taskRepository;
    private final GPTPriorityService gptPriorityService;

    @Override
    public Task createTask(Task dto) {
        // Call GPT to predict priority
        String predictedPriority = gptPriorityService.predictPriority(dto);
        dto.setStatus(predictedPriority);

        Task task = Task.builder()
                .title(dto.getTitle())
                .description(dto.getDescription())
                .status(dto.getStatus())
                .assignedTo(dto.getAssignedTo())
                .build();

        Task saved = taskRepository.save(task);

        dto.setId(saved.getId());
        return dto;
    }

    @Override
    public List<Task> getTasks(String status) {
        List<Task> tasks = (status != null)
                ? taskRepository.findByStatus(status)
                : taskRepository.findAll();

        return tasks.stream()
                .map(t -> Task.builder()
                        .id(t.getId())
                        .title(t.getTitle())
                        .description(t.getDescription())
                        .status(t.getStatus())
                        .assignedTo(t.getAssignedTo())
                        .build())
                .collect(Collectors.toList());
    }

    @Override
    public Task updateTask(String id, Task dto) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Task not found"));

        task.setTitle(dto.getTitle());
        task.setDescription(dto.getDescription());
        task.setStatus(dto.getStatus());
        task.setAssignedTo(dto.getAssignedTo());

        Task updated = taskRepository.save(task);

        dto.setId(updated.getId());
        return dto;
    }

    @Override
    public void deleteTask(String id) {
        taskRepository.deleteById(id);
    }
}
