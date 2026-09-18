package com.enigmacamp.enigmaCademy.controller;

import com.enigmacamp.enigmaCademy.dto.request.SearchUserRequest;
import com.enigmacamp.enigmaCademy.dto.request.UserRequest;
import com.enigmacamp.enigmaCademy.dto.response.CommonResponse;
import com.enigmacamp.enigmaCademy.dto.response.UserResponse;
import com.enigmacamp.enigmaCademy.mapper.UserMapper;
import com.enigmacamp.enigmaCademy.model.User;
import com.enigmacamp.enigmaCademy.service.UserService;
import com.enigmacamp.enigmaCademy.utils.constants.ResponseMessage;
import com.enigmacamp.enigmaCademy.utils.ResponseUtil;
import com.enigmacamp.enigmaCademy.utils.enums.UserRole;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users") // to use base url for the app
public class UserController {
    private final UserService userService;

    @Autowired // annotation that automatically adding the dependency injection. this that tell spring to create the object that this constructor dependent on. this is mandatory if the constructor need more than one service if it just one It's not really necessary.
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/create") // this tell that this will perform POST method, this will concat with the @RequestMapping url so this will be : /api/users/create.
    public ResponseEntity<CommonResponse<UserResponse>> createUser(@RequestBody UserRequest request){

        User newUser = new User(request.getName(), request.getEmail());

        UserResponse newUserResponse = UserMapper.convertToUserResponse(userService.saveUser(newUser));

        return ResponseUtil.builResponse(HttpStatus.CREATED, ResponseMessage.SUCCESS_CREATE_USER, newUserResponse);
    }

    @GetMapping
    public List<User> getAllUser(){
        return  userService.findAllUser();
    }
//
//    @GetMapping("/search")
//    public List<User> getByName(@RequestParam String name){
//
//        return userService.findByName(name);
//    }

    @GetMapping("/search-contain")
    public List<User> getByNameContaining(@RequestParam String name){

        return userService.findByNameContaining(name);
    }

    @GetMapping("/count")
    public long countByUserRole(@RequestParam UserRole role){
        return userService.countByRole(role);
    }

    @GetMapping("/search")
    public ResponseEntity<CommonResponse<List<UserResponse>>> searchUser(
        @RequestParam(required = false) String name,
        @RequestParam(required = false) String email,
        @RequestParam(required = false) String role,
        @RequestParam(required = false) String createdAt
    ) {
        SearchUserRequest request = SearchUserRequest.builder()
                .name(name)
                .email(email)
                .userRole(role)
                .createdAt(createdAt)
                .build();

        List<UserResponse> userResponses = userService.searchUsers(request);

        CommonResponse<List<UserResponse>> response = CommonResponse.<List<UserResponse>>builder()
                .statusCode(HttpStatus.OK.value())
                .message("result is here")
                .data(userResponses)
                .build();

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}
