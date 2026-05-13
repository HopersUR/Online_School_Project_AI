package ru.urfu.online_school_project_ai.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.urfu.online_school_project_ai.dto.HomeworkCreateDto;
import ru.urfu.online_school_project_ai.dto.HomeworkResponseDto;
import ru.urfu.online_school_project_ai.entity.Homework;
import ru.urfu.online_school_project_ai.entity.Lesson;
import ru.urfu.online_school_project_ai.entity.Task;
import ru.urfu.online_school_project_ai.repository.HomeworkRepository;
import ru.urfu.online_school_project_ai.repository.LessonRepository;
import ru.urfu.online_school_project_ai.repository.TaskRepository;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class HomeworkService {

    private final HomeworkRepository homeworkRepository;
    private final TaskRepository taskRepository;
    private final LessonRepository lessonRepository;

    @Transactional
    public HomeworkResponseDto createHomework(HomeworkCreateDto dto) {
        Homework homework = new Homework();

        if (dto.lessonId() != null) {
            Lesson lesson = lessonRepository.findById(dto.lessonId())
                    .orElseThrow(() -> new IllegalArgumentException("Урок не найден"));
            homework.setLesson(lesson);
        }

        homework.setTextAssignment(dto.textAssignment());
        homework.setDeadline(dto.deadline());

        if (dto.taskIds() != null && !dto.taskIds().isEmpty()) {
            List<Task> tasks = taskRepository.findAllById(dto.taskIds());
            homework.setTasks(tasks);
        }

        Homework saved = homeworkRepository.save(homework);

        return mapToDto(saved);
    }

    @Transactional(readOnly = true)
    public HomeworkResponseDto getHomeworkByLessonId(UUID lessonId) {
        Homework homework = homeworkRepository.findByLessonId(lessonId)
                .orElseThrow(() -> new IllegalArgumentException("Домашнее задание для данного урока не найдено"));
        return mapToDto(homework);
    }

    private HomeworkResponseDto mapToDto(Homework homework) {
        return new HomeworkResponseDto(
                homework.getId(),
                homework.getLesson() != null ? homework.getLesson().getId() : null,
                homework.isCompleted(),
                homework.getDeadline(),
                homework.getTextAssignment(),
                homework.getTasks() != null ? homework.getTasks().stream().map(Task::getId).toList() : List.of()
        );
    }
}