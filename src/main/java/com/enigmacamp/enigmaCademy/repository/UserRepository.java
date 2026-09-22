package com.enigmacamp.enigmaCademy.repository;

import com.enigmacamp.enigmaCademy.model.User;
import com.enigmacamp.enigmaCademy.utils.enums.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface UserRepository extends JpaRepository<User, Long>, JpaSpecificationExecutor<User> {
    List<User> findByName(String name);
    List<User> findByEmail(String email);
    List<User> findByNameContaining(String name);
    User findByUsername(String username);

    @Query("SELECT COUNT(u) FROM User u WHERE u.userRole=:role")
    long countByRole(UserRole role);
}
