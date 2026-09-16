package com.enigmacamp.enigmaCademy.controller;

import com.enigmacamp.enigmaCademy.model.User;
import com.enigmacamp.enigmaCademy.service.UserService;
import com.enigmacamp.enigmaCademy.utils.enums.UserRole;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    // create
    @PostMapping
    public User createUser(@RequestBody User user) {
        return userService.saveUser(user);
    }

    // get by id
//    @GetMapping("/{id}") // http://localhost:8080/api/users/{id}
//    public User getById(@RequestBody Long id) {
//        return ;
//    }

    // get all
    @GetMapping
    public List<User> findAllUser() {
        return userService.findAllUser();
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
