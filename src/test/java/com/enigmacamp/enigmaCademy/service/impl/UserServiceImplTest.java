package com.enigmacamp.enigmaCademy.service.impl;

import com.enigmacamp.enigmaCademy.dto.request.UserRequest;
import com.enigmacamp.enigmaCademy.dto.response.UserResponse;
import com.enigmacamp.enigmaCademy.entity.User;
import com.enigmacamp.enigmaCademy.mapper.UserMapper;
import com.enigmacamp.enigmaCademy.repository.UserRepository;
import com.enigmacamp.enigmaCademy.utils.enums.UserRole;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Optional;

@ExtendWith(MockitoExtension.class)
public class UserServiceImplTest {

    @Mock
    UserRepository userRepository;

    @InjectMocks
    UserServiceImpl userService;

    @Test
    void getUserById_shouldReturnCorrectUser() {
        // arrange - tidak membutuhkan apa2
        Long id = 1L;

        User user = new User(
                "Ipan",
                "ipan_unyu",
                "ipan@student.enigmacademy.com",
                UserRole.STUDENT,
                "ipan123"
        );

        // meniru behaviour dari findById yang ada di repo
        // biar service test terisolir, tidak dependen terhadap repo
        Mockito.when(userRepository.findById(id)).thenReturn(Optional.of(user));

        // act
        UserResponse userResponse = userService.getUserById(id);

        // assert
        assertNotNull(userResponse);
        assertEquals(UserMapper.convertToUserResponse(user), userResponse);
        assertEquals(user.getName(), userResponse.getName());
    }

    @Test
    void saveUser_shouldSaveUserToDatabase() {
        // arrange
        UserRequest userRequest = new UserRequest(
                "Ipan",
                "ipan_unyu",
                "ipan@student.enigmacademy.com",
                "ipan123",
                "STUDENT"
        );

        User newUser = new User(
                "Ipan",
                "ipan_unyu",
                "ipan@student.enigmacademy.com",
                UserRole.STUDENT,
                "ipan123"
        );

        User storedUser = new User(
                1L,
                "Ipan",
                "ipan_unyu",
                "ipan@student.enigmacademy.com",
                UserRole.STUDENT,
                "ipan123"
        );

        newUser.setId(1L);

        // meniru behaviour dari userRepository.save()
        Mockito.when(userRepository.save(ArgumentMatchers.any())).thenReturn(newUser);

        // act
        UserResponse savedUser = userService.saveUser(userRequest);

        // assert
        assertNotNull(savedUser);
        assertEquals(UserMapper.convertToUserResponse(newUser), savedUser);
        assertEquals(UserMapper.convertToUserResponse(newUser).getName(), savedUser.getName());
    }

}
