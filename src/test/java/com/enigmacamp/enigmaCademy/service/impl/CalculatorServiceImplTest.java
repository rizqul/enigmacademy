package com.enigmacamp.enigmaCademy.service.impl;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class CalculatorServiceImplTest {
    CalculatorServiceImpl calculatorService = new CalculatorServiceImpl();

    @Test
    void sumNumbers_shouldReturnRightNumber() {
        // Arrange - mempersiapkan kebutuhan
        int a = 6;
        int b = 7;

        // Act - menjalankan method yang mau kita test
        int result = calculatorService.sumNumbers(a,b);

        // Assert - membandingkan actual result dengan expected result
        assertEquals(13, result);
    }
}
