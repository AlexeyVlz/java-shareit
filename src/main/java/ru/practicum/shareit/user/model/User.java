package ru.practicum.shareit.user.model;

import lombok.Data;
import lombok.NonNull;

import javax.validation.constraints.Email;

@Data
public class User {

    Long userId;
   // @NonNull
    String name;
  //  @Email @NonNull
    String email;

    public User(Long userId, String name, String email) {
        this.userId = userId;
        this.name = name;
        this.email = email;
    }
}
