package com.enigmacamp.enigmaCademy.controller;

import com.enigmacamp.enigmaCademy.dto.request.UserRequest;
import com.enigmacamp.enigmaCademy.dto.response.CommonResponse;
import com.enigmacamp.enigmaCademy.dto.response.UserResponse;
import com.enigmacamp.enigmaCademy.model.User;
import com.enigmacamp.enigmaCademy.service.UserService;
import com.enigmacamp.enigmaCademy.utils.enums.UserRole;
import org.springframework.boot.webmvc.autoconfigure.WebMvcProperties;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    // mapper
    // mapper untuk create
    private UserResponse convertToUserResponse(User user){
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
    private List<UserResponse> convertToUserListResponse(List<User> users) {
        List<UserResponse> listOfUsers = users.stream()
                .map(this::convertToUserResponse)
                .toList();
        return  listOfUsers;
    }


    // create user baru
    @PostMapping
    public ResponseEntity<CommonResponse<UserResponse>> createUser(@RequestBody UserRequest request) {
        User newUser = new User(
                request.getName(),
                request.getEmail()
        );
        userService.saveUser(newUser);
        UserResponse userResponse = convertToUserResponse(newUser);

        CommonResponse<UserResponse> response = CommonResponse.<UserResponse>builder()
                .statusCode(HttpStatus.CREATED.value())
                .message("user sudah berhasil ditambahkan")
                .data(userResponse)
                .build();

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // get all
    @GetMapping
    public ResponseEntity<CommonResponse<List<UserResponse>>> findAllUser() {
        List<User> users = userService.findAllUser();
        CommonResponse<List<UserResponse>> response = CommonResponse.<List<UserResponse>>builder()
                .statusCode(HttpStatus.OK.value())
                .message("ok")
                .data(convertToUserListResponse(users))
                .build();

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    // find by Name
    @GetMapping("/search/by-name")
    public List<User> findByName(@RequestParam String name) {
        return userService.findByName(name);
    }

    @GetMapping("/search/by-email")
    public List<User> findByEmail(@RequestParam String email) {
        return userService.findByEmail(email);
    }

    // find by name containing some alphabet
    @GetMapping("/search")
    public List<User> findByNameContaining(@RequestParam String name) {
        return userService.findByNameContaining(name);
    }

    // count by role
    @GetMapping("/count")
    public long countByRole(@RequestParam UserRole role) {
        return userService.countUserByRole(role);
    }


}
