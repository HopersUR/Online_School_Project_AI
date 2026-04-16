package ru.urfu.online_school_project_ai.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.urfu.online_school_project_ai.dto.StudentDto;
import ru.urfu.online_school_project_ai.entity.Tutor;
import ru.urfu.online_school_project_ai.entity.User;
import ru.urfu.online_school_project_ai.repository.TutorRepository;
import ru.urfu.online_school_project_ai.repository.UserRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TutorService {

    private final UserRepository userRepository;
    private final TutorRepository tutorRepository;

    @Transactional(readOnly = true)
    public List<StudentDto> getStudentsForTutor(String email) {
        User user = userRepository.findByEmail(email);
        if (user == null) {
            throw new IllegalArgumentException("Пользователь не найден");
        }

        Tutor tutor = tutorRepository.findById(user.getId())
                .orElseThrow(() -> new IllegalArgumentException("Профиль репетитора не найден"));

        if (tutor.getStudents() == null) {
            return List.of();
        }

        return tutor.getStudents().stream()
                .map(s -> new StudentDto(
                        s.getId(),
                        s.getName(),
                        s.getAvatar(),
                        s.getLevel(),
                        s.getDate()
                ))
                .collect(Collectors.toList());
    }
}
