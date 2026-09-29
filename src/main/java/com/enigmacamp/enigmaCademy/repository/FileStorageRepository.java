package com.enigmacamp.enigmaCademy.repository;

import com.enigmacamp.enigmaCademy.entity.FileStorage;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FileStorageRepository extends JpaRepository<FileStorage, Long> {
}
