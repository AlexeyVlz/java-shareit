package ru.practicum.shareit.user;

import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.ConflictDataExeption;
import ru.practicum.shareit.exception.DuplicateException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.util.List;

@Service
public class UserService {

    private final InMemoryUserRepository userRepository;

    public UserService(InMemoryUserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public UserDto createUser(UserDto userDto) {
        InMemoryUserRepository newUserRepository = (InMemoryUserRepository) userRepository;
        for (User user : newUserRepository.getUsers().values()) {
            if (user.getEmail().equals(userDto.getEmail())) {
                throw new DuplicateException("Данная почта уже используется");
            }
        }
        return userRepository.createUser(userDto);
    }

    public UserDto updateUser(Long userId, UserDto userDto) {
        //InMemoryUserRepository newUserRepository = (InMemoryUserRepository) userRepository;
        if (!userRepository.getUsers().containsKey(userId)) {
            throw new NullPointerException(String.format("Пользователь с id %d в базе отсутствует", userId));
        }
        for (User user : userRepository.getUsers().values()) {
            if (!user.getId().equals(userId) && user.getEmail().equals(userDto.getEmail())) {
                throw new ConflictDataExeption("Данная электронная почта уже зарегистрирована");
            }
        }
        return userRepository.updateUser(userId, userDto);
    }

    public void deleteUser(Long userId) {
        InMemoryUserRepository newUserRepository = (InMemoryUserRepository) userRepository;
        if (!newUserRepository.getUsers().containsKey(userId)) {
            throw new NullPointerException(String.format("Пользователь с id %d в базе отсутствует", userId));
        }
        userRepository.deleteUser(userId);
    }

    public UserDto getUserById(Long userId) {
        InMemoryUserRepository newUserRepository = (InMemoryUserRepository) userRepository;
        if (!newUserRepository.getUsers().containsKey(userId)) {
            throw new NullPointerException(String.format("Пользователь с id %d в базе отсутствует", userId));
        }
        return userRepository.getUserById(userId);
    }

    public List<UserDto> getAllUsers() {
        return userRepository.getAllUsers();
    }
}
