package ru.urfu.online_school_project_ai.controller;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.urfu.online_school_project_ai.dto.StudentDto;
import ru.urfu.online_school_project_ai.service.TutorService;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/tutors")
@RequiredArgsConstructor
@SecurityRequirement(name = "JWT")
public class TutorController {

    private final TutorService tutorService;

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
}
