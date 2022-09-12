package ru.practicum.shareit.user.dto;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.user.model.User;

@Component
public class UserMapper {


    public static UserDto toUserDto(User user) {
        return new UserDto(user.getId(), user.getName(), user.getEmail());
    }

    public static User toUser(UserDto userDto) {
        return new User(userDto.getName(), userDto.getEmail());
    }

    public static User toUser(Long userId, UserDto userDto) {
        return new User(userId, userDto.getName(), userDto.getEmail());
    }
}
