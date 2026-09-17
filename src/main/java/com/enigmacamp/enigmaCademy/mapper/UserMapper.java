package com.enigmacamp.enigmaCademy.mapper;

import com.enigmacamp.enigmaCademy.dto.response.UserResponse;
import com.enigmacamp.enigmaCademy.model.User;

import java.util.List;

public class UserMapper {
    // mapper untuk create
    public static UserResponse convertToUserResponse(User user){
        // 1. instansiate
        UserResponse userResponse = new UserResponse();

        // 2. set value
        userResponse.setId(user.getId());
        userResponse.setName(user.getName());
        userResponse.setEmail(user.getEmail());
        userResponse.setUserRole(user.getUserRole());
        return userResponse;
    }

    // maper untuk list
    public static List<UserResponse> convertToUserListResponse(List<User> users) {
        List<UserResponse> listOfUsers = users.stream()
                .map(UserMapper::convertToUserResponse)
                .toList();
        return  listOfUsers;
    }
}
