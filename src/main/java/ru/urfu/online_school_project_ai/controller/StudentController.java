package ru.urfu.online_school_project_ai.controller;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.urfu.online_school_project_ai.dto.StudentStatisticsDto;
import ru.urfu.online_school_project_ai.dto.TutorProfileResponseDto;
import ru.urfu.online_school_project_ai.entity.User;
import ru.urfu.online_school_project_ai.repository.UserRepository;
import ru.urfu.online_school_project_ai.service.StudentService;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import java.util.List;

@RestController
@RequestMapping("/api/students")
@RequiredArgsConstructor
@SecurityRequirement(name = "JWT")
public class StudentController {

    private final UserRepository userRepository;
    private final StudentService studentService;

    // Доступно только студентам
    @GetMapping("/dashboard")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<StudentStatisticsDto> getStudentDashboard() {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User currentUser = userRepository.findByEmail(userDetails.getUsername());

        if (currentUser == null) {
            throw new RuntimeException("Пользователь не найден");
        }

        return ResponseEntity.ok(studentService.getStudentDashboard(currentUser));
    }

    @GetMapping("/tutors")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<List<TutorProfileResponseDto>> getMyTutors() {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User currentUser = userRepository.findByEmail(userDetails.getUsername());

        if (currentUser == null) {
            throw new RuntimeException("Пользователь не найден");
        }

        return ResponseEntity.ok(studentService.getMyTutors(currentUser));
    }
}
