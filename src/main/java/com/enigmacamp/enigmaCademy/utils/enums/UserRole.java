package com.enigmacamp.enigmaCademy.utils.enums;

public enum UserRole {
    ADMIN,
    INSTRUCTOR,
    STUDENT;

    public static boolean isValid(String value) {
        if (value == null) {
            return false;
        }

        for (UserRole userRole : UserRole.values()) {
            if (userRole.name().equals(value)) {
                return true;
            }
        }

        return false;
    }
}
