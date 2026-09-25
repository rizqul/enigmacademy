package com.enigmacamp.enigmaCademy.service;

import org.springframework.stereotype.Service;

@Service
public class CalculatorService {

    public int sumNumbers(int a, int b) {
        return a+b;
    }

    public int substractNumbers(int a, int b) {
        return a-b;
    }
}
