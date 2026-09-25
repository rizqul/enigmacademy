package com.enigmacamp.enigmaCademy.service.impl;

import com.enigmacamp.enigmaCademy.dto.request.UserRequest;
import com.enigmacamp.enigmaCademy.dto.response.UserResponse;
import com.enigmacamp.enigmaCademy.entity.User;
import com.enigmacamp.enigmaCademy.repository.UserRepository;
import com.enigmacamp.enigmaCademy.utils.enums.UserRole;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserServiceImpl userService;


    @Test
    void shouldCreateUserWithAdminRole() {

        // Arrange
        UserRequest request = new UserRequest(
                "Budi",
                "budi",
                "budi@gmail.com",
                "password123",
                "ADMIN"
        );

        User savedUser = new User(
                "Budi",
                "budi",
                "budi@gmail.com",
                UserRole.ADMIN,
                "password123"
        );

        savedUser.setId(1L);

        when(userRepository.save(any(User.class)))
                .thenReturn(savedUser);


        // Act
        UserResponse result = userService.saveUser(request);


        // Assert
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Budi", result.getName());
        assertEquals("budi@gmail.com", result.getEmail());
        assertEquals(UserRole.ADMIN, result.getUserRole());


        // Verify
        verify(userRepository).save(any(User.class));
    }


    @Test
    void shouldCreateUserWithDefaultStudentRoleWhenRoleIsNull() {

        // Arrange
        UserRequest request = new UserRequest(
                "Budi",
                "budi",
                "budi@gmail.com",
                "password123",
                null
        );

        User savedUser = new User(
                "Budi",
                "budi",
                "budi@gmail.com",
                UserRole.STUDENT,
                "password123"
        );

        savedUser.setId(1L);

        when(userRepository.save(any(User.class)))
                .thenReturn(savedUser);


        // Act
        UserResponse result = userService.saveUser(request);


        // Assert
        assertNotNull(result);
        assertEquals(UserRole.STUDENT, result.getUserRole());

        verify(userRepository).save(any(User.class));
    }


    @Test
    void shouldCreateUserWithDefaultStudentRoleWhenRoleIsBlank() {

        // Arrange
        UserRequest request = new UserRequest(
                "Budi",
                "budi",
                "budi@gmail.com",
                "password123",
                "   "
        );

        User savedUser = new User(
                "Budi",
                "budi",
                "budi@gmail.com",
                UserRole.STUDENT,
                "password123"
        );

        savedUser.setId(1L);

        when(userRepository.save(any(User.class)))
                .thenReturn(savedUser);


        // Act
        UserResponse result = userService.saveUser(request);


        // Assert
        assertNotNull(result);
        assertEquals(UserRole.STUDENT, result.getUserRole());

        verify(userRepository).save(any(User.class));
    }


    @Test
    void shouldThrowExceptionWhenUserRoleIsInvalid() {

        // Arrange
        UserRequest request = new UserRequest(
                "Budi",
                "budi",
                "budi@gmail.com",
                "password123",
                "SUPERADMIN"
        );


        // Act & Assert
        assertThrows(
                IllegalArgumentException.class,
                () -> userService.saveUser(request)
        );


        // Repository should never be called
        verify(userRepository, never())
                .save(any(User.class));
    }
}