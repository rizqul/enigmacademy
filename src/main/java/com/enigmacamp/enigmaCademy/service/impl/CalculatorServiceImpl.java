package com.enigmacamp.enigmaCademy.service.impl;

import org.springframework.stereotype.Service;

@Service
public class CalculatorServiceImpl {

    public int sumNumbers(int a, int b) {
        return a+b;
    }

    public int substractNumbers(int a, int b) {
        return a-b;
    }
}
