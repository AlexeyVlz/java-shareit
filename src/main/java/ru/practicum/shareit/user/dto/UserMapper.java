package ru.practicum.shareit.user.dto;

import ru.practicum.shareit.user.model.User;

public class UserMapper {

    public static UserDto mapUserToUserDto (User user){
        return new UserDto(user.getId(), user.getName(), user.getEmail());
    }

    public static User mapUserDtoToUser (UserDto userDto){
        return new User(userDto.getId(), userDto.getName(), userDto.getEmail());
    }
}
