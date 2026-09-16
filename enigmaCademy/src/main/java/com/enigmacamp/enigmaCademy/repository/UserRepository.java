package com.enigmacamp.enigmaCademy.repository;

import com.enigmacamp.enigmaCademy.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
}
