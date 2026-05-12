package ru.urfu.online_school_project_ai.controller;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.urfu.online_school_project_ai.dto.StudentDto;
import ru.urfu.online_school_project_ai.dto.StudentStatisticsDto;
import ru.urfu.online_school_project_ai.dto.TutorNotificationCreateDto;
import ru.urfu.online_school_project_ai.service.TutorService;
import ru.urfu.online_school_project_ai.service.StudentService;
import ru.urfu.online_school_project_ai.repository.UserRepository;
import ru.urfu.online_school_project_ai.entity.User;

import java.security.Principal;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/tutors")
@RequiredArgsConstructor
@SecurityRequirement(name = "JWT")
public class TutorController {

    private final TutorService tutorService;
    private final StudentService studentService;
    private final UserRepository userRepository;

    // Доступно только репетиторам (учителям) и админам
    @GetMapping("/dashboard")
    @PreAuthorize("hasAnyRole('TUTOR', 'ADMIN')")
    public ResponseEntity<String> getTutorDashboard() {
        return ResponseEntity.ok("Панель репетитора - создание и проверка заданий.");
    }

    // Получить список своих учеников (только для репетиторов)
    @GetMapping("/students")
    @PreAuthorize("hasRole('TUTOR')")
    public ResponseEntity<List<StudentDto>> getMyStudents(Principal principal) {
        List<StudentDto> students = tutorService.getStudentsForTutor(principal.getName());
        return ResponseEntity.ok(students);
    }

    @GetMapping("/students/{studentId}/dashboard")
    @PreAuthorize("hasAnyRole('TUTOR', 'ADMIN')")
    public ResponseEntity<StudentStatisticsDto> getStudentDashboardForTutor(
            @PathVariable UUID studentId) {

        // В идеале стоит еще проверить, что ученик принадлежит этому репетитору
        User studentUser = userRepository.findById(studentId)
                .orElseThrow(() -> new RuntimeException("Ученик не найден"));

        return ResponseEntity.ok(studentService.getStudentDashboard(studentUser));
    }

    @PostMapping("/notifications")
    @PreAuthorize("hasRole('TUTOR')")
    public ResponseEntity<String> createNotificationForStudents(
            Principal principal,
            @RequestBody TutorNotificationCreateDto dto) {

        tutorService.createNotificationForStudents(principal.getName(), dto);
        return ResponseEntity.ok("Уведомление успешно отправлено");
    }
}
