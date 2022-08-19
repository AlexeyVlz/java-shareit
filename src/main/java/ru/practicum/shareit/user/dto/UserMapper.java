package ru.practicum.shareit.user.dto;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.user.model.User;

@Component
public class UserMapper {

    private Long generateId = 0L;

    public UserDto mapUserToUserDto(User user) {
        return new UserDto(user.getId(), user.getName(), user.getEmail());
    }

    public User mapUserDtoToUser(UserDto userDto) {
        return new User(++generateId, userDto.getName(), userDto.getEmail());
    }
}
