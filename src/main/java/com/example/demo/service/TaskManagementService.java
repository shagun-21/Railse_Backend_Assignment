package com.example.demo.service;

import com.example.demo.dto.*;
import com.example.demo.model.enums.TaskActivity;
import com.example.demo.model.enums.TaskComment;

import java.util.List;

public interface TaskManagementService {
    List<TaskManagementDto> createTasks(TaskCreateRequest request);
    List<TaskManagementDto> updateTasks(UpdateTaskRequest request);
    String assignByReference(AssignByReferenceRequest request);
    List<TaskManagementDto> fetchTasksByDate(TaskFetchByDateRequest request);
    TaskManagementDto findTaskById(Long id);

    List<TaskManagementDto> findAllTasks();

    List<TaskManagementDto> findTasksByPriority(String priority);

    TaskManagementDto updateTaskPriority(UpdateTaskPriorityRequest request);
    void addComment(Long taskId, TaskComment comment);
    List<TaskComment> getComments(Long taskId);
    void logActivity(Long taskId, TaskActivity activity);
    List<TaskActivity> getActivity(Long taskId);
}

