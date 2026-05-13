package ru.urfu.online_school_project_ai.controller;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.http.MediaType;
import ru.urfu.online_school_project_ai.dto.ChangeRoleDto;
import ru.urfu.online_school_project_ai.dto.TaskCreateDto;
import ru.urfu.online_school_project_ai.dto.AdminDashboardDto;
import ru.urfu.online_school_project_ai.dto.AdminNotificationCreateDto;
import ru.urfu.online_school_project_ai.entity.Notification;
import ru.urfu.online_school_project_ai.entity.Task;
import ru.urfu.online_school_project_ai.service.AdminService;
import ru.urfu.online_school_project_ai.service.NotificationService;

import java.util.UUID;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@SecurityRequirement(name = "JWT")
public class AdminController {

    private final AdminService adminService;
    private final NotificationService notificationService;

    //test git
    // Доступно только администраторам
    @GetMapping("/dashboard")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<AdminDashboardDto> getAdminDashboard() {
        return ResponseEntity.ok(adminService.getDashboardStatistics());
    }

    @PostMapping("/notifications")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Notification> createNotification(@RequestBody AdminNotificationCreateDto dto) {
        return ResponseEntity.ok(notificationService.createAdminNotification(dto));
    }

    // Смена роли пользователя админом
    @PutMapping("/users/{userId}/role")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> changeUserRole(
            @PathVariable UUID userId,
            @RequestBody @Valid ChangeRoleDto dto) {
        adminService.changeUserRole(userId, dto.role());
        return ResponseEntity.ok("Роль пользователя успешно изменена на " + dto.role());
    }

    // Назначение репетитора ученику
    @PostMapping("/users/student/{studentId}/tutor/{tutorId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> assignTutorToStudent(
            @PathVariable UUID studentId,
            @PathVariable UUID tutorId) {
        adminService.assignTutorToStudent(studentId, tutorId);
        return ResponseEntity.ok("Репетитор успешно назначен ученику");
    }

    // Создание новой задачи админом
    @PostMapping("/tasks")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Task> addTask(@RequestBody @Valid TaskCreateDto dto) {
        Task createdTask = adminService.addTask(dto);
        return ResponseEntity.ok(createdTask);
    }

    // Редактирование задачи админом
    @PutMapping("/tasks/{taskId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Task> updateTask(
            @PathVariable Long taskId,
            @RequestBody @Valid ru.urfu.online_school_project_ai.dto.TaskUpdateDto dto) {
        Task updatedTask = adminService.updateTask(taskId, dto);
        return ResponseEntity.ok(updatedTask);
    }

    // Удаление задачи админом
    @DeleteMapping("/tasks/{taskId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> deleteTask(@PathVariable Long taskId) {
        adminService.deleteTask(taskId);
        return ResponseEntity.ok("Задача с ID " + taskId + " успешно удалена");
    }

    // Загрузка файла для задачи админом
    @PostMapping(value = "/tasks/{taskId}/file", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> uploadTaskFile(
            @PathVariable Long taskId,
            @RequestParam("file") MultipartFile file) {
        adminService.uploadTaskFile(taskId, file);
        return ResponseEntity.ok("Файл успешно загружен");
    }
}
