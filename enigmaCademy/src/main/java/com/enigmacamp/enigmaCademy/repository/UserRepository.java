package com.enigmacamp.enigmaCademy.repository;

import com.enigmacamp.enigmaCademy.model.User;
import com.enigmacamp.enigmaCademy.utils.enums.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface UserRepository extends JpaRepository<User, Long> { // this will shortened the repository making cause spring already implement the CRUD method for entity model. so no more creating any method for basic crud
    List<User> findByName(String name); // the name of the method is follow the rules of spring JpaRepository, cause the logic behind this is all implemented by looking up the method name so NOTE : CAREFUL CHOOSING THE NAME
    List<User> findByNameContaining(String name);

    //custom query
    @Query("SELECT COUNT(u) FROM User u WHERE u.userRole=:role")
    long countByRole(UserRole role);
}
