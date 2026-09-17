package com.enigmacamp.enigmaCademy.service;

import com.enigmacamp.enigmaCademy.dto.request.SearchUserRequest;
import com.enigmacamp.enigmaCademy.dto.request.UserRequest;
import com.enigmacamp.enigmaCademy.dto.response.UserResponse;
import com.enigmacamp.enigmaCademy.model.User;
import com.enigmacamp.enigmaCademy.utils.enums.UserRole;

import java.util.List;

public interface UserService {
    User saveUser(User user);
    List<User> findAllUser();
    List<User> findByName(String name);
    List<User> findByEmail(String email);
    List<User> findByNameContaining(String name);
    long countUserByRole(UserRole role);
    List<UserResponse> searchUsers(SearchUserRequest request);
}
