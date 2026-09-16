package com.enigmacamp.enigmaCademy.repository;

import com.enigmacamp.enigmaCademy.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> { // this will shortened the repository making cause spring already implement the CRUD method for entity model. so no more creating any method for basic crud
}
