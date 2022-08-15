package ru.practicum.shareit.user;

import lombok.Data;
import lombok.NonNull;

import javax.validation.constraints.Email;

@Data
public class UserDto {

        Long userId;
       // @NonNull
        String name;
       // @Email @NonNull
        String email;

    public UserDto(Long userId, String name, String email) {
        this.userId = userId;
        this.name = name;
        this.email = email;
    }
}

