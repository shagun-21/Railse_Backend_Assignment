package com.example.demo.service.Impl;

import com.example.demo.Repository.TaskRepository;
import com.example.demo.dto.*;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.mapper.ITaskManagementMapper;
import com.example.demo.model.enums.*;
import com.example.demo.service.TaskManagementService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class TaskManagementServiceImpl implements TaskManagementService {


    private final TaskRepository taskRepository;
    private final ITaskManagementMapper taskMapper;


    public TaskManagementServiceImpl(TaskRepository taskRepository, ITaskManagementMapper taskMapper) {
        this.taskRepository = taskRepository;
        this.taskMapper = taskMapper;
    }


    @Override
    public TaskManagementDto findTaskById(Long id) {
        TaskManagement task = taskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found with id: " + id));

        TaskManagementDto dto = taskMapper.modelToDto(task);

        List<TaskComment> comments = taskRepository.getComments(id);
        List<TaskActivity> activity = taskRepository.getActivity(id);
        comments.sort(Comparator.comparing(TaskComment::getCommentedAt));
        activity.sort(Comparator.comparing(TaskActivity::getTimestamp));

        dto.setComments(comments);
        dto.setActivityHistory(activity);

        return dto;
    }

    @Override
    public List<TaskManagementDto> findAllTasks() {
        return taskMapper.modelListToDtoList(taskRepository.findAll());
    }

    @Override
    public List<TaskManagementDto> findTasksByPriority(String priority) {
        return taskMapper.modelListToDtoList(taskRepository.findByPriority(Priority.valueOf(priority)));
    }

    @Override
    public TaskManagementDto updateTaskPriority(UpdateTaskPriorityRequest request) {
        return taskMapper.modelToDto(taskRepository.updateTaskPriority(request));
    }

    @Override
    public void addComment(Long taskId, TaskComment comment) {
        taskRepository.addComment(taskId,comment);
    }

    @Override
    public List<TaskComment> getComments(Long taskId) {
        return taskRepository.getComments(taskId);
    }

    @Override
    public void logActivity(Long taskId, TaskActivity activity) {
            taskRepository.logActivity(taskId,activity);
    }

    @Override
    public List<TaskActivity> getActivity(Long taskId) {
        return taskRepository.getActivity(taskId);
    }


    @Override
    public List<TaskManagementDto> createTasks(TaskCreateRequest createRequest) {
        List<TaskManagement> createdTasks = new ArrayList<>();
        for (TaskCreateRequest.RequestItem item : createRequest.getRequests()) {


            List<TaskManagement> existingTasks = taskRepository
                    .findByReferenceIdAndReferenceType(item.getReferenceId(), item.getReferenceType())
                    .stream()
                    .filter(t -> t.getTask() == item.getTask() &&
                            t.getStatus() != TaskStatus.COMPLETED &&
                            t.getStatus() != TaskStatus.CANCELLED)
                    .collect(Collectors.toList());

            // Cancel all existing active duplicates
            for (TaskManagement existing : existingTasks) {
                existing.setStatus(TaskStatus.CANCELLED);
                taskRepository.save(existing);
            }


            TaskManagement newTask = new TaskManagement();
            newTask.setReferenceId(item.getReferenceId());
            newTask.setReferenceType(item.getReferenceType());
            newTask.setTask(item.getTask());
            newTask.setAssigneeId(item.getAssigneeId());
            newTask.setPriority(item.getPriority());
            newTask.setTaskDeadlineTime(item.getTaskDeadlineTime());
            newTask.setStatus(TaskStatus.ASSIGNED);
            newTask.setDescription("New task created.");
            createdTasks.add(taskRepository.save(newTask));
        }
        return taskMapper.modelListToDtoList(createdTasks);
    }


    @Override
    public List<TaskManagementDto> updateTasks(UpdateTaskRequest updateRequest) {
        List<TaskManagement> updatedTasks = new ArrayList<>();
        for (UpdateTaskRequest.RequestItem item : updateRequest.getRequests()) {
            TaskManagement task = taskRepository.findById(item.getTaskId())
                    .orElseThrow(() -> new ResourceNotFoundException("Task not found with id: " + item.getTaskId()));


            if (item.getTaskStatus() != null) {
                task.setStatus(item.getTaskStatus());
            }
            if (item.getDescription() != null) {
                task.setDescription(item.getDescription());
            }
            updatedTasks.add(taskRepository.save(task));
        }
        return taskMapper.modelListToDtoList(updatedTasks);
    }


    @Override
    public String assignByReference(AssignByReferenceRequest request) {
        List<Task> applicableTasks = Task.getTasksByReferenceType(request.getReferenceType());
        List<TaskManagement> existingTasks = taskRepository.findByReferenceIdAndReferenceType(
                request.getReferenceId(), request.getReferenceType());

        for (Task taskType : applicableTasks) {
            List<TaskManagement> tasksOfType = existingTasks.stream()
                    .filter(t -> t.getTask() == taskType && t.getStatus() != TaskStatus.COMPLETED)
                    .collect(Collectors.toList());

            //Cancel all existing task of this type which are not completed
            for (TaskManagement taskToCancel : tasksOfType) {
                taskToCancel.setStatus(TaskStatus.CANCELLED);
                taskToCancel.setDescription("Reassigned");
                taskRepository.save(taskToCancel);
            }

            //new task for the new assignee
            TaskManagement newTask = new TaskManagement();
            newTask.setReferenceId(request.getReferenceId());
            newTask.setReferenceType(request.getReferenceType());
            newTask.setTask(taskType);
            newTask.setAssigneeId(request.getAssigneeId());
            newTask.setStatus(TaskStatus.ASSIGNED);
            newTask.setDescription("New task created.");
            taskRepository.save(newTask);
        }

        return "Tasks assigned successfully for reference " + request.getReferenceId();
    }



    @Override
    public List<TaskManagementDto> fetchTasksByDate(TaskFetchByDateRequest request) {
        List<TaskManagement> allTasks = taskRepository.findByAssigneeIdIn(request.getAssigneeIds());

        List<TaskManagement> filteredTasks = allTasks.stream()
                .filter(task -> {
                    TaskStatus status = task.getStatus();
                    boolean isActive = status != TaskStatus.COMPLETED && status != TaskStatus.CANCELLED;

                    long deadline = task.getTaskDeadlineTime();

                    //  tasks within the given range
                    boolean isInRange = deadline >= request.getStartDate() && deadline <= request.getEndDate();

                    // tasks before the range but still active
                    boolean isOldButStillOpen = deadline < request.getStartDate();

                    return isActive && (isInRange || isOldButStillOpen);
                })
                .collect(Collectors.toList());

        return taskMapper.modelListToDtoList(filteredTasks);
    }
}

