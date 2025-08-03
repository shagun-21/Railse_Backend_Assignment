package com.example.demo.Repository;

import com.example.demo.dto.TaskManagementDto;
import com.example.demo.dto.UpdateTaskPriorityRequest;
import com.example.demo.model.enums.Priority;
import com.example.demo.model.enums.TaskActivity;
import com.example.demo.model.enums.TaskComment;
import com.example.demo.model.enums.TaskManagement;

import java.util.List;
import java.util.Optional;

public interface TaskRepository {
    Optional<TaskManagement> findById(Long id);
    TaskManagement save(TaskManagement task);
    List<TaskManagement> findAll();
    List<TaskManagement> findByReferenceIdAndReferenceType(Long referenceId, com.example.demo.model.enums.ReferenceType referenceType);
    List<TaskManagement> findByAssigneeIdIn(List<Long> assigneeIds);
    List<TaskManagement> findByPriority(Priority priority);
    TaskManagement updateTaskPriority(UpdateTaskPriorityRequest request);
    void addComment(Long taskId, TaskComment comment);
    List<TaskComment> getComments(Long taskId);
    void logActivity(Long taskId,TaskActivity activity);
    List<TaskActivity> getActivity(Long taskId);
}

