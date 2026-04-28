package ru.urfu.online_school_project_ai;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Главный класс приложения для онлайн-школы с искусственным интеллектом.
 * Этот класс запускает Spring Boot приложение.
 */
@SpringBootApplication
@EnableScheduling
public class OnlineSchoolProjectAiApplication {

    public static void main(String[] args) {
        SpringApplication.run(OnlineSchoolProjectAiApplication.class, args);
    }
}
