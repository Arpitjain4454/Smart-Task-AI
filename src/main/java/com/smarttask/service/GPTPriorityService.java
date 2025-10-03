package com.smarttask.service;

import com.smarttask.entity.Task;

public interface GPTPriorityService {
    String predictPriority(Task task);
}
