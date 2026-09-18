package com.enigmacamp.enigmaCademy.controller;

import com.enigmacamp.enigmaCademy.dto.request.SearchUserRequest;
import com.enigmacamp.enigmaCademy.dto.request.UserRequest;
import com.enigmacamp.enigmaCademy.dto.response.CommonResponse;
import com.enigmacamp.enigmaCademy.dto.response.UserResponse;
import com.enigmacamp.enigmaCademy.mapper.UserMapper;
import com.enigmacamp.enigmaCademy.model.User;
import com.enigmacamp.enigmaCademy.service.UserService;
import com.enigmacamp.enigmaCademy.utils.ResponseUtil;
import com.enigmacamp.enigmaCademy.utils.constants.ResponseMessage;
import com.enigmacamp.enigmaCademy.utils.enums.UserRole;
import org.springframework.boot.webmvc.autoconfigure.WebMvcProperties;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/v1/users")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    // create user baru
    @PostMapping
    public ResponseEntity<CommonResponse<UserResponse>> createUser(@RequestBody UserRequest request) {
        User newUser = new User(
                request.getName(),
                request.getEmail());
        UserResponse userResponse = userService.saveUser(newUser);

        return ResponseUtil.buildResponse(HttpStatus.CREATED, ResponseMessage.SUCCES_CREATE_DATA, userResponse);
    }

    // get all
    // @GetMapping
    // public ResponseEntity<CommonResponse<List<UserResponse>>> findAllUser() {
    // List<User> users = userService.findAllUser();
    // CommonResponse<List<UserResponse>> response =
    // CommonResponse.<List<UserResponse>>builder()
    // .statusCode(HttpStatus.OK.value())
    // .message("ok")
    // .data(convertToUserListResponse(users))
    // .build();
    //
    // return ResponseEntity.status(HttpStatus.OK).body(response);
    // }

    // find by Name
    // @GetMapping("/search/by-name")
    // public List<User> findByName(@RequestParam String name) {
    // return userService.findByName(name);
    // }
    //
    // @GetMapping("/search/by-email")
    // public List<User> findByEmail(@RequestParam String email) {
    // return userService.findByEmail(email);
    // }
    //
    // // find by name containing some alphabet
    // @GetMapping("/search")
    // public List<User> findByNameContaining(@RequestParam String name) {
    // return userService.findByNameContaining(name);
    // }
    //
    // // count by role
    // @GetMapping("/count")
    // public long countByRole(@RequestParam UserRole role) {
    // return userService.countUserByRole(role);
    // }

    @PostMapping("/search")
    public ResponseEntity<CommonResponse<List<UserResponse>>> searchUsers(@RequestBody SearchUserRequest request) {
        List<UserResponse> userResponses = userService.searchUsers(request);

        CommonResponse<List<UserResponse>> response = CommonResponse.<List<UserResponse>>builder()
                .statusCode(HttpStatus.OK.value())
                .message("ok")
                .data(userResponses)
                .build();

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @GetMapping("/search")
    public ResponseEntity<CommonResponse<List<UserResponse>>> searchUser(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String email,
            @RequestParam(required = false) String role,
            @RequestParam(required = false) String createdAt) {
        SearchUserRequest request = SearchUserRequest.builder()
                .name(name)
                .email(email)
                .userRole(role)
                .createdAt(createdAt)
                .build();

        List<UserResponse> userResponses = userService.searchUsers(request);

        CommonResponse<List<UserResponse>> response = CommonResponse.<List<UserResponse>>builder()
                .statusCode(HttpStatus.OK.value())
                .message("ok")
                .data(userResponses)
                .build();

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

}
