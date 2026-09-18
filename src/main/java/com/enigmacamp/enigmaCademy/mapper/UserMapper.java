package com.enigmacamp.enigmaCademy.mapper;


import com.enigmacamp.enigmaCademy.dto.response.UserResponse;
import com.enigmacamp.enigmaCademy.model.User;

import java.util.List;

public class UserMapper {

    // mapper to single item
    public static UserResponse convertToUserResponse(User user){
        UserResponse userResponse = UserResponse.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .userRole(user.getUserRole())
                .build();

        return userResponse;
    }

    public static List<UserResponse> convertToUserResponseList(List<User> users){

        List<UserResponse> userResponses = users.stream()
                .map(user -> convertToUserResponse(user))
                .toList();

        return userResponses;
    }
}
