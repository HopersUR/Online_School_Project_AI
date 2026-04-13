package ru.urfu.online_school_project_ai.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import ru.urfu.online_school_project_ai.dto.UserRegistrationDto;
import ru.urfu.online_school_project_ai.entity.User;
import ru.urfu.online_school_project_ai.entity.enums.Role;
import ru.urfu.online_school_project_ai.repository.UserRepository;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public void registerUser(UserRegistrationDto dto) {
        if (!dto.getPassword().equals(dto.getConfirmPassword())) {
            throw new IllegalArgumentException("Пароли не совпадают");
        }

        String passwordPattern = "^(?=.*\\d)(?=.*[!#$%^&*()_@])(?=.*[a-z])(?=.*[A-Z]).*$";
        if (!dto.getPassword().matches(passwordPattern)) {
            throw new IllegalArgumentException("Пароль должен содержать хотя бы одну цифру, один специальный символ (!#$%^&*()_@), а также одну строчную и заглавную букву");
        }

        if (userRepository.existsByEmail(dto.getEmail())) {
            throw new IllegalArgumentException("Пользователь с таким адресом электронной почты уже существует");
        }

        User user = new User();
        user.setEmail(dto.getEmail());
        user.setPasswordHash(passwordEncoder.encode(dto.getPassword()));
        user.setRole(Role.STUDENT); // Принудительно устанавливаем роль студента

        // Сохранение пользователя в базу данных
        userRepository.save(user);
    }
}
