package ru.urfu.online_school_project_ai.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.urfu.online_school_project_ai.dto.LessonCreateDto;
import ru.urfu.online_school_project_ai.dto.LessonResponseDto;
import ru.urfu.online_school_project_ai.entity.Lesson;
import ru.urfu.online_school_project_ai.entity.Student;
import ru.urfu.online_school_project_ai.entity.Tutor;
import ru.urfu.online_school_project_ai.entity.User;
import ru.urfu.online_school_project_ai.repository.LessonRepository;
import ru.urfu.online_school_project_ai.repository.StudentRepository;
import ru.urfu.online_school_project_ai.repository.TutorRepository;
import ru.urfu.online_school_project_ai.repository.UserRepository;

import java.util.List;
import java.util.stream.Collectors;
import java.util.UUID;
import ru.urfu.online_school_project_ai.entity.enums.Role;

@Service
@RequiredArgsConstructor
public class LessonService {

    private final LessonRepository lessonRepository;
    private final TutorRepository tutorRepository;
    private final StudentRepository studentRepository;
    private final UserRepository userRepository;

    @Transactional
    public LessonResponseDto createLesson(String username, LessonCreateDto dto) {
        if (dto.scheduledAt() != null && dto.scheduledAt().isBefore(java.time.ZonedDateTime.now())) {
            throw new RuntimeException("Нельзя создать занятие в прошлом времени");
        }

        User user = userRepository.findByEmail(username);
        if (user == null) {
            throw new RuntimeException("Пользователь не найден");
        }

        Tutor tutor;
        if (user.getRole() == Role.ADMIN) {
            if (dto.tutorId() == null) {
                throw new RuntimeException("Администратор должен указать ID репетитора (tutorId)");
            }
            tutor = tutorRepository.findById(dto.tutorId())
                    .orElseThrow(() -> new RuntimeException("Указанный репетитор не найден"));
        } else {
            tutor = tutorRepository.findById(user.getId())
                    .orElseThrow(() -> new RuntimeException("Пользователь не является репетитором"));
        }

        Student student = studentRepository.findById(dto.studentId())
                .orElseThrow(() -> new RuntimeException("Ученик не найден"));

        if (!tutor.getStudents().contains(student)) {
            throw new RuntimeException("Ученик не прикреплен к этому репетитору");
        }

        long currentCount = lessonRepository.countByStudentProfileId(student.getId());

        Lesson lesson = new Lesson();
        lesson.setStudentLessonNumber((int) currentCount + 1);
        lesson.setTitle(dto.title());
        lesson.setDescription(dto.description());
        lesson.setTutorProfile(tutor);
        lesson.setStudentProfile(student);
        lesson.setScheduledAt(dto.scheduledAt());

        lesson = lessonRepository.save(lesson);

        return mapToDto(lesson);
    }

    public List<LessonResponseDto> getTutorSchedule(String username) {
        User user = userRepository.findByEmail(username);
        if (user == null) {
            throw new RuntimeException("Репетитор не найден");
        }

        Tutor tutor = tutorRepository.findById(user.getId())
                .orElseThrow(() -> new RuntimeException("Пользователь не является репетитором"));

        return lessonRepository.findAllByTutorProfileIdOrderByScheduledAtAsc(tutor.getId())
                .stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    public List<LessonResponseDto> getStudentSchedule(String username) {
        User user = userRepository.findByEmail(username);
        if (user == null) {
            throw new RuntimeException("Ученик не найден");
        }

        Student student = studentRepository.findById(user.getId())
                .orElseThrow(() -> new RuntimeException("Пользователь не является учеником"));

        return lessonRepository.findAllByStudentProfileIdOrderByScheduledAtAsc(student.getId())
                .stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    public LessonResponseDto getLessonById(String username, UUID lessonId) {
        User user = userRepository.findByEmail(username);
        if (user == null) {
            throw new RuntimeException("Пользователь не найден");
        }

        Lesson lesson = lessonRepository.findById(lessonId)
                .orElseThrow(() -> new RuntimeException("Занятие не найдено"));

        if (user.getRole() != Role.ADMIN) {
            boolean isOwner = false;
            if (user.getRole() == Role.TUTOR && lesson.getTutorProfile().getId().equals(user.getId())) {
                isOwner = true;
            } else if (user.getRole() == Role.STUDENT && lesson.getStudentProfile().getId().equals(user.getId())) {
                isOwner = true;
            }

            if (!isOwner) {
                throw new RuntimeException("У вас нет доступа к этому занятию");
            }
        }

        return mapToDto(lesson);
    }

    @Transactional
    public void deleteLesson(String username, UUID lessonId) {
        User user = userRepository.findByEmail(username);
        if (user == null) {
            throw new RuntimeException("Пользователь не найден");
        }

        Lesson lesson = lessonRepository.findById(lessonId)
                .orElseThrow(() -> new RuntimeException("Занятие не найдено"));

        if (user.getRole() != Role.ADMIN) {
            if (user.getRole() == Role.TUTOR && !lesson.getTutorProfile().getId().equals(user.getId())) {
                throw new RuntimeException("Вы можете удалять только свои занятия");
            } else if (user.getRole() == Role.STUDENT) {
                throw new RuntimeException("Ученики не могут удалять занятия");
            }
        }

        lessonRepository.delete(lesson);
    }

    @Transactional
    public LessonResponseDto updateLessonLink(String username, UUID lessonId, String meetingLink) {
        User user = userRepository.findByEmail(username);
        if (user == null) {
            throw new RuntimeException("Пользователь не найден");
        }

        Lesson lesson = lessonRepository.findById(lessonId)
                .orElseThrow(() -> new RuntimeException("Занятие не найдено"));

        if (user.getRole() != Role.ADMIN) {
            if (user.getRole() == Role.TUTOR && !lesson.getTutorProfile().getId().equals(user.getId())) {
                throw new RuntimeException("Вы можете изменять только свои занятия");
            } else if (user.getRole() == Role.STUDENT) {
                throw new RuntimeException("Ученики не могут изменять занятия");
            }
        }

        lesson.setMeeting_link(meetingLink);
        lesson = lessonRepository.save(lesson);

        return mapToDto(lesson);
    }

    private LessonResponseDto mapToDto(Lesson lesson) {
        return new LessonResponseDto(
                lesson.getId(),
                lesson.getStudentLessonNumber(),
                lesson.getTitle(),
                lesson.getDescription(),
                lesson.getTutorProfile().getId(),
                lesson.getStudentProfile().getId(),
                lesson.getMeeting_link(),
                lesson.getScheduledAt(),
                lesson.getCreatedAt()
        );
    }
}
