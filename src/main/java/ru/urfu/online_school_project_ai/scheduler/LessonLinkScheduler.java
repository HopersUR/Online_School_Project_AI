package ru.urfu.online_school_project_ai.scheduler;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import ru.urfu.online_school_project_ai.entity.Lesson;
import ru.urfu.online_school_project_ai.entity.Notification;
import ru.urfu.online_school_project_ai.repository.LessonRepository;
import ru.urfu.online_school_project_ai.repository.NotificationRepository;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.ArrayList;

@Component
@RequiredArgsConstructor
public class LessonLinkScheduler {

    private final LessonRepository lessonRepository;
    private final NotificationRepository notificationRepository;

    // Запускается каждую минуту
    @Scheduled(cron = "0 * * * * *")
    @Transactional
    public void generateLinksForUpcomingLessons() {
        ZonedDateTime now = ZonedDateTime.now();
        ZonedDateTime fiveMinutesLater = now.plusMinutes(5);

        // Ищем занятия, которые начинаются в ближайшие 5 минут, и у которых еще нет ссылки
        List<Lesson> upcomingLessons = lessonRepository.findLessonsWithoutLinkStartingBetween(now, fiveMinutesLater);

        List<Notification> notifications = new ArrayList<>();

        for (Lesson lesson : upcomingLessons) {
            String link = "https://meet.jit.si/OnlineSchool-Lesson-" + lesson.getId();
            lesson.setMeeting_link(link);

            String notificationText = "Ваше занятие \"" + lesson.getTitle() + "\" начнется через 5 минут. Ссылка для подключения: " + link;

            // Уведомление для ученика
            if (lesson.getStudentProfile() != null && lesson.getStudentProfile().getUser() != null) {
                Notification studentNotification = new Notification();
                studentNotification.setUser(lesson.getStudentProfile().getUser());
                studentNotification.setText(notificationText);
                studentNotification.setType("LESSON_REMINDER");
                studentNotification.setIs_read(false);
                notifications.add(studentNotification);
            }

            // Уведомление для репетитора
            if (lesson.getTutorProfile() != null && lesson.getTutorProfile().getUser() != null) {
                Notification tutorNotification = new Notification();
                tutorNotification.setUser(lesson.getTutorProfile().getUser());
                tutorNotification.setText(notificationText);
                tutorNotification.setType("LESSON_REMINDER");
                tutorNotification.setIs_read(false);
                notifications.add(tutorNotification);
            }
        }

        if (!upcomingLessons.isEmpty()) {
            lessonRepository.saveAll(upcomingLessons);
            notificationRepository.saveAll(notifications);
        }
    }
}
