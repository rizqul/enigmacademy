package com.enigmacamp.enigmaCademy.repository;

import com.enigmacamp.enigmaCademy.model.User;
import com.enigmacamp.enigmaCademy.utils.enums.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.lang.classfile.Opcode;
import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long>, JpaSpecificationExecutor<User> {
    Optional<User> findByUsername(String username);
    boolean existsByUsername(String username);

    @Query("SELECT COUNT(u) FROM User u WHERE u.userRole=:role")
    long countByRole(UserRole role);
}
