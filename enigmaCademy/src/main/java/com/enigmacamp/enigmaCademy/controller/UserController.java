package com.enigmacamp.enigmaCademy.controller;

import com.enigmacamp.enigmaCademy.model.User;
import com.enigmacamp.enigmaCademy.service.UserService;
import com.enigmacamp.enigmaCademy.utils.enums.UserRole;
import org.springframework.beans.factory.annotation.Autowired;
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
    public User createUser(@RequestBody User user){
        return userService.saveUser(user);
    }

    @GetMapping
    public List<User> getAllUser(){
        return  userService.findAllUser();
    }

    @GetMapping("/search")
    public List<User> getByName(@RequestParam String name){

        return userService.findByName(name);
    }

    @GetMapping("/search-contain")
    public List<User> getByNameContaining(@RequestParam String name){

        return userService.findByNameContaining(name);
    }

    @GetMapping("/count")
    public long countByUserRole(@RequestParam UserRole role){
        return userService.countByRole(role);
    }
}
