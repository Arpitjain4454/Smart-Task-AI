package com.smarttask.service;
import com.smarttask.entity.Task;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public interface TaskService {
    Task createTask(Task dto);
    List<Task> getTasks(String status);
    Task updateTask(String id, Task dto);
    void deleteTask(String id);
}
