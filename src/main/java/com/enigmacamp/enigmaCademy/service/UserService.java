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
    List<UserResponse> searchUsers(SearchUserRequest request);
    UserResponse updateById(Long id, UserRequest userRequest);
    Page<UserResponse> searchUserWithPagination(SearchUserRequest request);
}
