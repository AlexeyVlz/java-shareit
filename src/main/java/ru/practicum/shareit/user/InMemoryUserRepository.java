package ru.practicum.shareit.user;

import org.springframework.stereotype.Component;
import org.yaml.snakeyaml.constructor.DuplicateKeyException;
import ru.practicum.shareit.exception.DuplicateException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class InMemoryUserRepository implements UserRepository {

    Long userId = 0L;
    Map<Long, User> users = new HashMap<>();

    @Override
    public UserDto createUser(UserDto userDto) {
        for (User user : users.values()) {
            if (user.getEmail().equals(userDto.getEmail())) {
                throw new DuplicateException("Данная почта уже используется");
            }
        }
        userDto.setUserId(++userId);
        User newUser = UserMapper.mapUserDtoToUser(userDto);
        users.put(newUser.getUserId(), newUser);
        return UserMapper.mapUserToUserDto(newUser);
    }

    @Override
    public UserDto updateUser(Long userId, UserDto userDto) {
        if(!users.containsKey(userId)){
            throw new NullPointerException(String.format("Пользователь с id %d в базе отсутствует", userId));
        }
        User updatedUser = users.get(userId);
        if(userDto.getName() != null){
           updatedUser.setName(userDto.getName());
        }
        if(userDto.getEmail() != null){
            updatedUser.setEmail(userDto.getEmail());
        }
        return UserMapper.mapUserToUserDto(updatedUser);
    }

    @Override
    public void deleteUser(Long userId) {
        if(!users.containsKey(userId)){
            throw new NullPointerException(String.format("Пользователь с id %d в базе отсутствует", userId));
        }
        users.remove(userId);
    }

    @Override
    public UserDto getUserById(Long userId) {
        if(!users.containsKey(userId)){
            throw new NullPointerException(String.format("Пользователь с id %d в базе отсутствует", userId));
        }
        return UserMapper.mapUserToUserDto(users.get(userId));
    }

    @Override
    public List<UserDto> getAllUsers() {
        List<UserDto> allUsers = new ArrayList<>();
        for(User user : users.values()){
            allUsers.add(UserMapper.mapUserToUserDto(user));
        }
        return allUsers;
    }
}
