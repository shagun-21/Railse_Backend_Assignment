package com.example.demo.controller;

import com.example.demo.dto.*;
import com.example.demo.model.enums.TaskActivity;
import com.example.demo.model.enums.TaskComment;
import com.example.demo.model.enums.response.Response;
import com.example.demo.service.TaskManagementService;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.List;

@RestController
@RequestMapping("/task-mgmt")
public class TaskManagementController {


    private final TaskManagementService taskManagementService;


    public TaskManagementController(TaskManagementService taskManagementService) {
        this.taskManagementService = taskManagementService;
    }

    @GetMapping("/tasks")
    public Response<List<TaskManagementDto>> getAllTasks(){
        return new Response<>(taskManagementService.findAllTasks());
    }


    @GetMapping("/tasks/priority/{priority}")
    public Response<List<TaskManagementDto>> getTaskByPriority(@PathVariable String priority) {
        return new Response<>(taskManagementService.findTasksByPriority(priority));
    }

    @GetMapping("/{id}")
    public Response<TaskManagementDto> getTaskById(@PathVariable Long id) {
        return new Response<>(taskManagementService.findTaskById(id));
    }




    @PostMapping("/create")
    public Response<List<TaskManagementDto>> createTasks(@RequestBody TaskCreateRequest request) {
        return new Response<>(taskManagementService.createTasks(request));
    }


    @PostMapping("/update")
    public Response<List<TaskManagementDto>> updateTasks(@RequestBody UpdateTaskRequest request) {
        return new Response<>(taskManagementService.updateTasks(request));
    }


    @PostMapping("/assign-by-ref")
    public Response<String> assignByReference(@RequestBody AssignByReferenceRequest request) {
        return new Response<>(taskManagementService.assignByReference(request));
    }


    @PostMapping("/fetch-by-date/v2")
    public Response<List<TaskManagementDto>> fetchByDate(@RequestBody TaskFetchByDateRequest request) {
        return new Response<>(taskManagementService.fetchTasksByDate(request));
    }

    @PostMapping("/tasks/priority")
    public Response<TaskManagementDto> updateTaskPriority(@RequestBody UpdateTaskPriorityRequest request){
        return new Response<>(taskManagementService.updateTaskPriority(request));
    }

    @PostMapping("tasks/{taskId}/comments")
    public void addComment(@PathVariable Long taskId, @RequestBody CommentRequest request) {
        TaskComment comment = new TaskComment(
                request.getCommentText(),
                Instant.now()
        );
        taskManagementService.addComment(taskId, comment);
    }

    @GetMapping("/tasks/{taskId}")
    public List<TaskComment> getAllComments(@PathVariable Long taskId){
        return taskManagementService.getComments(taskId);
    }

    @PostMapping("/tasks/activity/{taskId}")
    public void logActivity(@PathVariable Long taskId, @RequestBody ActivityRequest request){
        TaskActivity activity= new TaskActivity(
                request.getMessage(),
                Instant.now()
        );
        taskManagementService.logActivity(taskId,activity);
    }

    @GetMapping("/tasks/activity/{taskId}")
    public List<TaskActivity> getActivity(@PathVariable Long taskId){
        return taskManagementService.getActivity(taskId);
    }
}

