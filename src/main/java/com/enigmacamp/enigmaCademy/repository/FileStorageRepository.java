package com.enigmacamp.enigmaCademy.repository;

import com.enigmacamp.enigmaCademy.entity.FileStorage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface FileStorageRepository extends JpaRepository<FileStorage, Long> {
    Optional<FileStorage> findById(Long id);
}
