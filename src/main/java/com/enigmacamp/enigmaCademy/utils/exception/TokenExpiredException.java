package com.enigmacamp.enigmaCademy.utils.exception;

public class TokenExpiredException extends RuntimeException{
    public TokenExpiredException(String message) {
        super(message);
    }
}
