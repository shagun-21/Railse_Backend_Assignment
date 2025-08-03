package com.example.demo.Repository;

import com.example.demo.dto.TaskManagementDto;
import com.example.demo.dto.UpdateTaskPriorityRequest;
import com.example.demo.model.enums.*;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

@Repository
public class InMemoryTaskRepository implements TaskRepository {


    private final Map<Long, TaskManagement> taskStore = new ConcurrentHashMap<>();
    private final Map<Long, List<TaskComment>> commentsStore = new ConcurrentHashMap<>();
    private final Map<Long, List<TaskActivity>> activityStore = new ConcurrentHashMap<>();
    private final AtomicLong idCounter = new AtomicLong(0);


    public InMemoryTaskRepository() {
        // Seed data
        createSeedTask(101L, ReferenceType.ORDER, Task.CREATE_INVOICE, 1L, TaskStatus.ASSIGNED, Priority.HIGH);
        createSeedTask(101L, ReferenceType.ORDER, Task.ARRANGE_PICKUP, 1L, TaskStatus.COMPLETED, Priority.HIGH);
        createSeedTask(102L, ReferenceType.ORDER, Task.CREATE_INVOICE, 2L, TaskStatus.ASSIGNED, Priority.MEDIUM);
        createSeedTask(201L, ReferenceType.ENTITY, Task.ASSIGN_CUSTOMER_TO_SALES_PERSON, 2L, TaskStatus.ASSIGNED, Priority.LOW);
        createSeedTask(201L, ReferenceType.ENTITY, Task.ASSIGN_CUSTOMER_TO_SALES_PERSON, 3L, TaskStatus.ASSIGNED, Priority.LOW); // Duplicate for Bug #1
        createSeedTask(103L, ReferenceType.ORDER, Task.COLLECT_PAYMENT, 1L, TaskStatus.CANCELLED, Priority.MEDIUM); // For Bug #2
    }


    private void createSeedTask(Long refId, ReferenceType refType, Task task, Long assigneeId, TaskStatus status, Priority priority) {
        long newId = idCounter.incrementAndGet();
        TaskManagement newTask = new TaskManagement();
        newTask.setId(newId);
        newTask.setReferenceId(refId);
        newTask.setReferenceType(refType);
        newTask.setTask(task);
        newTask.setAssigneeId(assigneeId);
        newTask.setStatus(status);
        newTask.setPriority(priority);
        newTask.setDescription("This is a seed task.");
        newTask.setTaskDeadlineTime(System.currentTimeMillis() + 86400000); // 1 day from now
        taskStore.put(newId, newTask);
    }


    @Override
    public Optional<TaskManagement> findById(Long id) {
        return Optional.ofNullable(taskStore.get(id));
    }


    @Override
    public TaskManagement save(TaskManagement task) {
        if (task.getId() == null) {
            task.setId(idCounter.incrementAndGet());
        }
        taskStore.put(task.getId(), task);
        return task;
    }


    @Override
    public List<TaskManagement> findAll() {
        return List.copyOf(taskStore.values());
    }


    @Override
    public List<TaskManagement> findByReferenceIdAndReferenceType(Long referenceId, ReferenceType referenceType) {
        return taskStore.values().stream()
                .filter(task -> task.getReferenceId().equals(referenceId) && task.getReferenceType().equals(referenceType))
                .collect(Collectors.toList());
    }


    @Override
    public List<TaskManagement> findByAssigneeIdIn(List<Long> assigneeIds) {
        return taskStore.values().stream()
                .filter(task -> assigneeIds.contains(task.getAssigneeId()))
                .collect(Collectors.toList());
    }

    @Override
    public List<TaskManagement> findByPriority(Priority priority) {
        return taskStore.values().stream()
                .filter(task -> task.getPriority() == priority)
                .collect(Collectors.toList());
    }

    @Override
    public TaskManagement updateTaskPriority(UpdateTaskPriorityRequest request) {
        TaskManagement task=taskStore.get(request.getTaskId());
        System.out.println(task.toString());

        if(task!=null){
            task.setPriority(request.getPriority());
        }
        task.setPriority(request.getPriority());
        

        //not required but just for safety
        taskStore.put(task.getId(), task);

        return task;
    }

    @Override
    public void addComment(Long taskId, TaskComment comment) {
        comment.setCommentedAt(Instant.now());
        commentsStore.computeIfAbsent(taskId, k -> new java.util.ArrayList<>()).add(comment);
    }

    @Override
    public List<TaskComment> getComments(Long taskId) {
        return commentsStore.getOrDefault(taskId, List.of())
                .stream()
                .sorted((a, b) -> a.getCommentedAt().compareTo(b.getCommentedAt()))
                .collect(Collectors.toList());
    }

    @Override
    public void logActivity(Long taskId, TaskActivity activity) {
        activity.setTimestamp(Instant.now());
        activityStore.computeIfAbsent(taskId, k -> new ArrayList<>()).add(activity);
    }

    @Override
    public List<TaskActivity> getActivity(Long taskId) {
        return activityStore.getOrDefault(taskId, List.of()).stream()
                .sorted((a1, a2) -> a1.getTimestamp().compareTo(a2.getTimestamp()))
                .collect(Collectors.toList());
    }

}

