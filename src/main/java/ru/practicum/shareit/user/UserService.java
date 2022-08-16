package ru.practicum.shareit.user;

import org.springframework.stereotype.Service;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public UserDto createUser(UserDto userDto) {
        return userRepository.createUser(userDto);
    }

    public UserDto updateUser(Long userId, UserDto userDto) {
        return userRepository.updateUser(userId, userDto);
    }

    public void deleteUser(Long userId) {
        userRepository.deleteUser(userId);
    }

    public UserDto getUserById(Long userId) {
        return userRepository.getUserById(userId);
    }

    public List<UserDto> getAllUsers() {
        return userRepository.getAllUsers();
    }
}
