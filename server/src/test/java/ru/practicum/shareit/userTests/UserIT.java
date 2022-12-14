package ru.practicum.shareit.userTests;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import ru.practicum.shareit.ObjectsForTests;
import ru.practicum.shareit.ShareItServer;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.UserServiceImpl;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.transaction.Transactional;

@Transactional
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@SpringJUnitConfig({ShareItServer.class, UserServiceImpl.class})
public class UserIT {

    private final EntityManager em;
    private final UserService userService;


    @Test
    void getUserById() {
        User user = ObjectsForTests.getUser1();
        UserDto userDto = ObjectsForTests.getUserDto1();
        userDto.setId(null);
        userService.createUser(userDto);

        TypedQuery<User> query = em.createQuery("Select u from User u where u.id = :id", User.class);
        User queryUser = query
                .setParameter("id", 1L)
                .getSingleResult();
        Assertions.assertEquals(user, queryUser);
    }
}
