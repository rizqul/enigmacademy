package com.enigmacamp.enigmaCademy.controller;

import com.enigmacamp.enigmaCademy.dto.request.SearchUserRequest;
import com.enigmacamp.enigmaCademy.dto.request.UserRequest;
import com.enigmacamp.enigmaCademy.dto.response.CommonResponse;
import com.enigmacamp.enigmaCademy.dto.response.UserResponse;
import com.enigmacamp.enigmaCademy.service.UserService;
import com.enigmacamp.enigmaCademy.utils.ResponseUtil;
import com.enigmacamp.enigmaCademy.utils.constants.ResponseMessage;
import com.enigmacamp.enigmaCademy.utils.validators.ValidationGroups;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

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
    public ResponseEntity<CommonResponse<UserResponse>> createUser(
            @Validated(ValidationGroups.OnCreate.class)
            @RequestBody UserRequest request) {
        UserResponse userResponse = userService.saveUser(request);

        return ResponseUtil.buildResponse(HttpStatus.CREATED, ResponseMessage.SUCCES_CREATE_DATA, userResponse);
    }

     // get all
     @GetMapping
     public ResponseEntity<CommonResponse<List<UserResponse>>> findAllUser() {
        List<UserResponse> users = userService.findAllUser();

        return ResponseUtil.buildResponse(HttpStatus.OK, ResponseMessage.SUCCES_GET_DATA, users);
     }

     // get by id
     @GetMapping("/{id}")
     public ResponseEntity<CommonResponse<UserResponse>> getUserById(@PathVariable Long id) {
        UserResponse userResponse = userService.getUserById(id);
        return ResponseUtil.buildResponse(HttpStatus.OK, ResponseMessage.SUCCES_GET_DATA, userResponse);
    }

    //update by id
    @PutMapping("/{id}")
    public ResponseEntity<CommonResponse<UserResponse>> updateById(@PathVariable Long id, @Validated(ValidationGroups.OnUpdate.class) @RequestBody UserRequest userRequest) {
        UserResponse userResponse = userService.updateById(id, userRequest);

        return ResponseUtil.buildResponse(HttpStatus.OK, ResponseMessage.SUCCES_UPDATE_DATA, userResponse);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<CommonResponse<UserResponse>> deleteById(@PathVariable Long id) {
        UserResponse userResponse = userService.deleteById(id);

        return ResponseUtil.buildResponse(HttpStatus.OK, ResponseMessage.SUCCES_DELETE_DATA, userResponse);
    }

    @DeleteMapping("/soft-delete/{id}")
    public ResponseEntity<CommonResponse<UserResponse>> softDeleteById(@PathVariable Long id) {
        UserResponse userResponse = userService.softDeleteById(id);

        return ResponseUtil.buildResponse(HttpStatus.OK, ResponseMessage.SUCCES_DELETE_DATA, userResponse);
    }

    @PostMapping("/search")
    public ResponseEntity<CommonResponse<List<UserResponse>>> searchUsers(@RequestBody SearchUserRequest request) {
        List<UserResponse> userResponses = userService.searchUsers(request);

        return ResponseUtil.buildResponse(HttpStatus.OK, ResponseMessage.SUCCES_GET_DATA, userResponses);
    }

    @GetMapping("/search")
    public ResponseEntity<CommonResponse<List<UserResponse>>> searchUser(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String username,
            @RequestParam(required = false) String email,
            @RequestParam(required = false) String role,
            @RequestParam(required = false) String createdAt) {
        SearchUserRequest request = SearchUserRequest.builder()
                .name(name)
                .username(username)
                .email(email)
                .userRole(role)
                .createdAt(createdAt)
                .build();

        List<UserResponse> userResponses = userService.searchUsers(request);

        return ResponseUtil.buildResponse(HttpStatus.OK, ResponseMessage.SUCCES_GET_DATA, userResponses);
    }

    @GetMapping("/advanced-search/page")
    public ResponseEntity<CommonResponse<List<UserResponse>>> searchWithPaging(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String username,
            @RequestParam(required = false) String email,
            @RequestParam(required = false, defaultValue = "STUDENT") String role,
            @RequestParam(required = false) String createdAt,
//            @RequestParam(defaultValue = "1") Integer page,
//            @RequestParam(defaultValue = "10") Integer size,
//            @RequestParam(defaultValue = "name") String sortBy,
//            @RequestParam(defaultValue = "asc") String direction
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size,
            @RequestParam(required = false) String sortBy,
            @RequestParam(required = false) String direction
            ) {
        SearchUserRequest request = SearchUserRequest.builder()
                .name(name)
                .username(username)
                .email(email)
                .userRole(role)
                .createdAt(createdAt)
                .page(page)
                .size(size)
                .sortBy(sortBy)
                .direction(direction)
                .build();

        Page<UserResponse> userResponses = userService.searchUserWithPagination(request);

        return ResponseUtil.buildResponse(
                HttpStatus.OK,
                ResponseMessage.SUCCES_GET_DATA,
                userResponses.getContent(),
                userResponses);
    }
}
