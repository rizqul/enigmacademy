package com.enigmacamp.enigmaCademy.service;

import com.enigmacamp.enigmaCademy.dto.request.SearchUserRequest;
import com.enigmacamp.enigmaCademy.dto.request.UserRequest;
import com.enigmacamp.enigmaCademy.dto.response.UserResponse;
import com.enigmacamp.enigmaCademy.model.User;
import com.enigmacamp.enigmaCademy.utils.enums.UserRole;
import org.springframework.data.domain.Page;

import java.util.List;

public interface UserService {
    UserResponse saveUser(UserRequest userRequest);
    List<UserResponse> findAllUser();
    UserResponse getUserById(Long id);
    List<User> findByName(String name);
    List<User> findByEmail(String email);
    List<User> findByNameContaining(String name);
    long countUserByRole(UserRole role);
    List<UserResponse> searchUsers(SearchUserRequest request);

    Page<UserResponse> searchUserWithPagination(SearchUserRequest request);
}
