package ru.urfu.online_school_project_ai.scheduler;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import ru.urfu.online_school_project_ai.entity.Lesson;
import ru.urfu.online_school_project_ai.repository.LessonRepository;

import java.time.ZonedDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
public class LessonLinkScheduler {

    private final LessonRepository lessonRepository;

    // Запускается каждую минуту
    @Scheduled(cron = "0 * * * * *")
    @Transactional
    public void generateLinksForUpcomingLessons() {
        ZonedDateTime now = ZonedDateTime.now();
        ZonedDateTime fiveMinutesLater = now.plusMinutes(5);

        // Ищем занятия, которые начинаются в ближайшие 5 минут, и у которых еще нет ссылки
        List<Lesson> upcomingLessons = lessonRepository.findLessonsWithoutLinkStartingBetween(now, fiveMinutesLater);

        for (Lesson lesson : upcomingLessons) {
            lesson.setMeeting_link("https://meet.jit.si/OnlineSchool-Lesson-" + lesson.getId());
        }

        if (!upcomingLessons.isEmpty()) {
            lessonRepository.saveAll(upcomingLessons);
        }
    }
}
