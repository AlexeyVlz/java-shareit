package ru.practicum.shareit.user;

public class UserMapper {

    public static UserDto mapUserToUserDto (User user){
        return new UserDto(user.getUserId(), user.getName(), user.getEmail());
    }

    public static User mapUserDtoToUser (UserDto userDto){
        return new User(userDto.getUserId(), userDto.getName(), userDto.getEmail());
    }
}
