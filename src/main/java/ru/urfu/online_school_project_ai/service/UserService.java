package ru.urfu.online_school_project_ai.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.urfu.online_school_project_ai.dto.UserRegistrationDto;
import ru.urfu.online_school_project_ai.entity.Student;
import ru.urfu.online_school_project_ai.entity.User;
import ru.urfu.online_school_project_ai.entity.enums.Role;
import ru.urfu.online_school_project_ai.repository.StudentRepository;
import ru.urfu.online_school_project_ai.repository.UserRepository;
import ru.urfu.online_school_project_ai.repository.TutorRepository;
import ru.urfu.online_school_project_ai.dto.UserProfileDto;
import ru.urfu.online_school_project_ai.dto.UpdateProfileDto;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final StudentRepository studentRepository;
    private final TutorRepository tutorRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public void registerUser(UserRegistrationDto dto) {

        if (userRepository.existsByEmail(dto.email())) {
            throw new IllegalArgumentException("Пользователь с таким адресом электронной почты уже существует");
        }

        User user = new User();
        user.setEmail(dto.email());
        user.setPasswordHash(passwordEncoder.encode(dto.password()));
        user.setPhone(dto.phone());
        user.setRole(Role.STUDENT); // Принудительно устанавливаем роль студента

        // Сохранение пользователя в базу данных
        user = userRepository.save(user);

        // Создаем и связываем профиль студента с созданным пользователем
        Student student = new Student();
        student.setUser(user);
        studentRepository.save(student);
    }

    @Transactional(readOnly = true)
    public UserProfileDto getUserProfile(String email) {
        User user = userRepository.findByEmail(email);
        if (user == null) {
            throw new IllegalArgumentException("Пользователь не найден");
        }

        String name = null;
        String avatar = null;

        if (user.getRole() == Role.STUDENT) {
            var s = studentRepository.findById(user.getId());
            if (s.isPresent()) {
                name = s.get().getName();
                avatar = s.get().getAvatar();
            }
        } else if (user.getRole() == Role.TUTOR) {
            var t = tutorRepository.findById(user.getId());
            if (t.isPresent()) {
                name = t.get().getName();
                avatar = t.get().getAvatar();
            }
        }

        return new UserProfileDto(
                user.getId(),
                user.getEmail(),
                user.getPhone(),
                user.getRole(),
                name,
                avatar
        );
    }

    @Transactional
    public UserProfileDto updateProfile(String email, UpdateProfileDto dto) {
        User user = userRepository.findByEmail(email);
        if (user == null) {
            throw new IllegalArgumentException("Пользователь не найден");
        }

        if (user.getRole() == Role.STUDENT) {
            var studentOpt = studentRepository.findById(user.getId());
            if (studentOpt.isPresent()) {
                var student = studentOpt.get();
                if (dto.name() != null) {student.setName(dto.name());}
                if (dto.avatar() != null) {student.setAvatar(dto.avatar());}
                studentRepository.save(student);
            }
        } else if (user.getRole() == Role.TUTOR) {
            var tutorOpt = tutorRepository.findById(user.getId());
            if (tutorOpt.isPresent()) {
                var tutor = tutorOpt.get();
                if (dto.name() != null) {tutor.setName(dto.name());}
                if (dto.name() != null) {tutor.setAvatar(dto.avatar());}
                tutorRepository.save(tutor);
            }
        }

        return getUserProfile(email);
    }
}
