package com.enigmacamp.enigmaCademy.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class Greetings {
    @GetMapping("/")
    public String welcome() {
        return "Selamat datang di EnigmaCademy!";
    }

    @GetMapping("/about")
    public String about() {
        return "EnigmaCademy adalah sebuah Learning Management System";
    }
}
