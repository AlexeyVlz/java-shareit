package ru.practicum.shareit.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.model.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class InMemoryUserRepository implements UserRepository {

    private final Map<Long, User> users = new HashMap<>();
    private final UserMapper mapper;

    @Autowired
    public InMemoryUserRepository(UserMapper mapper) {
        this.mapper = mapper;
    }

    public Map<Long, User> getUsers() {
        return users;
    }

    @Override
    public UserDto createUser(UserDto userDto) {
        User newUser = mapper.mapUserDtoToUser(userDto);
        users.put(newUser.getId(), newUser);
        return mapper.mapUserToUserDto(newUser);
    }

    @Override
    public UserDto updateUser(Long userId, UserDto userDto) {
        User updatedUser = users.get(userId);
        if (userDto.getName() != null) {
            updatedUser.setName(userDto.getName());
        }
        if (userDto.getEmail() != null) {
            updatedUser.setEmail(userDto.getEmail());
        }
        return mapper.mapUserToUserDto(updatedUser);
    }

    @Override
    public void deleteUser(Long userId) {
        users.remove(userId);
    }

    @Override
    public UserDto getUserById(Long userId) {
        return mapper.mapUserToUserDto(users.get(userId));
    }

    @Override
    public List<UserDto> getAllUsers() {
        List<UserDto> allUsers = new ArrayList<>();
        for (User user : users.values()) {
            allUsers.add(mapper.mapUserToUserDto(user));
        }
        return allUsers;
    }
}
