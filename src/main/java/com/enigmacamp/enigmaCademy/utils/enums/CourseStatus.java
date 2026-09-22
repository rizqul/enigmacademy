package com.enigmacamp.enigmaCademy.utils.enums;

public enum CourseStatus {
    DRAFT,
    PUBLISHED,
    ARCHIVED;

    public static boolean isValid(String value) {
        if (value == null) {
            return false;
        }

        for (CourseStatus courseStatus : CourseStatus.values()) {
            if (courseStatus.name().equals(value)) {
                return true;
            }
        }

        return false;
    }
}
